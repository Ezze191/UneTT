package com.example.uttmovil

import android.content.Context
import androidx.appcompat.app.AlertDialog

object utiles {
    fun showerror(context: Context,msg: String?) {
        AlertDialog.Builder(context).apply {
            setTitle("Error")
            setMessage(msg)
            setPositiveButton("OK", null)
        }.show()

    }

}