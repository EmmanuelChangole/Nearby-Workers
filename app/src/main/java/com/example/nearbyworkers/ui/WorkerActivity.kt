package com.example.nearbyworkers.ui

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.nearbyworkers.R
import com.example.nearbyworkers.databinding.ActivityWorkerBinding
import com.example.nearbyworkers.model.Worker
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.wang.avi.AVLoadingIndicatorView
import java.util.HashMap

class WorkerActivity : AppCompatActivity() {
    private lateinit var binding:ActivityWorkerBinding
    private lateinit var imgAdd:ImageView
    private lateinit var tvWork:TextView
    private lateinit var tvstate:TextView
    private lateinit var switch: Switch
    private val TAG=WorkerActivity::class.java.simpleName
    private lateinit var mRef:DatabaseReference
    private lateinit var worker: Worker
    private var access=false
    private var permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION)
    private var REQUEST_CODE = 1001
    private lateinit var fusedLocationClient : FusedLocationProviderClient
    private lateinit var locationRequest : LocationRequest
    private lateinit var locationCallback : LocationCallback
    private var locationUpdateState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityWorkerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        imgAdd=binding.imgAdd
        tvWork=binding.tvWork
        switch=binding.switchBar
        tvstate=binding.tvAcess

        initLocation()
        createLocationRequest()
        initFirebase()
        checkUser()
        locationCallBack()




        imgAdd.setOnClickListener{
         setUpBottomSheet()
        }

        switch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->

            if(b)
            {
                access=b
                tvstate.setText("Available")
                updateState(access)
            }
            else
            {
                access=b
                tvstate.setText("Unavailable")
                updateState(access)
            }

        })

    }

    private fun locationCallBack() {
        locationCallback = object :LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                var lastLocation = p0.lastLocation

                //HashMap<String, O>
                mRef.child("lat").setValue(lastLocation.latitude)
                mRef.child("lon").setValue(lastLocation.longitude)
            }
        }

    }

    private fun createLocationRequest()
    {
        locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        var builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            locationUpdateState = true
            startLocationUpdates()
        }

    }

    private fun startLocationUpdates()
    {
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        this.fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)

    }




    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_CODE) {
            if(grantResults.size == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()
            }
        }
    }

    private fun initLocation()
    {
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE)
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun updateState(state: Boolean)
    {
        val map = HashMap<String, Any>()
        map.put("access",state)
        mRef.updateChildren(map)
        startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        this.fusedLocationClient.removeLocationUpdates(locationCallback)

    }

    override fun onResume() {
        super.onResume()
        if(locationUpdateState) {
            startLocationUpdates()
        }
    }





    private fun checkUser() {
        val sh = getSharedPreferences("work", MODE_PRIVATE)
        var work=sh.getString("work","").toString()
        if(work.contentEquals("work"))
        {
            tvWork.visibility=View.GONE
            imgAdd.setImageResource(R.drawable.ic_update)

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
                        updateSwitch()

                    } }
                override fun onCancelled(error: DatabaseError) {
                }
            }
            mRef.addListenerForSingleValueEvent(eventListener)


        }
    }


  private fun updateSwitch()
  {
      if(worker!=null)
      {
          switch.isChecked=worker.access
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


            if(worker!=null)
            {
                if(worker!!.description!="")
                {
                    edTextArea.setText(worker!!.description)
                    spinner.setSelection(work_categories.indexOf(worker!!.category))
                    tvWork.visibility=View.GONE

                }
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