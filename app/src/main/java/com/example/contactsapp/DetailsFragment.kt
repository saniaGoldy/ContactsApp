package com.example.contactsapp

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.contactsapp.model.ContactsData

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetailsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    @SuppressLint("SetTextI18n")
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
                "Organization name: ${
                    if (ContactsData.selectedItem?.organization?.isNotEmpty() == true) {
                        ContactsData.selectedItem?.organization!![0]
                    } else {
                        "no organization"
                    }
                }"

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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DetailsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}