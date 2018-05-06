package com.example.rotatednotesview

/**
 * Created by anweshmishra on 06/05/18.
 */

import android.app.Activity
import android.graphics.*
import android.view.View
import android.view.MotionEvent
import android.content.Context

class RotatedNotesView (ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val renderer : Renderer = Renderer(this)

    private var onRotatedListener : OnRotatedListener? = null

    fun addOnRotateListener(onRotate : () -> Unit) {
        onRotatedListener = OnRotatedListener(onRotate)
    }

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State (var prevScale : Float = 0f, var dir : Float = 0f, var j : Int = 0, var delay : Int = 0) {

        val scales : Array<Float> = arrayOf(0f, 0f, 0f)

        val MAX_DELAY = 6
        fun update(stopcb : (Float) -> Unit) {
            if (delay == 0) {
                scales[j] += dir * 0.1f
                if (Math.abs(scales[j] - prevScale) > 1) {
                    scales[j] = prevScale + dir
                    delay++
                }
            }
            else {
                delay++
                if (delay == MAX_DELAY) {
                    delay = 0
                    j += dir.toInt()
                    if (j == scales.size || j == -1) {
                        j -= dir.toInt()
                        dir = 0f
                        prevScale = scales[j]
                        stopcb(prevScale)
                    }
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
            val size : Float = 2 * Math.min(w, h) / 3
            val barH : Float = Math.min(w,h) / 9
            val deg : Float = -15f
            canvas.save()
            canvas.translate(w / 2, h / 2)
            canvas.rotate(deg * state.scales[2])
            paint.color = Color.WHITE
            canvas.drawRect(-size/2, -size/2, size/2, size/2, paint)
            paint.color = Color.parseColor("#FFCA28")
            canvas.save()
            canvas.translate(-size/2, -size/2)
            canvas.drawRect(0f, 0f, size * state.scales[0], barH, paint)
            paint.color = Color.parseColor("#212121")
            paint.strokeWidth = Math.min(w, h) / 60
            paint.strokeCap = Paint.Cap.ROUND
            val hSize : Float = (size - barH) / 10
            for (i in 1..9) {
                canvas.save()
                canvas.translate(0f, barH + hSize * i)
                canvas.drawLine(0f, 0f, size * state.scales[1], 0f, paint)
                canvas.restore()
            }
            canvas.restore()
            canvas.restore()
        }
    }

    data class Renderer (var view : RotatedNotesView) {

        private val animator : Animator = Animator(view)

        private val rotatedNotes : RotatedNotes = RotatedNotes(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#212121"))
            rotatedNotes.draw(canvas, paint)
            animator.animate {
                rotatedNotes.update {
                    animator.stop()
                    when (it) {
                        1f -> view.onRotatedListener?.onRotate?.invoke()
                    }
                }
            }
        }

        fun handleTap() {
            rotatedNotes.startUpdating {
                animator.start()
            }
        }
    }

    companion object {
        fun create(activity : Activity) : RotatedNotesView {
            val view : RotatedNotesView = RotatedNotesView(activity)
            activity.setContentView(view)
            return view
        }
    }

    data class OnRotatedListener(var onRotate : () -> Unit)
}