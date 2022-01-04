package com.example.chatkt

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView

    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: UserAdapter

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Der angives hvilken xml der skal vises i appen. R = res, layout = layout directory, activity_main = xml fil
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance("https://chatkt-4049a-default-rtdb.europe-west1.firebasedatabase.app").getReference()

        userList = ArrayList()
        adapter = UserAdapter(this,userList)

        userRecyclerView =  findViewById(R.id.userRecyclerView)

        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter


        // Listen med brugere hentes
        mDbRef.child("user").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                userList.clear()
                for (postSnapshot in snapshot.children){
                    val currentUser = postSnapshot.getValue(User::class.java)


                    // Sørger for at man ikke ser sig selv på listen, da det ikke skal være en feature at kunne skrive til sig selv.

                    if(mAuth.currentUser?.uid != currentUser?.uid){
                        userList.add(currentUser!!)
                    }
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    // menu punkt: Log Out
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }


    // function til at logge ud
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout){

            mAuth.signOut()
            val intent = Intent(this@MainActivity,LogIn::class.java)
            finish()
            startActivity(intent)
            return true
        }
            return true
    }

}