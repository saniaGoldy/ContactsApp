package com.example.contactsapp.model

import contacts.core.Contacts
import contacts.core.util.phoneList

object ContactsData {
    val ITEMS: MutableList<ContactData> = ArrayList()
    var selectedItem: ContactData? = null

    data class ContactData(
        val contactId: Long,
        val name: String?,
        val phoneNumber: List<String>,
        val organization: List<String>,
        val email: List<String>
    )
}