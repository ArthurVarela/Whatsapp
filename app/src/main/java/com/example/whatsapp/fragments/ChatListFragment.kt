package com.example.whatsapp.fragments

import android.content.Intent
import android.icu.lang.UCharacter.VerticalOrientation
import android.location.GnssAntennaInfo.Listener
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsapp.R
import com.example.whatsapp.activities.ChatActivity
import com.example.whatsapp.adapters.ChatListAdapter
import com.example.whatsapp.databinding.FragmentChatListBinding
import com.example.whatsapp.model.Chat
import com.example.whatsapp.model.User
import com.example.whatsapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject

class ChatListFragment : Fragment() {

    private lateinit var binding: FragmentChatListBinding
    private lateinit var eventListener: ListenerRegistration
    private lateinit var chatListAdapter: ChatListAdapter

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
        binding = FragmentChatListBinding.inflate(
            inflater, container, false
        )

        chatListAdapter = ChatListAdapter{ chat ->
            val intent = Intent(context, ChatActivity::class.java)

            val user = User(
                id = chat.recipientUserId,
                name = chat.name,
                photo = chat.photo
            )
            intent.putExtra("recipientData", user)
            //intent.putExtra("source", Constants.SOURCE_CHAT)
            startActivity(intent)
        }
        binding.rvChatList.adapter = chatListAdapter
        binding.rvChatList.layoutManager = LinearLayoutManager(context)
        binding.rvChatList.addItemDecoration(
            DividerItemDecoration(
                context, LinearLayoutManager.VERTICAL
            )
        )

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        addChatListListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        eventListener.remove()
    }

    private fun addChatListListener() {
        val loggedUserId = firebaseAuth.currentUser?.uid
        if ( loggedUserId != null){
            eventListener = firestore
                .collection(Constants.DB_CHATS)
                .document(loggedUserId)
                .collection(Constants.DB_LAST_CHATS)
                .orderBy(Constants.DB_DATE, Query.Direction.DESCENDING)
                .addSnapshotListener { querySnapshot, error ->

                    val chatList = mutableListOf<Chat>()
                    val documents = querySnapshot?.documents
                    documents?.forEach { documentSnapshot ->
                        val chat = documentSnapshot.toObject(Chat::class.java)
                        if ( chat != null){
                            chatList.add( chat )
                        }

                        if ( chatList.isNotEmpty() ){
                            chatListAdapter.addList( chatList )
                        }
                    }
                }
        }
    }
}