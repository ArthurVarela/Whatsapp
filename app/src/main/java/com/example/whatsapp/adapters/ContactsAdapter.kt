package com.example.whatsapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.whatsapp.databinding.ItemContactsBinding
import com.example.whatsapp.model.User
import com.squareup.picasso.Picasso

class ContactsAdapter(
    private val onClick: (User) -> Unit
) : Adapter<ContactsAdapter.ContactViewHolder>() {

    private var contactList = emptyList<User>()
    fun addList( list: List<User>){
        contactList = list
        notifyDataSetChanged()
    }

    inner class ContactViewHolder(
        private val binding: ItemContactsBinding
    ) : ViewHolder( binding.root ){

        fun bind( user: User ){

            binding.textContactName.text = user.name
            Picasso.get()
                .load(user.photo)
                .into(binding.imageContactPhoto)

            binding.clItemContact.setOnClickListener {
                onClick( user )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val itemView = ItemContactsBinding.inflate(
            inflater, parent, false
        )

        return ContactViewHolder( itemView )
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val user = contactList[position]
        holder.bind( user )
    }

    override fun getItemCount(): Int {
        return  contactList.size
    }
}