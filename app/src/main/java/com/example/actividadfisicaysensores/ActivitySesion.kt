package com.example.actividadfisicaysensores

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
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
                    typeActivity = "Ejercicio" //Se establece por defecto para todas las actividades registradas (En un futuro se puede cambiar)
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
            startActivity(intent)
        }
    }
}