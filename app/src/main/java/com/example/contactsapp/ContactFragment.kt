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
    private val contacts: MutableList<ContactData> = mutableListOf()
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val data = requireArguments().get("data")
        if (data is MutableList<*>) {
            contacts.addAll(data as MutableList<ContactData>)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contact_item_list, container, false)


        return view
    }

    override fun onResume() {
        super.onResume()
        var recyclerView: RecyclerView
        requireActivity().apply {
            recyclerView = this.findViewById(R.id.list)
        }
        recyclerView.apply {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
            adapter = MyContactRecyclerViewAdapter(this@ContactFragment, contacts)
        }
    }

    /*override fun onStart() {
        super.onStart()
        requireActivity().apply {
            IntentFilter().let { ifilter ->
                ifilter.addAction(Intent.ACTION_POWER_CONNECTED)
                ifilter.addAction(Intent.ACTION_POWER_DISCONNECTED)
                ifilter.addAction(Intent.ACTION_BATTERY_CHANGED)
                registerReceiver(BatteryStatusReceiver(), ifilter)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        requireActivity().unregisterReceiver(BatteryStatusReceiver())
    }*/

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
