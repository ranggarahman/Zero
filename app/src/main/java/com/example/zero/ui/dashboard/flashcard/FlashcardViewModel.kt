package com.example.zero.ui.dashboard.flashcard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zero.data.FlashcardItem

class FlashcardViewModel: ViewModel() {

    val isPreviousButtonEnabled: LiveData<Boolean>
        get() = _isPreviousButtonEnabled
    private val _isPreviousButtonEnabled = MutableLiveData<Boolean>()

    val isNextButtonEnabled: LiveData<Boolean>
        get() = _isNextButtonEnabled
    private val _isNextButtonEnabled = MutableLiveData<Boolean>()

    private val _currentFlashcardIndex = MutableLiveData<Int>()
    val currentFlashcardIndex:LiveData<Int> = _currentFlashcardIndex

    init {
        _currentFlashcardIndex.value = 0
    }

    fun incrementIndex(flashcards: List<FlashcardItem>) {
        val currentIndex = _currentFlashcardIndex.value ?: return
        val newIndex = currentIndex + 1
        if (newIndex <= flashcards.lastIndex) {
            _currentFlashcardIndex.value = newIndex
        }
    }

    fun decrementIndex() {
        val currentIndex = _currentFlashcardIndex.value ?: return
        val newIndex = currentIndex - 1
        if (newIndex >= 0) {
            _currentFlashcardIndex.value = newIndex
        }
    }
}