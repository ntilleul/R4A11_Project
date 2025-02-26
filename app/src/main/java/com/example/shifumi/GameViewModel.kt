package com.example.shifumi

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class GameViewModel : ViewModel() {
    var velocity_x = 0.0F
    var velocity_y = 0.0F
    var velocity_z = 0.0F
    var shake_threehold = 20.0F
    var is_shaken = false
    var n_shake = 0

    fun get_velocity(): Float {
        return velocity_x + velocity_y + velocity_z
    }
}