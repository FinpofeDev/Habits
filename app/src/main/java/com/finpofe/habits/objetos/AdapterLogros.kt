package com.finpofe.habits.objetos

import ObjLogros
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.finpofe.habits.R


class AdapterLogros(
    private val lista: List<ObjLogros>
) : RecyclerView.Adapter<AdapterLogros.LogroViewHolder>() {

    var onItemClick: ((Int) -> Unit)? = null

    inner class LogroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre = itemView.findViewById<TextView>(R.id.tvNombre)
        val tvDescripcion = itemView.findViewById<TextView>(R.id.tvDescripcion)
        val tvRecompensa = itemView.findViewById<TextView>(R.id.tvRecompensa)
        val tvFecha = itemView.findViewById<TextView>(R.id.tvFecha)

        init {
            itemView.setOnClickListener{
                onItemClick?.invoke(adapterPosition)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogroViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_logro, parent, false)
        return LogroViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogroViewHolder, position: Int) {
        val logro = lista[position]
        holder.tvNombre.text = logro.nombre
        holder.tvDescripcion.text = logro.descripcion
        holder.tvRecompensa.text = logro.recompensa
        holder.tvFecha.text = "${logro.fechaInicio.dia}/${logro.fechaInicio.mes}/${logro.fechaInicio.anio}"

        //Cambiar el fondo si esta completo nyejeje
        if(logro.completado){
            holder.tvNombre.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context,R.color.nord9))
        } else{
            holder.itemView.setBackgroundResource(R.drawable.logro_card_bg)
        }
    }

    override fun getItemCount(): Int = lista.size
}