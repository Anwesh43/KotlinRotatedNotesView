package com.example.rotatednotesview

/**
 * Created by anweshmishra on 06/05/18.
 */

import android.graphics.*
import android.view.View
import android.view.MotionEvent
import android.content.Context

class RotatedNotesView (ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class State (var prevScale : Float = 0f, var dir : Float = 0f, var j : Int = 0) {

        val scales : Array<Float> = arrayOf(0f, 0f, 0f)

        fun update(stopcb : (Float) -> Unit) {
            scales[j] += dir * 0.1f
            if (Math.abs(scales[j] - prevScale) > 1) {
                scales[j] = prevScale + dir
                j += dir.toInt()
                if (j == scales.size || j == -1) {
                    j -= dir.toInt()
                    dir = 0f
                    prevScale = scales[j]
                    stopcb(prevScale)
                }
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            if (dir == 0f) {
                dir = 1 - 2 * prevScale
                startcb()
            }
        }
    }

    data class Animator (var view : RotatedNotesView, var animated : Boolean = false) {

        fun animate(updatecb : () -> Unit) {
            if (animated) {
                updatecb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                } catch (ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }

    }

    data class RotatedNotes(var i : Int, val state : State = State()) {

        fun update(stopcb : (Float) -> Unit) {
            state.update(stopcb)
        }

        fun startUpdating(startcb : () -> Unit) {
            state.startUpdating(startcb)
        }

        fun draw(canvas : Canvas, paint : Paint) {
            val w : Float = canvas.width.toFloat()
            val h : Float = canvas.height.toFloat()
            val size : Float = 2 * Math.min(w, h)/3
            val barH : Float = Math.min(w,h)/15
            canvas.save()
            canvas.translate(w / 2, h / 2)
            canvas.rotate(-30f * state.scales[2])
            paint.color = Color.WHITE
            canvas.drawRect(-size/2, -size/2, size/2, size/2, paint)
            paint.color = Color.parseColor("#FFCA28")
            canvas.save()
            canvas.translate(-size/2, -size/2)
            canvas.drawRect(0f, 0f, w * state.scales[0], barH, paint)
            val hSize : Float = h / 10
            for (i in 1..9) {
                canvas.save()
                canvas.translate(0f, hSize * i)
                canvas.drawLine(0f, 0f, w * state.scales[1], 0f, paint)
                canvas.restore()
            }
            canvas.restore()
            canvas.restore()
        }
    }
}