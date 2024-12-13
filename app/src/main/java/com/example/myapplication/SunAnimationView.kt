package com.example.myapplication

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.os.Handler
import android.os.Looper

class SunAnimationView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private var sunPositionY: Float = 0f
    private val sunRadius = 100f
    private val paint = Paint().apply {
        color = resources.getColor(android.R.color.holo_orange_light) // Color del sol
        isAntiAlias = true
    }
    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval: Long = 1000 // 1 segundo en milisegundos

    init {
        // Inicia la posición inicial del sol (debajo de la pantalla)
        sunPositionY = height.toFloat() + sunRadius
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Dibuja el sol en la posición Y actual
        canvas?.drawCircle(width / 2f, sunPositionY, sunRadius, paint)

        // Llama al método de actualización de la posición
        updateSunPosition()
    }

    private fun updateSunPosition() {
        // Actualiza la posición Y del sol según la hora del día
        val currentTime = System.currentTimeMillis()
        val dayDuration = 24 * 60 * 60 * 1000 // 24 horas en milisegundos

        // Calcula la posición en función del tiempo transcurrido en el día
        sunPositionY = ((currentTime % dayDuration) / dayDuration.toFloat()) * height.toFloat()

        // Invalida la vista para que se redibuje
        invalidate()

        // Refresca la vista cada segundo
        handler.postDelayed({ invalidate() }, updateInterval)
    }
}
