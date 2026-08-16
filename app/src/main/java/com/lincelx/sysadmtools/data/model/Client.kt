package com.lincelx.sysadmtools.data.model

import java.util.UUID

data class CustomField(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val value: String = "",
)

data class Client(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val address: String = "",
    val phone: String = "",
    val note: String = "",
    val customFields: List<CustomField> = emptyList(),
    val category: String = "",
)
