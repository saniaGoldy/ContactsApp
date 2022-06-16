package com.example.contactsapp

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.*
import android.provider.ContactsContract
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*


const val TAG = "MyApp"

val CONTACT_PROJECTION: Array<out String> = arrayOf(
    ContactsContract.Data._ID,
    ContactsContract.Contacts.LOOKUP_KEY,
    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
    ContactsContract.CommonDataKinds.Phone.NUMBER
)

class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!readContactsPermissionGranted()) {
            requestReadContactsPermission()
        }

        val contacts = requestContacts()
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                val contactsFragment = ContactFragment.newInstance(contacts)
                replace(R.id.fragmentContainerView, contactsFragment)
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

    override fun onStart() {
        super.onStart()

        IntentFilter().let { ifilter ->
            ifilter.addAction(Intent.ACTION_POWER_CONNECTED)
            ifilter.addAction(Intent.ACTION_POWER_DISCONNECTED)
            ifilter.addAction(Intent.ACTION_BATTERY_CHANGED)
            registerReceiver(BatteryStatusReceiver(), ifilter)
        }
    }

    override fun onStop() {
        super.onStop()
        this.unregisterReceiver(BatteryStatusReceiver())
    }

    inner class BatteryStatusReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            this@MainActivity.apply {
                val tvBatteryStatus = findViewById<TextView>(R.id.tvBattery)
                if (intent != null) {
                    val status = intent.getIntExtra(
                        BatteryManager.EXTRA_STATUS,
                        BatteryManager.BATTERY_STATUS_UNKNOWN
                    )
                    Log.d(TAG, "onReceive: battery status is $status")
                    tvBatteryStatus.text = when (status) {
                        BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Charging disconnected"
                        BatteryManager.BATTERY_STATUS_CHARGING -> "Charging connected"
                        else -> "battery status"
                    }
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun requestContacts(): MutableList<ContactData> {
        val result: MutableList<ContactData> = mutableListOf()

        runBlocking {
            this@MainActivity.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null
            )
                ?.also { cursor ->
                    if (cursor.moveToFirst()) {
                        do {
                            val contactId =
                                cursor.getLong(cursor.getColumnIndexOrThrow(CONTACT_PROJECTION[0]))
                            Log.d(TAG, "requestContacts: contact $contactId request")
                            val name =
                                cursor.getString(cursor.getColumnIndexOrThrow(CONTACT_PROJECTION[2]))
                                    ?: ""
                            val phones = requestContactDetail(
                                contactId,
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                ContactsContract.CommonDataKinds.Phone.NUMBER
                            )
                            val emails = requestContactDetail(
                                contactId,
                                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                ContactsContract.CommonDataKinds.Email.ADDRESS
                            )
                            val rawContactId = getRawContactId(contactId.toString())
                            val companyName = getCompanyName(rawContactId!!) ?: ""



                            result.add(
                                ContactData(
                                    contactId,
                                    name,
                                    arrayListOf(phones),
                                    companyName,
                                    arrayListOf(emails)
                                ).also { Log.d(TAG, it.phoneNumber.toString()) })

                        } while (cursor.moveToNext())
                    }
                }


        }
        return result
    }

    private suspend fun requestContactDetail(
        contactId: Long,
        uri: android.net.Uri,
        columnName: String
    ): String {
        return withContext(lifecycleScope.coroutineContext + Dispatchers.IO) {
            var string = "Can`t get details"
            contentResolver.query(
                uri,
                null,
                "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} =?",
                arrayOf(contactId.toString()),
                null
            )?.also { contactDetailsCursor ->
                if (contactDetailsCursor.moveToFirst()) {
                    string =
                        contactDetailsCursor.getString(
                            contactDetailsCursor.getColumnIndexOrThrow(
                                columnName
                            )
                        ).also {
                            Log.d(TAG, "$contactId data: $it")
                            contactDetailsCursor.close()
                        }

                }
            }
            string
        }
    }

    private fun getRawContactId(contactId: String): String? {
        val projection = arrayOf(ContactsContract.RawContacts._ID)
        val selection = ContactsContract.RawContacts.CONTACT_ID + "=?"
        val selectionArgs = arrayOf(contactId)
        val c: Cursor = contentResolver.query(
            ContactsContract.RawContacts.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )
            ?: return null
        var rawContactId = -1
        c.getColumnIndex(ContactsContract.RawContacts._ID).apply {
            if (c.moveToFirst() && this >= 0) {
                rawContactId = c.getInt(this)
            } else {
                Log.d(TAG, "getRawContactId: cursor.getString(-1)")
            }
        }

        c.close()
        return rawContactId.toString()
    }

    private fun getCompanyName(rawContactId: String): String? {
        return try {
            val orgWhere =
                ContactsContract.Data.RAW_CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"
            val orgWhereParams = arrayOf(
                rawContactId,
                ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE
            )
            val cursor: Cursor = contentResolver.query(
                ContactsContract.Data.CONTENT_URI,
                null, orgWhere, orgWhereParams, null
            ) ?: return null
            var name: String? = null
            cursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY).apply {
                if (cursor.moveToFirst() && this >= 0) {
                    name = cursor.getString(this)
                } else {
                    Log.d(TAG, "getCompanyName: cursor.getString(-1)")
                }
            }
            cursor.close()
            name
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}