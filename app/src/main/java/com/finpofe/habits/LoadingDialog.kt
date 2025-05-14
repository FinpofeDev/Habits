package com.finpofe.habits

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.airbnb.lottie.LottieAnimationView
import androidx.core.graphics.drawable.toDrawable

class LoadingDialog(context: Context) : Dialog(context) {

    init {
        setCancelable(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_loading)

        window?.apply {
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            decorView.setPadding(0, 0, 0, 0)

        }

        val lottieView = findViewById<LottieAnimationView>(R.id.animationView)
        val displayMetrics = context.resources.displayMetrics
        val size = (displayMetrics.widthPixels * 0.3).toInt() // 30% del ancho de pantalla
        lottieView.layoutParams.width = size
        lottieView.layoutParams.height = size
    }
}