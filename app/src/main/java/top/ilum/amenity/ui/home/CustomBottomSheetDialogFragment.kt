package top.ilum.amenity.ui.home

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet.*
import top.ilum.amenity.R

class CustomBottomSheetDialogFragment : BottomSheetDialogFragment() {

    companion object {
        var item = -1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.bottom_sheet, container, false)

    private val mBottomSheetBehaviorCallback: BottomSheetCallback = object : BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(context, R.layout.bottom_sheet, null)
        dialog.setContentView(contentView)
        val layoutParams =
            (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = layoutParams.behavior
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
        }
    }

    override fun onStart() {
        super.onStart()
        txt_bench.setOnClickListener {
            item = 0
            dismiss()
        }
        txt_fence.setOnClickListener {
            item = 1
            dismiss()
        }
        txt_streetlight.setOnClickListener {
            item = 2
            dismiss()
        }
        txt_tree.setOnClickListener {
            item = 3
            dismiss()
        }
        txt_flower.setOnClickListener {
            item = 4
            dismiss()
        }
        txt_parking.setOnClickListener {
            item = 5
            dismiss()
        }
        txt_custom.setOnClickListener {
            item = 6
            dismiss()
        }
    }

    fun getItem(): Int = item
}