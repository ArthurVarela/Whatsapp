package com.example.whatsapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsapp.activities.ChatActivity
import com.example.whatsapp.adapters.ContactsAdapter
import com.example.whatsapp.databinding.FragmentContactsBinding
import com.example.whatsapp.model.User
import com.example.whatsapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class ContactsFragment : Fragment() {

    private lateinit var binding: FragmentContactsBinding
    private lateinit var eventListener: ListenerRegistration
    private lateinit var contactsAdapter: ContactsAdapter

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentContactsBinding.inflate(
            inflater, container, false
        )

        contactsAdapter = ContactsAdapter{ user ->
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("recipientData", user)
            //intent.putExtra("source", Constants.SOURCE_CONTACT)
            startActivity( intent )
        }
        binding.rvContacts.adapter = contactsAdapter
        binding.rvContacts.layoutManager = LinearLayoutManager(context)
        binding.rvContacts.addItemDecoration(
            DividerItemDecoration(
                context, LinearLayoutManager.VERTICAL
            )
        )

        return binding.root
        //return inflater.inflate(R.layout.fragment_contacts, container, false)
    }

    override fun onStart() {
        super.onStart()
        addContactsListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        eventListener.remove()
    }

    private fun addContactsListener() {

        eventListener = firestore
            .collection(Constants.DB_USERS)
            .addSnapshotListener { querySnapshot, error ->

                val userList = mutableListOf<User>()
                val documents = querySnapshot?.documents
                documents?.forEach { documentSnapshot ->

                    val userLoggedId = firebaseAuth.currentUser?.uid
                    val user = documentSnapshot.toObject(User::class.java)

                    if ( user != null && userLoggedId != null ){
                        if ( userLoggedId != user.id){
                            userList.add( user )
                        }
                    }
                }

                if ( userList.isNotEmpty() ){
                    contactsAdapter.addList( userList )
                }
            }
    }
}


