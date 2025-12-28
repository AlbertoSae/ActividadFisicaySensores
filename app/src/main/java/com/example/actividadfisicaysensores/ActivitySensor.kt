package com.example.actividadfisicaysensores


import android.content.Intent
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

    // Guarda los milisegundos de inicio
    private var tiempoInicio: Long = 0

    //Lista para los valores del acelerometro
    private var listaIntensidades = mutableListOf<Double>()

    // Componentes de la interfaz
    private lateinit var tvEstado: TextView
    private lateinit var fondo: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor)

        tiempoInicio = System.currentTimeMillis() //Se guarda el tiempo de inicio

        // Vinculación de vistas con el layout XML
        tvEstado = findViewById(R.id.tvEstadoMovimiento)
        fondo = findViewById(R.id.layoutFondo)
        val btnFinalizar = findViewById<Button>(R.id.btnFinalizar)

        // 1. Acceder al servicio de sensores del hardware
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        // 2. Intentar obtener el sensor de acelerómetro físico
        acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // Botón para cerrar la actividad y volver a la pantalla principal
        btnFinalizar.setOnClickListener {
            val tiempoFin = System.currentTimeMillis()
            val segundosTotales = (tiempoFin - tiempoInicio) / 1000
            val minutos = segundosTotales / 60
            val segundos = segundosTotales % 60

            val tiempoFormateado = String.format("%02d:%02d min", minutos, segundos)
            // 2. CALCULAR INTENSIDAD MEDIA
            // Si la lista está vacía, la media es 0. Si no, sumamos todo y dividimos.
            val promedio = if (listaIntensidades.isNotEmpty()) listaIntensidades.average() else 0.0

            // 3. Determinar etiqueta según el promedio
            val etiquetaFinal = when {
                promedio < 0.5 -> "Reposo"
                promedio < 3.0 -> "Actividad Moderada"
                else -> "Actividad Intensa"
            }
            // 4. Enviar datos de vuelta
            val data = Intent()
            data.putExtra("RESULTADO_INTENSIDAD", etiquetaFinal) // Enviamos la media, no el último valor
            data.putExtra("TIEMPO_SESION", tiempoFormateado)

            setResult(RESULT_OK, data)
            finish()
        }
    }

    /**
     * Se dispara automáticamente cada vez que el sensor detecta un cambio de movimiento.
     */
    override fun onSensorChanged(event: SensorEvent?) {
        // Verificamos que los datos provengan del acelerómetro
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            // Obtenemos la aceleración en los tres ejes (X, Y, Z)
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            // Aplicamos el teorema de Pitágoras en 3D para obtener la magnitud total del movimiento
            val magnitud = sqrt((x * x + y * y + z * z).toDouble())

            // Restamos la constante de gravedad terrestre (~9.8) para obtener el movimiento real
            // causado por el usuario (movimiento neto)
            val movimientoNeto = magnitud - 9.8

            // --- LÓGICA DE PROMEDIO ---
            // Guardamos cada lectura en la lista mutable. Esto nos permitirá saber
            // cómo fue la actividad durante TODA la sesión, no solo en el último segundo.
            listaIntensidades.add(movimientoNeto)

            // Actualizamos los colores y textos de la pantalla con el valor instantáneo
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