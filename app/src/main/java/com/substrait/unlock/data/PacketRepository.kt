package com.substrait.unlock.data

import com.substrait.unlock.data.remote.PacketApiService
import javax.inject.Inject

class PacketRepository @Inject constructor(private val apiService: PacketApiService) {
    suspend fun getPacket(url: String): PacketImage? {
        return try {
            apiService.getPacket(url)
        } catch (e: Exception) {
            // Handle exceptions (e.g., network error)
            null
        }
    }
}