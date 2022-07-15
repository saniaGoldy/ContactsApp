package com.example.contactsapp

import android.os.Parcel
import android.os.Parcelable

data class ContactData(
    val contactId: Long,
    val name: String?,
    val phoneNumber: String?,
    val organization: String?,
    val email: String?
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readLong(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeLong(contactId)
        writeString(name)
        writeString(phoneNumber)
        writeString(organization)
        writeString(email)
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