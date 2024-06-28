package com.example.whatsapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var id: String = "",
    var name: String = "",
    var email: String = "",
    var photo: String = ""
) : Parcelable
