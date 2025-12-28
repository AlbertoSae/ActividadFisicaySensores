package com.example.actividadfisicaysensores

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.actividadfisicaysensores.model.RegistroSesion

/**
 * Clase Adapter encargada de conectar la lógica de datos con la interfaz gráfica.
 * Implementa el patrón ViewHolder para optimizar el rendimiento al reciclar las vistas.
 */
class Adapter(
    // Lista mutable que almacena los objetos de tipo RegistroSesion
    private val items: MutableList<RegistroSesion> = mutableListOf()
) : RecyclerView.Adapter<Adapter.VH>() {

    /**
     * El ViewHolder es un contenedor que "sujeta" las referencias a los componentes visuales.
     * Al usar findViewById aquí, evitamos llamar a esta función cada vez que se hace scroll.
     */
    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvDuration: TextView = itemView.findViewById(R.id.tvDuration)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvType: TextView = itemView.findViewById(R.id.tvType)
    }

    /**
     * Crea la estructura visual de una fila (infla el XML).
     * Se llama solo cuando el RecyclerView necesita crear un nuevo contenedor.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        // LayoutInflater convierte el archivo XML (activity_row) en un objeto View de Kotlin
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_row, parent, false)
        return VH(view)
    }

    /**
     * Une los datos del objeto RegistroSesion con la vista (TextViews).
     * Se llama cada vez que una fila se hace visible en pantalla.
     */
    override fun onBindViewHolder(holder: VH, position: Int) {
        // Obtenemos el registro correspondiente a la posición actual
        val item = items[position]

        // Asignación de textos básicos capturados desde el modelo
        holder.tvName.text = item.name
        holder.tvDuration.text = item.duration
        holder.tvType.text = item.typeActivity

        // Formateo de la fecha: Convierte el objeto Date (milisegundos) a texto legible
        // dd: día, MM: mes, yy: año, HH:mm: horas y minutos
        holder.tvDate.text = DateFormat.format("dd/MM/yy HH:mm", item.date)
    }

    /**
     * Indica al RecyclerView cuántos elementos hay en total en la lista.
     */
    override fun getItemCount(): Int = items.size

    /**
     * Método de utilidad para actualizar la lista completa.
     * Limpia la lista actual, añade los nuevos elementos y refresca la UI.
     */
    fun submitList(newItems: List<RegistroSesion>) {
        items.clear()
        items.addAll(newItems)
        // Notifica que todos los datos han cambiado para redibujar la lista
        notifyDataSetChanged()
    }
}