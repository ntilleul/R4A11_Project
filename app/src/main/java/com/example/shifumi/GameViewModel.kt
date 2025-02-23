package com.example.shifumi

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class GameViewModel : ViewModel() {
    var coords by mutableStateOf("")
}