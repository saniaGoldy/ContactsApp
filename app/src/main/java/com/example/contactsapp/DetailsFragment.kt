package com.example.contactsapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment


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
        var contact: ContactData? = null
        if (arguments != null) {
            contact = requireArguments().get(BundleTag) as ContactData?
            Log.d(BundleTag, "detailsFragment: $contact")
        } else {
            Log.d(BundleTag, "detailsFragment: Bundle is null")
        }
        view.setDataToUI(contact)
    }

    private fun View.setDataToUI(contact: ContactData?) {
        this.also { rootView ->
            rootView.findViewById<TextView>(R.id.contactName).text =
                contact?.name ?: "Cant find a name"

            rootView.findViewById<TextView>(R.id.contactPhone).apply {
                var hasPhoneNumber = false
                val noNumberLabel = "no phone number"
                val phoneNumber = contact?.let {
                    if (contact.phoneNumber?.isNotEmpty() == true) {
                        hasPhoneNumber = true
                        contact.phoneNumber
                    } else noNumberLabel
                } ?: noNumberLabel
                text = phoneNumber

                this.setIntentAction(
                    hasPhoneNumber,
                    phoneNumber,
                    Intent.ACTION_DIAL,
                    "tel:",
                    "contactPhone clicked"
                )
            }

            rootView.findViewById<TextView>(R.id.contactOrganization).text =
                buildString {
                    append("Organization name: ")
                    append(
                        if (contact?.organization?.isNotEmpty() == true) {
                            contact.organization
                        } else {
                            "no organization"
                        }
                    )
                }

            rootView.findViewById<TextView>(R.id.contactEmail).apply {
                var hasEmail = false
                val noEmailLabel = "no email"
                val email = contact.let {
                    if (contact?.email?.isNotEmpty() == true) {
                        hasEmail = true
                        contact.email
                    } else noEmailLabel
                }
                text = email

                this.setIntentAction(
                    hasEmail,
                    email,
                    Intent.ACTION_SENDTO,
                    "mailto:",
                    "email clicked"
                )
            }
        }
    }

    private fun TextView.setIntentAction(
        isEmpty: Boolean,
        value: String,
        action: String,
        uriString: String,
        logMessage: String
    ) {
        setOnClickListener {
            Log.d(BundleTag, logMessage)
            Intent(action).also {
                it.data = Uri.parse("$uriString${if (isEmpty) value else ""}")
                startActivity(it)
            }
        }
    }


    companion object {
        const val BundleTag = "details"
        fun newInstance(contact: ContactData): DetailsFragment =
            DetailsFragment().apply { arguments = bundleOf(BundleTag to contact) }
    }
}