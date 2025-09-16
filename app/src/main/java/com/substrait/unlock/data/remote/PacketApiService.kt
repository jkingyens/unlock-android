package com.substrait.unlock.data.remote

import com.substrait.unlock.data.PacketImage
import retrofit2.http.GET
import retrofit2.http.Url

interface PacketApiService {
    @GET
    suspend fun getPacket(@Url url: String): PacketImage
}