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


val CONTACT_PROJECTION: Array<out String> = arrayOf(
    ContactsContract.Data._ID,
    ContactsContract.Contacts.LOOKUP_KEY,
    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
    ContactsContract.CommonDataKinds.Phone.NUMBER
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

        val contactsData = requestContacts()

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<ListFragment>(R.id.fragmentContainerView)
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun requestContacts(): MutableList<ContactData> {

        val result: MutableList<ContactData> = mutableListOf()
        contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null)
            ?.use { cursor ->
                if (cursor.moveToFirst()) {
                    do {
                        val contactId =
                            cursor.getLong(cursor.getColumnIndexOrThrow(CONTACT_PROJECTION[0]))
                        val name =
                            cursor.getString(cursor.getColumnIndexOrThrow(CONTACT_PROJECTION[2]))
                                ?: ""
                        val phoneNumber =
                            cursor.getString(cursor.getColumnIndexOrThrow(CONTACT_PROJECTION[3]))

                        result.add(
                            ContactData(
                                contactId,
                                name,
                                listOf(phoneNumber),
                                null
                            ).also { Log.d(TAG, it.phoneNumber.toString()) })
                    } while (cursor.moveToNext())
                }
            }
        return result
    }

    private fun requestReadContactsPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), 0)

    }

    private fun readContactsPermissionGranted() = ActivityCompat.checkSelfPermission(
        this,
        Manifest.permission.READ_CONTACTS
    ) == PackageManager.PERMISSION_GRANTED

}