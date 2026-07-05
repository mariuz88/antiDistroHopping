package com.mario.beta_antidh.modello

// struttura data class per caratteristiche distro. Serve per risposta AI in formato json 
data class Distro(
    val nome: String,
    val base: String,
    val architetture: String,
    val nvidia_nativo: String,
    val categoria: String
)
