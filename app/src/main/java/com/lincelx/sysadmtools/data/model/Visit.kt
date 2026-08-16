package com.lincelx.sysadmtools.data.model

import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

data class Visit(
    val id: String = UUID.randomUUID().toString(),
    val date: LocalDate,
    val clientId: String,
    val time: LocalTime? = null,
)
