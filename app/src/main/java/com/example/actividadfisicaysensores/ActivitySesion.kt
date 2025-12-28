package com.example.actividadfisicaysensores

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.actividadfisicaysensores.model.RegistroSesion
import java.util.Date

/**
 * Actividad principal que actúa como panel de control.
 * Permite registrar nuevas actividades manuales y visualizar el historial mediante un RecyclerView.
 */
class ActivitySesion : AppCompatActivity() {

    // Fuente de datos: Lista mutable que almacena las sesiones registradas en memoria
    private val listaSesiones = mutableListOf<RegistroSesion>()

    // Adaptador encargado de gestionar la visualización de los datos en la lista
    private lateinit var adapter: Adapter

    /**
     * Launcher que gestiona el retorno de datos desde ActivitySensor.
     * registerForActivityResult registra un canal de comunicación que espera un resultado
     * de la actividad que vamos a lanzar.
     */
    private val sensorLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Verificamos si la actividad secundaria (Sensor) terminó con éxito (RESULT_OK)
        if (result.resultCode == RESULT_OK) {

            // 1. EXTRAER DATOS: Obtenemos la información que empaquetamos en el Intent de vuelta.
            // Usamos el operador ?: (elvis operator) para poner valores por defecto si algo falla.
            val intensidad = result.data?.getStringExtra("RESULTADO_INTENSIDAD") ?: "Sin datos"
            val duracionCorta = result.data?.getStringExtra("TIEMPO_SESION") ?: "00:00 min"

            // 2. CREAR MODELO: Creamos un nuevo objeto de tipo RegistroSesion con la info recibida.
            // Combinamos la intensidad en el nombre para que sea más descriptivo.
            val sesionSensor = RegistroSesion(
                name = "Sesión en tiempo real: $intensidad",
                duration = duracionCorta,
                date = Date(),                       // Fecha y hora
                typeActivity = "Sensor"              // Etiqueta para diferenciarlo de lo manual
            )

            // 3. ACTUALIZAR UI: Insertamos el nuevo registro al principio de la lista.
            listaSesiones.add(0, sesionSensor)

            // Notificamos al adaptador que solo se ha insertado un elemento
            adapter.notifyItemInserted(0)

            // Hacemos scroll automático para que el usuario vea su sesión recién grabada
            findViewById<RecyclerView>(R.id.rvRegistro).scrollToPosition(0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enlace con el archivo de diseño XML principal
        setContentView(R.layout.activity_main)

        // --- 1. VINCULACIÓN DE VISTAS ---
        val etEntradaActividad = findViewById<EditText>(R.id.etEntradaActividad)
        val etEntradaTiempo = findViewById<EditText>(R.id.etEntradaTiempo)
        val btnGuardar = findViewById<Button>(R.id.btnGuardar)
        val rv = findViewById<RecyclerView>(R.id.rvRegistro)
        val btnSensor = findViewById<Button>(R.id.btnSensorActivity)

        // --- 2. CONFIGURACIÓN DEL RECYCLERVIEW ---
        // Inicializamos el adaptador pasándole nuestra lista de sesiones
        adapter = Adapter(listaSesiones)
        // El LayoutManager define cómo se posicionan los elementos (en este caso, lista vertical)
        rv.layoutManager = LinearLayoutManager(this)
        // Conectamos el adaptador al RecyclerView
        rv.adapter = adapter

        // --- 3. LÓGICA DE REGISTRO (BOTÓN GUARDAR) ---
        btnGuardar.setOnClickListener {
            val nombre = etEntradaActividad.text.toString()
            val tiempo = etEntradaTiempo.text.toString()

            // Validación de entrada: Evitamos campos vacíos o con solo espacios
            if (nombre.isNotBlank() && tiempo.isNotBlank()) {

                // Creación del objeto de modelo con los datos capturados y la fecha actual
                val nuevaSesion = RegistroSesion(
                    name = nombre,
                    duration = "$tiempo min",
                    date = Date(),
                    typeActivity = "Manual" //Se establece por defecto para todas las actividades registradas (En un futuro se puede cambiar)
                )

                // Insertamos la nueva sesión en la posición 0 (parte superior de la lista)
                listaSesiones.add(0, nuevaSesion)

                // Notificamos al adaptador que hay un nuevo elemento para que refresque la UI
                adapter.notifyItemInserted(0)

                // Hacemos scroll automático para asegurar que el usuario vea el nuevo registro
                rv.scrollToPosition(0)

                // Limpieza de campos
                etEntradaActividad.text.clear()
                etEntradaTiempo.text.clear()
            } else {
                // Feedback visual en caso de error de validación
                Toast.makeText(this, "Por favor, completa los campos", Toast.LENGTH_SHORT).show()
            }
        }

        // --- 4. NAVEGACIÓN (BOTÓN SENSORES) ---
        btnSensor.setOnClickListener {
            // Un Intent define la intención de pasar de la actividad actual a la de sensores
            val intent = Intent(this, ActivitySensor::class.java)
            // se cambia startActivity por sensorlauncher, para que recoja los datos de la otra activity
            sensorLauncher.launch(intent)
        }
    }
}