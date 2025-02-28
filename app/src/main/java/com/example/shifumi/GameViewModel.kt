package com.example.shifumi

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import java.text.DecimalFormat

class GameViewModel : ViewModel() {
    var velocity_x = 0.0F
    var velocity_y = 0.0F
    var velocity_z = 0.0F
    var shake_threshold = 50.0F
    var n_shake = 0
    var printable by mutableStateOf("")

    fun get_velocity(): Float {
        return velocity_x + velocity_y + velocity_z
    }
}