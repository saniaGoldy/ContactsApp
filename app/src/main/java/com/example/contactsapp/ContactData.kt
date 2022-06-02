package com.example.contactsapp

import android.net.Uri

data class ContactData(
    val contactId: Long,
    val name: String,
    val phoneNumber: List<String>,
    val avatar: Uri?
)