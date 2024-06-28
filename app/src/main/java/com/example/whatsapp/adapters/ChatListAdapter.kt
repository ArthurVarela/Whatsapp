package com.example.whatsapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.whatsapp.databinding.ItemChatListBinding
import com.example.whatsapp.model.Chat
import com.example.whatsapp.model.User
import com.squareup.picasso.Picasso

class ChatListAdapter(
    private val onClick: (Chat) -> Unit
) : Adapter<ChatListAdapter.ChatListViewHolder>() {

    private var chatList = emptyList<Chat>()
    fun addList(list: List<Chat>){
        chatList = list
        notifyDataSetChanged()
    }

    inner class ChatListViewHolder(
        private val bindind: ItemChatListBinding
    ) : ViewHolder(bindind.root){

        fun bind( chat: Chat ) {
            bindind.textChatListName.text = chat.name
            bindind.textChatListLastMessage.text = chat.lastMessage
            Picasso.get()
                .load(chat.photo)
                .into(bindind.imageChatListPhoto)

            bindind.clChat.setOnClickListener {
                onClick ( chat )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder {
        val inflater = LayoutInflater.from( parent.context )
        val itemView = ItemChatListBinding.inflate(
            inflater, parent, false
        )
        return ChatListViewHolder( itemView )
    }

    override fun onBindViewHolder(holder: ChatListViewHolder, position: Int) {
        val chat = chatList[position]
        holder.bind( chat )

    }

    override fun getItemCount(): Int {
        return chatList.size
    }
}