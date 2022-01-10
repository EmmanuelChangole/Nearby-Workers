package com.example.nearbyworkers.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.addisonelliott.segmentedbutton.SegmentedButton
import com.addisonelliott.segmentedbutton.SegmentedButtonGroup
import com.example.nearbyworkers.R
import com.example.nearbyworkers.databinding.ActivityDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.wang.avi.AVLoadingIndicatorView
import java.util.*
import kotlin.collections.HashMap
import android.content.SharedPreferences




class DetailsActivity : AppCompatActivity() {

    private lateinit var binding:ActivityDetailsBinding
    private var accountType:String=""
    private lateinit var mRadioButton:SegmentedButtonGroup
    private lateinit var butSave:Button
    private lateinit var progress:AVLoadingIndicatorView
    private val TAG=DetailsActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        binding= ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val userName=getIntent().getStringExtra("username").toString()
        initObject(userName)

    }

    private fun initObject(username:String)
    {
        val preferences = getSharedPreferences("work", 0)
        preferences.edit().remove("work").commit();

        val sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val myEdit: SharedPreferences.Editor = sh.edit()


        mRadioButton=binding.radioRealButtonGroup
        mRadioButton.setPosition(0,false)
        butSave=binding.register
        progress=binding.loadingView
        butSave.setOnClickListener{
            val selectedPosition= mRadioButton.position
            if(selectedPosition ==1)
            {
                accountType="worker"
            }
            else
            {
                accountType="client"

            }
          progress.visibility=View.VISIBLE
          var userId=FirebaseAuth.getInstance().currentUser!!.uid
          val newUserMap= mutableMapOf<String,Any>()
          newUserMap.put("uid",userId)
          newUserMap.put("username",username)
          newUserMap.put("profileImageUrl", "default")
          newUserMap.put("account",accountType)

          myEdit.putString("account",accountType)
          myEdit.commit()



            FirebaseDatabase.getInstance().getReference().child("users").child(accountType).child(userId).
            updateChildren(newUserMap).addOnCompleteListener{
              if(it.isSuccessful)
              {
                              progress.visibility=View.GONE
                              if (accountType.contentEquals("worker")) {
                                  var intent= Intent(this,WorkerActivity::class.java)
                                  startActivity(intent)
                                  finish()
                              }
                              else {
                                  var intent = Intent(this, ClientActivity::class.java)
                                  startActivity(intent)
                                  finish()

                              }

                      }
             else
             {
                 progress.visibility= View.GONE
                 Log.w(TAG, "error:failure", it.exception)
                 Toast.makeText(this, "Could not save details",
                     Toast.LENGTH_SHORT).show()
             }
          }


        }





    }
}