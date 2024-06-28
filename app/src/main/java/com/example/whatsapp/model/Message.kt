package com.example.whatsapp.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Message(
    val userId: String = "",
    val message: String = "",
    @ServerTimestamp
    val dateTime: Date? = null
)
