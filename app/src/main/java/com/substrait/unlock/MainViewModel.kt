package com.substrait.unlock

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.substrait.unlock.data.PacketContent
import com.substrait.unlock.data.PacketImage
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class MainViewModel : ViewModel() {

    private val _currentPacket = MutableLiveData<PacketImage?>()
    val currentPacket: LiveData<PacketImage?> = _currentPacket

    private val _currentContent = MutableLiveData<PacketContent?>()
    val currentContent: LiveData<PacketContent?> = _currentContent

    init {
        loadPacketFromUrl("https://unpack.nyc3.digitaloceanspaces.com/shared/img_1757978337453_z7yvz_1757978349460.json")
    }

    fun loadPacketFromUrl(urlString: String) {
        viewModelScope.launch {
            val json = withContext(Dispatchers.IO) {
                try {
                    URL(urlString).readText()
                } catch (e: Exception) {
                    // Handle exceptions (e.g., network error)
                    null
                }
            }
            json?.let {
                val packet = Gson().fromJson(it, PacketImage::class.java)
                _currentPacket.postValue(packet)
            }
        }
    }

    fun setCurrentContent(content: PacketContent) {
        _currentContent.value = content
    }
}