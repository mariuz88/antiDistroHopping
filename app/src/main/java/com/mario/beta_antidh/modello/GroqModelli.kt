package com.mario.beta_antidh.modello
import com.google.gson.annotations.SerializedName

data class GroqMessage(
    val role: String,    //"system" o "user"
    val content: String  // coontenuto stringa messaggio
)


data class GroqRequest(
    // parametri richiesta, temperature e tokens default piano gratuito 
    val messages: List<GroqMessage>,
    val model: String = "llama-3.3-70b-versatile",
    val temperature: Double = 0.7,
    val max_tokens: Int = 1024
)

data class GroqChoice(    // 1 sola risposta IA 
    val message: GroqMessage
)
data class GroqResponse(  // insieme possibili risposte 
    val choices: List<GroqChoice>  //  gson traduce JSON in lista kotlin
)


