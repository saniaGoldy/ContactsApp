package com.example.contactsapp

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.example.contactsapp.model.ContactsData
import contacts.core.Contacts
import contacts.core.util.emailList
import contacts.core.util.organizationList
import contacts.core.util.phoneList


const val TAG = "MyApp"

class MainActivity : AppCompatActivity() {
    private var batteryStatus: Intent? = null
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
                    contact.phoneList().map { it.number ?: "no phone number" },
                    contact.organizationList().map { it.company ?: "no organization found" },
                    contact.emailList().map { it.address ?: "no email address found" }
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

    override fun onStart() {
        super.onStart()

        batteryStatus = IntentFilter().let { ifilter ->
            ifilter.addAction(Intent.ACTION_POWER_CONNECTED)
            ifilter.addAction(Intent.ACTION_POWER_DISCONNECTED)
            ifilter.addAction(Intent.ACTION_BATTERY_CHANGED)
            this.registerReceiver(BatteryStatusReceiver(), ifilter)
        }
    }

    override fun onStop() {
        super.onStop()
        this.unregisterReceiver(BatteryStatusReceiver())
    }

    inner class BatteryStatusReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val tvBatteryStatus = findViewById<TextView>(R.id.tvBattery)
            if (intent != null) {
                val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN)
                tvBatteryStatus.text = when (status) {
                    BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Charging disconnected"
                    BatteryManager.BATTERY_STATUS_CHARGING -> "Charging connected"
                    else -> ""
                }
            }
        }
    }
}