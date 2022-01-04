package com.example.chatkt

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(val context: Context, val messageList: ArrayList<Message>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_RECEIVED = 1;
    val ITEM_SENT = 2;

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {


        // her inflates beskeden enten som sent eller received og skriver den ind i recyclerview i frontend

    if (viewType == 1 ) {
        val view: View = LayoutInflater.from(context).inflate(R.layout.received, parent, false)
        return ReceivedViewHolder(view)
    } else {
        val view: View = LayoutInflater.from(context).inflate(R.layout.sent, parent, false)
        return SentViewHolder(view)
    }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]

        //Tekst hentes fra message object fra messageliste

        if (holder.javaClass == SentViewHolder::class.java) {
            // sent


            val viewHolder = holder as SentViewHolder
            holder.sentMessage.text = currentMessage.message
        } else {
            // Received

            val viewHolder = holder as ReceivedViewHolder
            holder.receivedMessage.text = currentMessage.message
    }

    }



        // Vi finder ud af om beskeden er til eller fra den loggede bruger

    override fun getItemViewType(position: Int): Int {

        val currentMessage = messageList[position]

        // Vi tjekker sender id

        if (FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)){
            return ITEM_SENT
        } else {
            return ITEM_RECEIVED
        }
    }



    // Metode til total antal af beskeder
    override fun getItemCount(): Int {

        return messageList.size

    }


    //Chat beskeden angives som item til recyclerview

    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val sentMessage = itemView.findViewById<TextView>(R.id.txt_sent_message)

    }

    class ReceivedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val receivedMessage = itemView.findViewById<TextView>(R.id.txt_received_message)

    }

}