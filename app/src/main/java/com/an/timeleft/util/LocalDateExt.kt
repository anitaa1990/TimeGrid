package com.an.timeleft.util

import java.time.Instant
import java.time.ZoneId

fun String.toLocalDate() =
    Instant.ofEpochMilli(this.toLong())
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
