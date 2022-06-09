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
            contact = requireArguments().get("details") as ContactData?
            Log.d(TAG, "detailsFragment: $contact")
        }else{
            Log.d(TAG, "detailsFragment: Bundle is null")
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
                        contact.phoneNumber[0]
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
                        contact.email[0]
                    } else noEmailLabel
                }
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

    companion object{
        fun newInstance(contact: ContactData): DetailsFragment {
            val myFragment = DetailsFragment()
            val args = bundleOf("details" to contact)
            myFragment.arguments = args
            return myFragment
        }
    }
}