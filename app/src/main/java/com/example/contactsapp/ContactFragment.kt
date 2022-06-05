package com.example.contactsapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.contactsapp.model.ContactsData


class ContactFragment : Fragment(), MyContactRecyclerViewAdapter.OnContactClickListener {

    private var columnCount = 1

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
                adapter = MyContactRecyclerViewAdapter(this@ContactFragment, ContactsData.ITEMS)
            }
        }
        return view
    }

    override fun onClick(position: Int) {

        val contact = ContactsData.ITEMS[position]
        Log.d(TAG, "onClick: $contact")
        ContactsData.selectedItem = contact
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragmentContainerView, DetailsFragment())?.addToBackStack(null)?.commit()
    }


}