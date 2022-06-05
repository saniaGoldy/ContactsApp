package com.example.contactsapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.contactsapp.model.ContactsData


class DetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.also { fragmentActivity ->
            fragmentActivity.findViewById<TextView>(R.id.contactName).text =
                ContactsData.selectedItem?.name ?: "Cant find a name"

            fragmentActivity.findViewById<TextView>(R.id.contactPhone).apply {
                var hasPhoneNumber = false
                val noNumberLabel = "no phone number"
                val phoneNumber = ContactsData.selectedItem?.let {
                    if (ContactsData.selectedItem!!.phoneNumber.isNotEmpty()) {
                        hasPhoneNumber = true
                        ContactsData.selectedItem!!.phoneNumber[0]
                    } else noNumberLabel
                } ?: noNumberLabel
                text = phoneNumber

                setOnClickListener {
                    Log.d(TAG, "contactPhone clicked")
                    Intent(Intent.ACTION_DIAL).also {
                        it.data = Uri.parse("tel:${if (hasPhoneNumber) phoneNumber else ""}")
                        startActivity(it)
                    }
                }
            }

            fragmentActivity.findViewById<TextView>(R.id.contactOrganization).text =
                buildString {
                    append("Organization name: ")
                    append(
                        if (ContactsData.selectedItem?.organization?.isNotEmpty() == true) {
                            ContactsData.selectedItem?.organization!![0]
                        } else {
                            "no organization"
                        }
                    )
                }

            fragmentActivity.findViewById<TextView>(R.id.contactEmail).apply {
                var hasEmail = false
                val noEmailLabel = "no email"
                val email = ContactsData.selectedItem?.let {
                    if (ContactsData.selectedItem!!.email.isNotEmpty()) {
                        hasEmail = true
                        ContactsData.selectedItem!!.email[0]
                    } else noEmailLabel
                } ?: noEmailLabel
                text = email

                setOnClickListener {
                    Log.d(TAG, "email clicked")
                    Intent(Intent.ACTION_SENDTO).also {
                        it.data = Uri.parse("mailto:${if (hasEmail) email else ""}")
                        startActivity(it)
                    }
                }

            }
        }
    }
}