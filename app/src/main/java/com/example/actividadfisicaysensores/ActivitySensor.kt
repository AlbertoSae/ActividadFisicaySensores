package com.example.actividadfisicaysensores


import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlin.math.sqrt

/**
 * Activity que gestiona el uso del acelerómetro en tiempo real.
 * Implementa SensorEventListener para reaccionar a los cambios físicos del dispositivo.
 */
class ActivitySensor : AppCompatActivity(), SensorEventListener {

    // Gestor de sensores del sistema
    private lateinit var sensorManager: SensorManager
    // Referencia específica al sensor de aceleración
    private var acelerometro: Sensor? = null

    // Componentes de la interfaz
    private lateinit var tvEstado: TextView
    private lateinit var fondo: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor)

        // Vinculación de vistas con el layout XML
        tvEstado = findViewById(R.id.tvEstadoMovimiento)
        fondo = findViewById(R.id.layoutFondo)
        val btnFinalizar = findViewById<Button>(R.id.btnFinalizar)

        // 1. Acceder al servicio de sensores del hardware
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        // 2. Intentar obtener el sensor de acelerómetro físico
        acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // Botón para cerrar la actividad y volver a la pantalla principal
        btnFinalizar.setOnClickListener { finish() }
    }

    /**
     * Se dispara automáticamente cada vez que el sensor detecta un cambio de movimiento.
     */
    override fun onSensorChanged(event: SensorEvent?) {
        // Verificamos que el evento provenga del acelerómetro
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            // Valores de aceleración en los ejes X, Y y Z
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            // Cálculo de la Magnitud Vectorial: Representa la fuerza total del movimiento
            // independientemente de la dirección en la que se mueva el móvil.
            val magnitud = sqrt((x * x + y * y + z * z).toDouble())

            // El acelerómetro marca 9.8 m/s² en reposo debido a la gravedad terrestre.
            // Restamos la gravedad para obtener solo la fuerza aplicada por el usuario.
            val movimientoNeto = magnitud - 9.8

            actualizarUI(movimientoNeto)
        }
    }

    /**
     * Modifica el texto y el color de fondo según la intensidad detectada.
     */
    private fun actualizarUI(valor: Double) {
        // Usamos ContextCompat para obtener colores definidos en colors.xml
        when {
            valor < 1.0 -> {
                tvEstado.text = "Sin movimiento"
                fondo.setBackgroundColor(ContextCompat.getColor(this, R.color.background_main))
            }
            valor < 5.0 -> {
                tvEstado.text = "Movimiento suave"
                fondo.setBackgroundColor(ContextCompat.getColor(this, R.color.highlight_secondary))
            }
            else -> {
                tvEstado.text = "¡Movimiento intenso!"
                fondo.setBackgroundColor(ContextCompat.getColor(this, R.color.highlight_tertiary))
            }
        }
    }

    /**
     * Método requerido por la interfaz, se activa si cambia la precisión del sensor.
     */
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No es necesario implementar para esta lógica básica
    }

    /**
     * Al volver a la actividad, activamos el sensor (consumo de energía activo).
     */
    override fun onResume() {
        super.onResume()
        acelerometro?.also { acc ->
            // Registramos esta clase como escuchador con una tasa de refresco apta para UI
            sensorManager.registerListener(this, acc, SensorManager.SENSOR_DELAY_UI)
        }
    }

    /**
     * Si la actividad pasa a segundo plano, desactivamos el sensor para ahorrar batería.
     */
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}