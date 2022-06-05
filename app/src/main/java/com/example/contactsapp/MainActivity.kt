package com.example.contactsapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.example.contactsapp.model.ContactsData
import contacts.core.Contacts
import contacts.core.util.emailList
import contacts.core.util.organizationList
import contacts.core.util.phoneList


val CONTACT_PROJECTION: Array<out String> = arrayOf(
    ContactsContract.Data._ID,
    ContactsContract.Contacts.LOOKUP_KEY,
    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
    ContactsContract.CommonDataKinds.Phone.NUMBER,
    ContactsContract.CommonDataKinds.Organization.COMPANY,
    ContactsContract.CommonDataKinds.Email.ADDRESS,
    ContactsContract.Data.MIMETYPE
)

const val TAG = "MyApp"

class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!readContactsPermissionGranted()) {
            requestReadContactsPermission()
        }
        val contactsData = mutableListOf<ContactsData.ContactData>()
        val contacts = Contacts(this).query().find()
        contacts.forEach { contact ->
            contactsData.add(
                ContactsData.ContactData(
                    contact.id,
                    contact.displayNamePrimary,
                    contact.phoneList().map{ it.number ?: "no phone number" },
                    contact.organizationList().map{ it.company ?: "no organization found" },
                    contact.emailList().map{ it.address ?: "no email address found" }
                    )
            )
        }

        ContactsData.ITEMS.apply {
            this.clear()
            this.addAll(contactsData)
        }
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<ContactFragment>(R.id.fragmentContainerView)
            }
        }

    }

    private fun requestReadContactsPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), 0)

    }

    private fun readContactsPermissionGranted() = ActivityCompat.checkSelfPermission(
        this,
        Manifest.permission.READ_CONTACTS
    ) == PackageManager.PERMISSION_GRANTED

}