package com.example.contactsapp

import android.os.Parcel
import android.os.Parcelable

data class ContactData(
    val contactId: Long,
    val name: String?,
    val phoneNumber: ArrayList<String>?,
    val organization: String?,
    val email: ArrayList<String>?
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readLong(),
        source.readString(),
        source.createStringArrayList(),
        source.readString(),
        source.createStringArrayList()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeLong(contactId)
        writeString(name)
        writeStringList(phoneNumber)
        writeString(organization)
        writeStringList(email)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ContactData> =
            object : Parcelable.Creator<ContactData> {
                override fun createFromParcel(source: Parcel): ContactData = ContactData(source)
                override fun newArray(size: Int): Array<ContactData?> = arrayOfNulls(size)
            }
    }
}