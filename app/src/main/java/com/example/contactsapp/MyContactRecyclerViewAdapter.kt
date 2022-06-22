package com.example.contactsapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.contactsapp.databinding.FragmentContactItemBinding

class MyContactRecyclerViewAdapter(
    private val onContactClickListener: OnContactClickListener,
    private val values: List<ContactData>
) : RecyclerView.Adapter<MyContactRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        FragmentContactItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ),
        onContactClickListener
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(values[position])

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(
        binding: FragmentContactItemBinding,
        private val onContactClickListener: OnContactClickListener
    ) : View.OnClickListener,
        RecyclerView.ViewHolder(binding.root) {

        private val idView: TextView = binding.name

        init {
            idView.setOnClickListener(this)
        }

        fun bind(item: ContactData) {
            val text = item.name + " : " + if (item.phoneNumber?.isNotEmpty() == true) {
                item.phoneNumber
            } else "no phone"
            idView.text = text
        }

        override fun onClick(v: View?) = onContactClickListener.onClick(bindingAdapterPosition)
    }

    interface OnContactClickListener {
        fun onClick(position: Int)
    }
}