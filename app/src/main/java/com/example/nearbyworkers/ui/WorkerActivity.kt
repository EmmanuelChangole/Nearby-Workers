package com.example.nearbyworkers.ui

import android.annotation.SuppressLint
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.nearbyworkers.R
import com.example.nearbyworkers.databinding.ActivityWorkerBinding
import com.example.nearbyworkers.model.Worker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.wang.avi.AVLoadingIndicatorView

class WorkerActivity : AppCompatActivity() {
    private lateinit var binding:ActivityWorkerBinding
    private lateinit var imgAdd:ImageView
    private lateinit var tvWork:TextView
    private val TAG=WorkerActivity::class.java.simpleName
    private lateinit var mRef:DatabaseReference
    lateinit var worker:Worker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityWorkerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        imgAdd=binding.imgAdd
        tvWork=binding.tvWork
        checkUser()
        initFirebase()
        imgAdd.setOnClickListener{
         setUpBottomSheet()


        }

    }

    private fun checkUser() {
        val sh = getSharedPreferences("work", MODE_PRIVATE)
        var work=sh.getString("work","").toString()
        if(work.contentEquals("work"))
        {
            tvWork.visibility=View.GONE
        }



    }

    private fun initFirebase()
    {
        val user = FirebaseAuth.getInstance().currentUser
        if(user!=null) {
             mRef = FirebaseDatabase.getInstance().getReference("users").child("worker").child(user.uid)
             mRef.keepSynced(true)
            val eventListener: ValueEventListener =object : ValueEventListener
            {
                @SuppressLint("RestrictedApi")
                override fun onDataChange(snapshot: DataSnapshot)
                {
                    if (snapshot.exists())
                    {
                        worker = snapshot.getValue(Worker::class.java)!!
                    } }
                override fun onCancelled(error: DatabaseError) {
                }
            }
            mRef.addListenerForSingleValueEvent(eventListener)


        }
    }

    private fun setUpBottomSheet() {
        MaterialDialog(this, BottomSheet()).cornerRadius(20f).title(R.string.workAdd).show() {
            customView(R.layout.work_add)

            val work_categories = resources.getStringArray(R.array.workCategories)
            val spinner=view.findViewById<Spinner>(R.id.workSpinner)
            val edTextArea=view.findViewById<EditText>(R.id.textArea_information)
            val butDone=view.findViewById<Button>(R.id.butDone)
            val progress=view.findViewById<AVLoadingIndicatorView>(R.id.loading_view)


            if(spinner!=null)
            {
                val adapter = ArrayAdapter(context,android.R.layout.simple_spinner_dropdown_item,work_categories)
                spinner.adapter=adapter

            }

            if(worker.description!="")
            {
                edTextArea.setText(worker.description)
                spinner.setSelection(work_categories.indexOf(worker.category))
                tvWork.visibility=View.GONE

            }




            butDone.setOnClickListener{

                val sh = getSharedPreferences("work", MODE_PRIVATE)
                val myEdit: SharedPreferences.Editor = sh.edit()


                val description=edTextArea.text
                if (description.isEmpty() || description.length<10)
                {
                    edTextArea.error="Description cannot be less than 10 characters"
                    edTextArea.requestFocus()
                    return@setOnClickListener
                }

               var desc=description.toString()
               var category=spinner.selectedItem


                var userId= FirebaseAuth.getInstance().currentUser!!.uid
                val workMap= mutableMapOf<String,Any>()

                workMap.put("category",category)
                workMap.put("description",desc)

                progress.visibility=View.VISIBLE
                butDone.visibility=View.GONE
                myEdit.putString("work","work")
                myEdit.commit()

                FirebaseDatabase.getInstance().getReference().child("users").child("worker").child(userId).
                updateChildren(workMap).addOnCompleteListener{
                    if(it.isSuccessful)
                    {
                        progress.visibility= View.GONE
                        butDone.visibility=View.VISIBLE
                        tvWork.visibility=View.GONE

                        val eventListener: ValueEventListener =object : ValueEventListener
                        {
                            @SuppressLint("RestrictedApi")
                            override fun onDataChange(snapshot: DataSnapshot)
                            {
                                if (snapshot.exists())
                                {
                                    worker = snapshot.getValue(Worker::class.java)!!
                                } }
                            override fun onCancelled(error: DatabaseError) {
                            }
                        }
                        mRef.addListenerForSingleValueEvent(eventListener)

                        edTextArea.setText(workMap.get("description").toString())
                        spinner.setSelection(work_categories.indexOf(workMap.get("category").toString()))



                    }
                    else
                    {
                        progress.visibility= View.GONE
                        butDone.visibility=View.VISIBLE
                        Log.w(TAG, "error:failure", it.exception)
                        Toast.makeText(context, "Could not save details",
                            Toast.LENGTH_SHORT).show()
                    }
                }





            }






        }

    }


}