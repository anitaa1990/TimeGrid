package com.an.timeleft.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter


private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

fun String.toLocalDate() = LocalDate.parse(this, dateFormatter)
