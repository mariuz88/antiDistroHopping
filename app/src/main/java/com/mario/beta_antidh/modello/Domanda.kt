package com.mario.beta_antidh.modello


   // struttara della domanda
data class Domanda(
    val testo: String,
    val opzioni: List<String> = emptyList(),
    val haAltro: Boolean = true,
    val soloTesto: Boolean = false
)
