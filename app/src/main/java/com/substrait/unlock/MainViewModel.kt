package com.substrait.unlock

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.substrait.unlock.data.PacketContent
import com.substrait.unlock.data.PacketImage
import com.substrait.unlock.data.PacketRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: PacketRepository) : ViewModel() {

    private val _currentPacket = MutableLiveData<PacketImage?>()
    val currentPacket: LiveData<PacketImage?> = _currentPacket

    private val _currentContent = MutableLiveData<PacketContent?>()
    val currentContent: LiveData<PacketContent?> = _currentContent

    // The init block is now empty, so the app starts in an empty state.

    fun loadPacketFromUrl(urlString: String) {
        viewModelScope.launch {
            val packet = repository.getPacket(urlString)
            _currentPacket.postValue(packet)
        }
    }

    fun setCurrentContent(content: PacketContent) {
        _currentContent.value = content
    }

    fun closePacket() {
        _currentPacket.value = null
        _currentContent.value = null
    }
}