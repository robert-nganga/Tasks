package com.robert.tasks.data.remote

import com.robert.tasks.utils.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

//    val apiInterface by lazy {
//        retrofit.create(ApiInterface::class.java)
//    }
}