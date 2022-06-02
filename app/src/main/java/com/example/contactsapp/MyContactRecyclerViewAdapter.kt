package com.example.contactsapp

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView

import com.example.contactsapp.placeholder.ContactsData.PlaceholderItem
import com.example.contactsapp.databinding.FragmentContactItemBinding

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyContactRecyclerViewAdapter(
    private val values: List<ContactData>
) : RecyclerView.Adapter<MyContactRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentContactItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = item.name
        holder.contentView.text = item.phoneNumber[0]
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentContactItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.name
        val contentView: TextView = binding.number

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

}