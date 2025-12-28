package com.example.actividadfisicaysensores.model

import java.util.Date

data class RegistroSesion(
    val name: String, //nombre actividad
    val duration: String, //tiempo en min
    val date: Date, //Objeto Date para dia y hora
    val typeActivity: String //Tipo de actividad, por defecto puesta en ejercicio
)



