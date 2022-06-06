package com.example.contactsapp

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class ContactFragment : Fragment(), MyContactRecyclerViewAdapter.OnContactClickListener {

    private var columnCount = 1
    private val contacts: MutableList<MainActivity.ContactData> = mutableListOf()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val data = requireArguments().get("data")
        if (data is MutableList<*>) {
            contacts.addAll(data as MutableList<MainActivity.ContactData>)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contact_item_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = MyContactRecyclerViewAdapter(this@ContactFragment, contacts)
            }
        }
        return view
    }

    override fun onClick(position: Int) {

        val contact = contacts[position]
        Log.d(TAG, "onClick: $contact")

        val detailsFragment = DetailsFragment()
        val contactData = bundleOf("details" to contact)
        detailsFragment.arguments = contactData

        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragmentContainerView, detailsFragment)?.addToBackStack(null)?.commit()
    }


}