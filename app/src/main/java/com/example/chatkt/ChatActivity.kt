package com.example.chatkt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class ChatActivity : AppCompatActivity() {


    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDbRef: DatabaseReference

    // de to variabler står for et unikt id id for et et chat rum mellem to parter
    var receiverRoom: String? = null
    var senderRoom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        //Navnet på personen der chattes. Kommer med fra user adapter intent
        val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid")

        val senderUid = FirebaseAuth.getInstance().currentUser?.uid

        mDbRef = FirebaseDatabase.getInstance("https://chatkt-4049a-default-rtdb.europe-west1.firebasedatabase.app").getReference()


        // Chat rum id er baseret på summen af de to parters id
        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid



        // Top action bar for navnet som personen man chatter med som title
        supportActionBar?.title = name

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.messageBox)
        sendButton = findViewById(R.id.sendButton)

        messageList = ArrayList()

        // Listen med alle beskeder behandles hos adapteren og placeres korrekt efter som de er sendt eller modtaget.
        messageAdapter = MessageAdapter(this, messageList)


        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter

        // henter data til recycler view (besked tråd)
        mDbRef.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    //clear so beskedtråden ikke duplikeres
                    messageList.clear()

                    for (postSnapshot in snapshot.children){

                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)

                    }

                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })


        // Sender beskeden og tilføjer besked til database
        sendButton.setOnClickListener{

            val message = messageBox.text.toString()
            val messageObject = Message(message,senderUid)

            //push til database

            mDbRef.child("chats").child(senderRoom!!).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
                        .setValue(messageObject)
                }

            //gør inputfeltet tomt efter besked er sendt
            messageBox.setText("")



        }


    }
}