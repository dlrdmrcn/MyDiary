package com.dilara.mydiary.base

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class BaseActivity : AppCompatActivity() {

    fun showPopUp(
        title: String,
        message: String,
        positiveButtonName: String,
        positiveButtonCallBack: (() -> Unit)? = null,
        negativeButtonName: String? = null,
        negativeButtonCallBack: (() -> Unit)? = null,
        neutralButtonName: String? = null,
        neutralButtonCallBack: (() -> Unit)? = null
    ) {
        val builder = AlertDialog.Builder(this@BaseActivity)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(positiveButtonName) { dialog, _ ->
            positiveButtonCallBack?.invoke()
            dialog.cancel()
        }

        if (!negativeButtonName.isNullOrEmpty()) {
            builder.setNegativeButton(negativeButtonName) { dialog, _ ->
                negativeButtonCallBack?.invoke()
                dialog.cancel()
            }
        }

        if (!neutralButtonName.isNullOrEmpty()) {
            builder.setNeutralButton(neutralButtonName) { dialog, _ ->
                neutralButtonCallBack?.invoke()
                dialog.cancel()
            }
        }
        builder.create().show()
    }
}