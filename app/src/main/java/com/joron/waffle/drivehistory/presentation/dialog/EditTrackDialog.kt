package com.joron.waffle.drivehistory.presentation.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.joron.waffle.drivehistory.R
import com.joron.waffle.drivehistory.databinding.EditTrackDialogBinding

class EditTrackDialog : AppCommonDialogFragment() {
    private var okListener: ((String) -> Unit)? = null
    private var neuListener: (() -> Unit)? = null
    private var negListener: (() -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val layout = EditTrackDialogBinding.inflate(
            LayoutInflater.from(context),
            null,
            false,
        )
        layout.editTrackDialogButtonNegative.setOnClickListener {
            dismissAllowingStateLoss()
            negListener?.invoke()
        }
        layout.editTrackDialogButtonPositive.setOnClickListener {
            dismissAllowingStateLoss()
            val editTitle = layout.editTrackTitle.text?.toString() ?: ""
            okListener?.invoke(editTitle)
        }
        val builder = AlertDialog.Builder(requireActivity(), R.style.AppCommonDialogStyle)
        return builder.setView(layout.root).create()
    }

    /**
     * Positiveボタン押下時のリスナーをセット
     *
     * @param okListener Positiveボタン押下時のリスナー
     */
    fun setOkListener(okListener: ((String) -> Unit)?) {
        this.okListener = okListener
    }

    /**
     * Neutralボタン押下時のリスナーをセット
     *
     * @param neuListener Neutralボタン押下時のリスナー
     */
    fun setNeuListener(neuListener: (() -> Unit)?) {
        this.neuListener = neuListener
    }

    /**
     * Negativeボタン押下時のリスナーをセット
     *
     * @param negListener Negativeボタン押下時のリスナー
     */
    fun setNegListener(negListener: (() -> Unit)?) {
        this.negListener = negListener
    }
}