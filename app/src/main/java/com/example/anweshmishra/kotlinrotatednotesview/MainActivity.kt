package com.example.anweshmishra.kotlinrotatednotesview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import com.example.rotatednotesview.RotatedNotesView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view : RotatedNotesView = RotatedNotesView.create(this)
        fullScreen()
        view.addOnRotateListener {
            Toast.makeText(this, "Rotation Complete", Toast.LENGTH_LONG).show()
        }
    }
}

fun MainActivity.fullScreen() {
    supportActionBar?.hide()
    window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
}
