package com.example.whatsapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet.Layout
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.whatsapp.databinding.ItemMessagesRecipientBinding
import com.example.whatsapp.databinding.ItemMessagesSenderBinding
import com.example.whatsapp.model.Message
import com.example.whatsapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth

class ChatAdapter : Adapter<ViewHolder>() {

    private var messageList = emptyList<Message>()
    fun addList( list: List<Message> ){
        messageList = list
        notifyDataSetChanged()
    }

    class SenderMessageViewHolder(
        private val binding: ItemMessagesSenderBinding
    ) : ViewHolder( binding.root ){

        fun bind( message: Message ){
            binding.textMessageSender.text = message.message
        }

        companion object{
            fun inflateLayout(parent: ViewGroup) : SenderMessageViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val itemView = ItemMessagesSenderBinding.inflate(
                    inflater, parent, false
                )

                return SenderMessageViewHolder( itemView )
            }
        }
    }

    class RecipientMessageViewHolder(
        private val binding: ItemMessagesRecipientBinding
    ) : ViewHolder( binding.root ){

        fun bind( message: Message ){
            binding.textMessagesRecipient.text = message.message
        }

        companion object{
            fun inflateLayout(parent: ViewGroup) : RecipientMessageViewHolder{
                val inflater = LayoutInflater.from(parent.context)
                val itemView = ItemMessagesRecipientBinding.inflate(
                    inflater, parent, false
                )

                return RecipientMessageViewHolder( itemView )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {

        val message = messageList[position]
        val loggedUserId = FirebaseAuth.getInstance().currentUser?.uid.toString()

        return if (loggedUserId == message.userId){
            Constants.SENDER_TYPE
        }else{
            Constants.RECIPIENT_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        if ( viewType == Constants.SENDER_TYPE)
            return SenderMessageViewHolder.inflateLayout(parent)

        return RecipientMessageViewHolder.inflateLayout(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val message = messageList[position]
        when ( holder ){
            is SenderMessageViewHolder -> holder.bind(message)
            is RecipientMessageViewHolder -> holder.bind(message)
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }
}