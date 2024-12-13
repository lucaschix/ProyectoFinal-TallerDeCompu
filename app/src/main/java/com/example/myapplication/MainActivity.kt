package com.example.myapplication

import SensorDataAdapter
import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.echo.holographlibrary.Bar
import com.echo.holographlibrary.BarGraph
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnGetData: Button
    private lateinit var barGraph: BarGraph
    private lateinit var tvTemperatura: TextView
    private lateinit var tvHumedad: TextView
    private lateinit var tvEstadoAmbiente: TextView
    private lateinit var sunAnimationView: SunAnimationView
    private lateinit var mainLayout: ConstraintLayout
    private lateinit var cloud1: ImageView
    private lateinit var cloud2: ImageView
    private val handler = Handler()
    private val updateInterval: Long = 1000 // 1 segundo en milisegundos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicialización de vistas
        recyclerView = findViewById(R.id.recycler_view)
        btnGetData = findViewById(R.id.btn_get_data)
        barGraph = findViewById(R.id.bar_graph)
        tvTemperatura = findViewById(R.id.tv_temperatura)
        tvHumedad = findViewById(R.id.tv_humedad)
        sunAnimationView = findViewById(R.id.sun_animation_view)
        tvEstadoAmbiente = findViewById(R.id.tv_estado_ambiente)
        mainLayout = findViewById(R.id.main_layout)

        // Inicialización de las nubes
        cloud1 = findViewById(R.id.cloud1)
        cloud2 = findViewById(R.id.cloud2)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // Acción del botón para obtener datos del servidor
        btnGetData.setOnClickListener {
            fetchSensorData()
        }

        // Cambiar el fondo dinámicamente según la hora
        updateBackgroundColor()

        // Animar las nubes
        animateClouds()

        // Llamar a la función para actualizar los datos automáticamente cada 1 segundo
        startAutoUpdate()
    }

    private fun startAutoUpdate() {
        val runnable = object : Runnable {
            override fun run() {
                fetchSensorData()
                sunAnimationView.invalidate() // Forzar el refresco de la animación del sol
                updateBackgroundColor() // Cambiar el color de fondo dinámicamente
                animateClouds() // Actualizar animaciones de nubes
                handler.postDelayed(this, updateInterval)
            }
        }
        handler.post(runnable) // Iniciar la actualización automática
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    private fun animateClouds() {
        // Animar la nube 1
        ObjectAnimator.ofFloat(cloud1, View.TRANSLATION_X, -500f, 1000f).apply {
            duration = 10000 // Duración de 10 segundos
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.RESTART
            start()
        }

        // Animar la nube 2
        ObjectAnimator.ofFloat(cloud2, View.TRANSLATION_X, 1000f, -500f).apply {
            duration = 12000 // Duración de 12 segundos
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.RESTART
            start()
        }
    }

    private fun fetchSensorData() {
        RetrofitClient.instance.getLatestData().enqueue(object : Callback<List<SensorData>> {
            override fun onResponse(call: Call<List<SensorData>>, response: Response<List<SensorData>>) {
                if (response.isSuccessful) {
                    val sensorDataList = response.body() ?: emptyList()
                    if (sensorDataList.isNotEmpty()) {
                        val latestData = sensorDataList[0]
                        recyclerView.adapter = SensorDataAdapter(listOf(latestData))
                        tvTemperatura.text = "Temperatura: ${latestData.temperatura}°C"
                        tvHumedad.text = "Humedad: ${latestData.humedad}%"
                        updateAmbienteStatus(latestData.pm2_5.toFloat(), latestData.pm10.toFloat())
                        val bars = ArrayList<Bar>()
                        val barPm25 = Bar().apply {
                            setValue(latestData.pm2_5.toFloat())
                            color = resources.getColor(android.R.color.holo_green_light)
                            name = "PM2.5"
                        }
                        bars.add(barPm25)
                        val barPm10 = Bar().apply {
                            setValue(latestData.pm10.toFloat())
                            color = resources.getColor(android.R.color.holo_orange_light)
                            name = "PM10"
                        }
                        bars.add(barPm10)
                        barGraph.setBars(bars)
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<SensorData>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateAmbienteStatus(pm2_5: Float, pm10: Float) {
        val estadoAmbiente: String = when {
            pm2_5 <= 12 && pm10 <= 54 -> "Estado del Ambiente: Buena"
            pm2_5 <= 35.4 && pm10 <= 154 -> "Estado del Ambiente: Moderada"
            pm2_5 <= 55.4 && pm10 <= 254 -> "Estado del Ambiente: No saludable para grupos sensibles"
            pm2_5 <= 150.4 && pm10 <= 354 -> "Estado del Ambiente: No saludable"
            pm2_5 <= 250.4 && pm10 <= 424 -> "Estado del Ambiente: Muy no saludable"
            else -> "Estado del Ambiente: Peligroso"
        }

        // Actualizar el TextView con el estado
        tvEstadoAmbiente.text = estadoAmbiente
    }

    private fun updateBackgroundColor() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        val color: Int = when (hour) {
            in 0..5 -> Color.parseColor("#001F3F") // Azul oscuro (madrugada)
            in 6..11 -> Color.parseColor("#0074D9") // Azul medio (mañana)
            in 12..17 -> Color.parseColor("#7FDBFF") // Azul claro (tarde)
            in 18..23 -> Color.parseColor("#001F3F") // Azul oscuro (noche)
            else -> Color.WHITE // Color de respaldo
        }

        // Cambiar el color de fondo del layout principal
        mainLayout.setBackgroundColor(color)
    }
}
