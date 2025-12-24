package com.joron.waffle.drivehistory.presentation.dialog

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

open class AppCommonDialogFragment : DialogFragment() {

    /**
     * ダイアログ表示しようとしたときにアプリがバックグラウンドに行ってしまっているときの
     * エラーをもみ消すダイアログ表示
     */
    override fun show(manager: FragmentManager, tag: String?) {
        try {
            super.show(manager, tag)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    /**
     * ダイアログ消そうとしたときにアプリがバックグラウンドに行ってしまっているときの
     * エラーをもみ消すダイアログ表示
     */
    override fun dismissAllowingStateLoss() {
        try {
            super.dismissAllowingStateLoss()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    override fun dismiss() {
        super.dismiss()
    }

    fun closeSkb(editText: EditText) {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(
            editText.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
        editText.clearFocus()
    }
}