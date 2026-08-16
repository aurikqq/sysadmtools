package com.lincelx.sysadmtools.data.model

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.util.UUID

data class Note(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    @SerializedName("clientIds")
    private val _clientIds: List<String>? = emptyList(),
    @SerializedName("clientId")
    private val _clientId: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    val clientIds: List<String>
        get() = _clientIds ?: listOfNotNull(_clientId)

    companion object {
        fun create(
            id: String = UUID.randomUUID().toString(),
            title: String,
            content: String,
            clientIds: List<String> = emptyList(),
            createdAt: LocalDateTime = LocalDateTime.now()
        ) = Note(id, title, content, clientIds, null, createdAt)
    }
}
