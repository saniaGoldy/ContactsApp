package com.example.contactsapp

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.CursorAdapter
import android.widget.ListView
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader

private val FROM_COLUMNS: Array<String> = arrayOf(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY, ContactsContract.CommonDataKinds.Phone.NUMBER)

private val TO_IDS: IntArray = intArrayOf(android.R.id.text1, android.R.id.text2)

// The column index for the _ID column
private const val CONTACT_ID_INDEX: Int = 0

// The column index for the CONTACT_KEY column
private const val CONTACT_KEY_INDEX: Int = 1

private val PROJECTION: Array<out String> = arrayOf(
    ContactsContract.Data._ID,
    ContactsContract.Contacts.LOOKUP_KEY,
    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
    ContactsContract.CommonDataKinds.Phone.NUMBER
)


class ListFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor>,
    AdapterView.OnItemClickListener {

    // Define global mutable variables
    // Define a ListView object
    lateinit var contactsList: ListView

    // Define variables for the contact the user selects
    // The contact's _ID value
    var contactId: Long = 0

    // The contact's LOOKUP_KEY
    var contactKey: String? = null

    // A content URI for the selected contact
    var contactUri: Uri? = null

    // An adapter that binds the result Cursor to the ListView
    private var cursorAdapter: SimpleCursorAdapter? = null

    // A UI Fragment must inflate its View
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the fragment layout
        return inflater.inflate(R.layout.contact_list_fragment, container, false)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // Gets the ListView from the View list of the parent activity
        activity?.also {
            contactsList = it.findViewById<ListView>(R.id.list_view)
            // Gets a CursorAdapter
            cursorAdapter = SimpleCursorAdapter(
                it,
                R.layout.contact_list_item,
                null,
                FROM_COLUMNS, TO_IDS,
                0
            )
            // Sets the adapter for the ListView
            contactsList.adapter = cursorAdapter
            contactsList.descendantFocusability = ListView.FOCUS_BLOCK_DESCENDANTS
            contactsList.onItemClickListener = this

            /*contactsList.onItemClickListener = OnItemClickListener { parent, view, position, id ->
                val selectedItem = parent.getItemAtPosition(position) as String
                Log.d("MyApp", "onActivityCreated: ")
            }*/
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loaderManager.initLoader(0, null, this)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return activity?.let {

            return CursorLoader(
                it,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                PROJECTION,
                null,
                null,
                null
            )
        } ?: throw IllegalStateException()
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        // Put the result Cursor in the adapter for the ListView
        cursorAdapter?.swapCursor(data)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        // Delete the reference to the existing Cursor
        cursorAdapter?.swapCursor(null)
    }

    override fun onItemClick(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        Log.d("MyApp", "onItemClick: ")
        // Get the Cursor
        val cursor: Cursor? = (parent.adapter as? CursorAdapter)?.cursor?.apply {
            // Move to the selected contact
            moveToPosition(position)
            // Get the _ID value
            contactId = getLong(CONTACT_ID_INDEX)
            // Get the selected LOOKUP KEY
            contactKey = getString(CONTACT_KEY_INDEX)
            // Create the contact's content Uri
            contactUri = ContactsContract.Contacts.getLookupUri(contactId, contactKey)
            /*
             * You can use contactUri as the content URI for retrieving
             * the details for a contact.
             */
        }
    }
}