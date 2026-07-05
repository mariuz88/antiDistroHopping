package com.mario.beta_antidh.rete
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit



//singleton statico
object RetrofitClient {
    private const val BASE_URL = "https://api.groq.com/openai/"   //coollegato a endpoint 
    
         //interceptor logcat
  private val interceptor = HttpLoggingInterceptor().apply {
        level = if (com.mario.beta_antidh.BuildConfig.DEBUG) {  //se dbgug log
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

  
     //clietn okHttp -> regole collegamento e timeout
    private val client = OkHttpClient.Builder().addInterceptor(interceptor).connectTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).build()

    
     // configurare retrofit con GSON, baseurl e client okHttp
    val service: GroqApiService{
        Retrofit.Builder().baseUrl(BASE_URL).client(client).addConverterFactory(GsonConverterFactory.create()).build().create(GroqApiService::class.java) //create crea GroqApi che prima era vuota
    }
}
