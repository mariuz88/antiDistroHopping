package com.mario.beta_antidh.rete

import com.mario.beta_antidh.modello.GroqRequest
import com.mario.beta_antidh.modello.GroqResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST


//comunicazione con groq
interface GroqApiService {
    @POST("v1/chat/completions")    //endpoint
    suspend fun chatCompletions(   
        @Header("Authorization") apiKey: String, 
        @Body request: GroqRequest    // @Body  kot to json     
    ): GroqResponse//gson
}
