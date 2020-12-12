package top.ilum.amenity.ui.chat

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Manager
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import top.ilum.amenity.R
import top.ilum.amenity.data.Event
import top.ilum.amenity.data.Message
import top.ilum.amenity.data.SocketData
import java.net.URI

class ChatFragment : Fragment() {
    lateinit var chatSocket: Socket
    lateinit var errorSocket: Socket
    lateinit var chatAdapter: ChatAdapter
    lateinit var chatRecycler: RecyclerView
    lateinit var sendMsgButton: Button
    lateinit var chatMsgText: EditText
    private val gson: Gson = Gson()
    private var list: ArrayList<Event> = arrayListOf()
    private var room = SharedPrefs.room

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_chat, container, false)

        chatRecycler = root.findViewById<RecyclerView>(R.id.chat_recycler)
        chatAdapter = ChatAdapter(requireContext(), list)
        chatRecycler.adapter = chatAdapter
        val layoutManager = LinearLayoutManager(requireContext())
        chatRecycler.layoutManager = layoutManager

        sendMsgButton = root.findViewById(R.id.send_message_btn)
        chatMsgText = root.findViewById(R.id.chat_input)
        if (room == null) {
            sendMsgButton.isEnabled = false
            updateData(Event(1, "Пожалуйста, выберите ваш дом в настройках"))
        }

        sendMsgButton.setOnClickListener {
            if (TextUtils.isEmpty(chatMsgText.text.toString())) {
                chatMsgText.setError("Введите сообщение!")
            } else {
                chatSocket.emit(
                    "text",
                    gson.toJson(room?.let { it1 ->
                        SocketData(
                            it1,
                            token,
                            chatMsgText.text.toString()
                        )
                    })
                )
                chatMsgText.text.clear()
            }
        }

        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        try {
            val manager = Manager(URI("http://10.0.2.2:5000"))
            chatSocket = manager.socket("/chat")
            errorSocket = manager.socket("/errors")
        } catch (e: Exception) {
        }


        /**
         * Connect to chat/error socket and register all event listeners / emitters
         */
        chatSocket.connect() // Chat
        chatSocket.on(Socket.EVENT_CONNECT, onConnect)
        chatSocket.on("status", status)
        chatSocket.on("message", message)

        errorSocket.connect() // Errors
        errorSocket.on("error", handleError)


    }

    /**
     * LISTENERS / EMITTERS
     */
    private val token: String = SharedPrefs.token as String

    private val status = Emitter.Listener {
        val msg = gson.fromJson(it[0].toString(), Message::class.java)
        updateData(Event(1, msg.msg))
    }

    private val message = Emitter.Listener {
        val msg = gson.fromJson(it[0].toString(), Message::class.java)
        updateData(Event(2, msg.msg, msg.name))

    }

    private var onConnect = Emitter.Listener { //Sent on connection
        chatSocket.emit("joined", gson.toJson(room?.let { it1 -> SocketData(it1, token) }))

    }

    private var handleError = Emitter.Listener {
        //TODO token refresh
    }

    private fun updateData(event: Event) {
        activity?.runOnUiThread {
            list.add(event)
            chatAdapter.notifyItemInserted(list.size)
            chatRecycler.scrollToPosition(list.size - 1)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
    }

    override fun onDestroy() {
        super.onDestroy()
        chatSocket.emit("left", gson.toJson((room?.let { SocketData(it, token) })))
    }
}