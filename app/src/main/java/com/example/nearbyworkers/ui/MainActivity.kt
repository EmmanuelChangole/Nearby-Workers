package com.example.nearbyworkers.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.nearbyworkers.R
import com.example.nearbyworkers.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.content.SharedPreferences




class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth= FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser == null){
          val intent= Intent(this,LoginActivity::class.java)
            intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        else
        {
            val sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
            var account=sh.getString("account","").toString()

         if(account.contentEquals("worker"))
         {
             var intent= Intent(this@MainActivity,WorkerActivity::class.java)
             startActivity(intent)
             finish()
         }
            else
            {
                var intent= Intent(this@MainActivity,ClientActivity::class.java)
                startActivity(intent)
                finish()

            }



        }





    }


}