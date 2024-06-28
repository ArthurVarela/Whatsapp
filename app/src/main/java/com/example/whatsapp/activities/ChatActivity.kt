package com.example.whatsapp.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsapp.adapters.ChatAdapter
import com.example.whatsapp.databinding.ActivityChatBinding
import com.example.whatsapp.model.Chat
import com.example.whatsapp.model.Message
import com.example.whatsapp.utils.Constants
import com.example.whatsapp.model.User
import com.example.whatsapp.utils.showMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso

class ChatActivity : AppCompatActivity() {

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val binding by lazy {
        ActivityChatBinding.inflate( layoutInflater )
    }

    private lateinit var chatAdapter: ChatAdapter
    private lateinit var listenerRegistration: ListenerRegistration
    private var recipientUserData: User? = null
    private var senderUserData: User? = null
    private var chatUserData: Chat? = null

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration.remove()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( binding.root )
        retrieveUsersData()
        initializeToolbar()
        initializeClickEvents()
        initializeRecyclerView()
        initializeListeners()
    }

    private fun initializeRecyclerView() {

        with (binding){
            chatAdapter = ChatAdapter()
            rvChatMessages.adapter = chatAdapter
            rvChatMessages.layoutManager = LinearLayoutManager( applicationContext )
        }
    }

    private fun initializeListeners() {
        val senderUserId = firebaseAuth.currentUser?.uid
        val recipientUserId = recipientUserData?.id
        if (senderUserId != null && recipientUserId != null){
            listenerRegistration = firestore
                .collection(Constants.DB_MESSAGES)
                .document(senderUserId)
                .collection(recipientUserId)
                .orderBy(Constants.DB_DATE, Query.Direction.ASCENDING)
                .addSnapshotListener { querySnapshot, error ->

                    if (error != null){
                        showMessage("Erro ao carregar mesagens")
                    }

                    val documents = querySnapshot?.documents
                    val messagesList = mutableListOf<Message>()
                    documents?.forEach { documentSnapshot ->
                        val message = documentSnapshot.toObject( Message::class.java )
                        if (message != null) {
                            messagesList.add( message )
                        }
                    }

                    if ( messagesList.isNotEmpty() ){
                        chatAdapter.addList( messagesList )
                    }


                }
        }
    }

    private fun initializeClickEvents() {
        binding.fabChatMessagesSend.setOnClickListener {
            val textMessage = binding.editChatMessages.text.toString()
            saveMessage(textMessage)
        }
    }

    private fun saveMessage(textMessage: String) {
        if ( textMessage.isNotEmpty() ){

            if (senderUserData?.id != null && recipientUserData?.id != null){
                val message = Message(
                    senderUserData!!.id, textMessage
                )

                val senderChat = Chat(
                    senderUserData!!.id,
                    recipientUserData!!.id,
                    recipientUserData!!.photo,
                    recipientUserData!!.name,
                    textMessage
                )

                val recipientChat = Chat(
                    recipientUserData!!.id,
                    senderUserData!!.id,
                    senderUserData!!.photo,
                    senderUserData!!.name,
                    textMessage
                )

                //save for sender
                saveMessageFirestore(senderUserData!!.id, recipientUserData!!.id, message)
                saveChatFirestore( senderChat )

                //save for recipient
                saveMessageFirestore(recipientUserData!!.id, senderUserData!!.id, message)
                saveChatFirestore( recipientChat )
                binding.editChatMessages.setText("")
            }
        }
    }

    private fun saveChatFirestore(chat: Chat) {
        firestore
            .collection(Constants.DB_CHATS)
            .document(chat.senderUserId)
            .collection(Constants.DB_LAST_CHATS)
            .document(chat.recipientUserId)
            .set( chat )
            .addOnFailureListener {
                showMessage("Erro ao salvar conversa")
            }
    }

    private fun saveMessageFirestore(
        senderUserId: String,
        recipientUserId: String,
        message: Message) {
        firestore
            .collection(Constants.DB_MESSAGES)
            .document(senderUserId)
            .collection(recipientUserId)
            .add( message )
            .addOnFailureListener {
                showMessage("Erro ao enviar mensagem")
            }
    }

    private fun initializeToolbar() {
        val toolbar = binding.tbChat
        setSupportActionBar( toolbar )
        supportActionBar?.apply {
            title = ""
            if ( recipientUserData != null ){
                binding.textChatName.text = recipientUserData!!.name
                Picasso.get()
                    .load(recipientUserData!!.photo)
                    .into(binding.imageChatPhoto)
            }
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun retrieveUsersData() {

        //Recovering sender(logged) User Data
        val loggedUserId = firebaseAuth.currentUser?.uid
        if (loggedUserId != null) {
            firestore
                .collection(Constants.DB_USERS)
                .document(loggedUserId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    senderUserData = documentSnapshot.toObject(User::class.java)
                }
        }

        //Recovering recipient Data
        val extras = intent.extras
        if (extras != null) {
            recipientUserData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                extras.getParcelable("recipientData", User::class.java)
            } else {
                extras.getParcelable("recipientData")
            }
        }
    }
}
