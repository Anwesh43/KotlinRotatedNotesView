package com.example.anweshmishra.kotlinrotatednotesview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.rotatednotesview.RotatedNotesView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RotatedNotesView.create(this)
    }
}
