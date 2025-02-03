package com.an.timeleft.data

data class LeftUiModel(
    val title: String,
    val totalTime: Long,
    val timeCompleted: Long,
    val timeLeft: Long,
    val timeLeftInPercentage: String,
    val isTimeLeftInDays: Boolean
)
