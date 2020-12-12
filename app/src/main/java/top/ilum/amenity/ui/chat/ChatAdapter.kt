package top.ilum.amenity.ui.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import top.ilum.amenity.R
import top.ilum.amenity.data.Event

class ChatAdapter(val context: Context, private val messages: ArrayList<Event>) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == 1) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.chat_status, parent, false)
            ViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.chat_message, parent, false)
            ViewHolder(view)
        }
    }

    override fun getItemCount(): Int = messages.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = messages[position]
        if (event.type == 1) {
            holder.statusMsg.text = event.msg
        } else {
            holder.msgName.text = event.name
            holder.msgContent.text = event.msg
        }
    }

    override fun getItemViewType(position: Int): Int {
        return messages[position].type
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val statusMsg = itemView.findViewById<TextView>(R.id.status_msg)
        val msgName = itemView.findViewById<TextView>(R.id.chat_name)
        val msgContent = itemView.findViewById<TextView>(R.id.chat_msg)
    }

}