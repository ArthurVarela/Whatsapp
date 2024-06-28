package com.example.whatsapp.model

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Chat(
    val senderUserId: String = "",
    val recipientUserId: String = "",
    val photo: String = "",
    val name: String = "",
    val lastMessage: String = "",
    @ServerTimestamp
    val dateTime: Date? = null
)
