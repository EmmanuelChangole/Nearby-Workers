package com.example.nearbyworkers.ui

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.nearbyworkers.R
import com.example.nearbyworkers.database.Contact
import com.example.nearbyworkers.database.ContactDatabase
import com.example.nearbyworkers.databinding.LocationFragmentBinding
import com.example.nearbyworkers.model.User
import com.example.nearbyworkers.model.Worker
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.location_fragment.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class LocationFragment : Fragment(),UserAdapter.ClickListener {
    private var permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION)
    private var REQUEST_CODE = 1001
    private lateinit var fusedLocationClient : FusedLocationProviderClient
    private lateinit var locationRequest : LocationRequest
    private lateinit var locationCallback : LocationCallback
    private val TAG=LocationFragment::class.java.simpleName
    private var locationUpdateState = false
    private lateinit var swiperefresh:SwipeRefreshLayout
    private lateinit var recyclerView:RecyclerView
    private lateinit var mRef1:DatabaseReference
    private lateinit var mRef2:DatabaseReference
    private lateinit var contactViewModel:ContactsViewModel
    var currentUser: User? = null
    private lateinit var binding: LocationFragmentBinding
    lateinit var itemListener:UserAdapter.ClickListener
    private  var worker:ArrayList<Worker> = ArrayList()
    private lateinit var userAdapter:UserAdapter

    companion object {
        fun newInstance() = LocationFragment()
    }

    private lateinit var viewModel: LocationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= LocationFragmentBinding.inflate(inflater)
        return binding.root

        //return inflater.inflate(R.layout.location_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LocationViewModel::class.java)

        if(ActivityCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), permissions, REQUEST_CODE)
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        createLocationRequest()
        locationCallback = object :LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                var lastLocation = p0.lastLocation
                val uid = FirebaseAuth.getInstance().uid ?: return
                val ref = FirebaseDatabase.getInstance().getReference("users").child("client").child(uid)
                //HashMap<String, O>
                ref.child("lat").setValue(lastLocation.latitude)
                ref.child("lon").setValue(lastLocation.longitude)
            }
        }
        userAdapter=UserAdapter(itemListener)
        startLocationUpdates()
        initFirebase()


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
         swiperefresh=binding.swiperefresh
         recyclerView=binding.recyclerviewNewmessage
        val dataSource=ContactDatabase.getInstance(requireContext()).contactsDatabase
        val application= requireNotNull(activity).application
        val viewModelFactory=ContactsViewModelFactory(dataSource,application)
        contactViewModel=ViewModelProvider(this,viewModelFactory).get(ContactsViewModel::class.java)
        itemListener=this
         swiperefresh.setOnRefreshListener {
            fetchUsers()
         }
    }

    private fun initFirebase()
    {
        val uid = FirebaseAuth.getInstance().uid ?: return
        mRef1 = FirebaseDatabase.getInstance().getReference("users").child("worker")
        mRef1.keepSynced(true)
        mRef2 = FirebaseDatabase.getInstance().getReference("users").child("client").child(uid)
        mRef2.keepSynced(true)
        val eventListener: ValueEventListener =object : ValueEventListener
        {
            @SuppressLint("RestrictedApi")
            override fun onDataChange(snapshot: DataSnapshot)
            {
                if (snapshot.exists())
                {
                    currentUser = snapshot.getValue(User::class.java)


                }}
            override fun onCancelled(error: DatabaseError) {
            }
        }

        mRef2.addListenerForSingleValueEvent(eventListener)


    }

    override fun onResume() {
        super.onResume()
        if(locationUpdateState) {
            startLocationUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        this.fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun startLocationUpdates() {
        if(ActivityCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        this.fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == REQUEST_CODE) {
            if(grantResults.size == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()
            }
        }
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        var builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(requireActivity())
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            locationUpdateState = true
            startLocationUpdates()
        }
    }

    fun getDistances(myLat: Double, myLong: Double, hisLat: Double, hisLong: Double):Double
    {
        val r = 6371.0

        var lon1 = Math.toRadians(myLong)
        var lon2 = Math.toRadians(hisLong)
        var lat1 = Math.toRadians(myLat)
        var lat2 = Math.toRadians(hisLat)
        val dlon = lon2 - lon1
        val dlat = lat2 - lat1

        val a = (Math.pow(Math.sin(dlat / 2), 2.0)
                + (Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2), 2.0)))
        val c = 2 * Math.asin(Math.sqrt(a))

        return(c * r)
    }


    fun fetchUsers() {
        swiperefresh.isRefreshing = true
        var myLon = 0.0
        var myLat = 0.0
        if (currentUser != null) {
            myLon = currentUser!!.lon
            myLat = currentUser!!.lat
        }

        mRef1.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()

                dataSnapshot.children.forEach {
                    Log.d(TAG, it.toString())
                    @Suppress("NestedLambdaShadowedImplicitParameter")
                    it.getValue(Worker::class.java)?.let {
                        // calculate distance with all others
                            //var dis = getDistandce(myLat,myLon,it.lat,it.lon)
                            if(it.access!=false)
                            {
                                var dis=getDistances(myLat,myLon,it.lat,it.lon)
                               // Toast.makeText(requireActivity(),"${dis}", Toast.LENGTH_LONG).show()
                                //  adapter.add(UserItem(it, this@GetLocationActivity))
                                //here compare the distance, load the user in range
                                if (dis < 5.0)
                                {
                                 worker.clear()
                                 worker.add(it)
                                  //  adapter.add(UserItem(it, requireActivity()))
                                }
                            }



                    }
                }
                // use a dialog to add user to contact

                adapter.setOnItemClickListener { item, _ ->
                    val dialog = Dialog(requireActivity(), R.style.dialog)
                    dialog.setContentView(R.layout.dialog_add_contact)
                    item.getPosition(item)
                    val window = dialog.window
                    window?.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)
                    dialog.findViewById<Button>(R.id.yes).setOnClickListener {



                        return@setOnClickListener

                        var worker=item as Worker
                        val contact=Contact(uid =worker.uid, name = worker.username, description = worker.description)
                       if(!contactViewModel.checkUser(contact))
                       {
                           contactViewModel.addContact(contact)
                           Toast.makeText(requireContext(),"Worker added to contact",Toast.LENGTH_LONG).show()
                       }
                        else{
                           Toast.makeText(requireContext(),"Failed to add worker",Toast.LENGTH_LONG).show()

                        }




                     /*   var users : ArrayList<String>? = ArrayList()
                        val dbRef = FirebaseDatabase.getInstance().getReference("users/${FirebaseAuth.getInstance().currentUser?.uid}")
                        val contacts = dbRef.child("contacts")
                        contacts.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                            }
                            override fun onDataChange(p0: DataSnapshot) {
                                users = p0.value as? ArrayList<String>
                                var user = (item as? UserItem)?.worker!!.uid
                                val contact=Contact(us)




                                Toast.makeText(requireContext(),user.toString(),Toast.LENGTH_LONG).show()
                                // in case duplicate adding
                              *//*  if (users!!.contains(user)){
                                    Toast.makeText(requireActivity(),"Already in your contacts",
                                        Toast.LENGTH_SHORT).show()
                                }
                                else if (!users!!.contains(user)){
                                    users!!.add(user)
                                    Toast.makeText(requireActivity(),"Add successful", Toast.LENGTH_SHORT).show()
                                }
                                contacts.setValue(users)*//*
                            }
                        })*/
                    }
                    dialog.show()
                    dialog.findViewById<Button>(R.id.cancel).setOnClickListener {
                        dialog.dismiss()
                    }
                }
                userAdapter.submitList(worker)
                recyclerView.layoutManager= LinearLayoutManager(activity)
                recyclerView.setHasFixedSize(true)
                recyclerView.adapter=userAdapter
                swiperefresh.isRefreshing = false
            }

        })
    }

    override fun onItemClicked(worker: Worker, position: Int)
    {
        val dialog = Dialog(requireActivity(), R.style.dialog)
        dialog.setContentView(R.layout.dialog_add_contact)
        val window = dialog.window
        window?.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)
        dialog.findViewById<Button>(R.id.yes).setOnClickListener {

            val contact=Contact(uid =worker.uid, name = worker.username, description = worker.description)
            if(contactViewModel.checkUser(contact))
            {
                contactViewModel.addContact(contact)
                Toast.makeText(requireContext(),"Worker added to contact",Toast.LENGTH_LONG).show()
            }
            else{
                Toast.makeText(requireContext(),"Failed to add worker",Toast.LENGTH_LONG).show()

            }


        }
        dialog.show()
        dialog.findViewById<Button>(R.id.cancel).setOnClickListener {
            dialog.dismiss()
        }
    }


    }


class UserItem(val worker: Worker, val context: Context) : Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.username_textview_new_message.text = worker.username

        if (!worker.profileImageUrl!!.isEmpty()) {
            val requestOptions = RequestOptions().placeholder(R.drawable.ic_user)


            Glide.with(viewHolder.itemView.imageview_new_message.context)
                .load(worker.profileImageUrl)
                .apply(requestOptions)
                .into(viewHolder.itemView.imageview_new_message)

            viewHolder.itemView.user_id.text = worker.username

            viewHolder.itemView.imageview_new_message.setOnClickListener{
                  Toast.makeText(context,worker.toString(),Toast.LENGTH_LONG).show()

            }
        }
    }

    fun getItem()
    {

    }



    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }
}

class UserAdapter( private val listener:ClickListener):
    ListAdapter<Worker, RecyclerView.ViewHolder>(ListItemCallback())
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.ViewHolder
    {
        val view: View =LayoutInflater.from(parent.context).inflate(R.layout.user_row_new_message,parent,false)
        return ViewHolder(view)


    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    {
        val worker=getItem(position)
        (holder as ViewHolder).bind(worker,position)

    }


   inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        var tvUsername: TextView?=null
        var circleImageView:CircleImageView?=null

        init {
            tvUsername=itemView.findViewById(R.id.username_textview_new_message)
            circleImageView=itemView.findViewById(R.id.imageview_new_message)
        }
        fun bind(worker: Worker,position:Int)
        {
            tvUsername?.text=worker?.username
            Picasso.get().load(worker?.profileImageUrl).fit().into(circleImageView)
            itemView.setOnClickListener{
                listener.onItemClicked(worker,position)
            }


        }



    }
    class ListItemCallback : DiffUtil.ItemCallback<Worker>() {
        override fun areItemsTheSame(oldItem: Worker, newItem: Worker): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Worker, newItem: Worker): Boolean {
            return     oldItem.username==newItem.username
                    && oldItem.access ==newItem.access
                    && oldItem.account ==newItem.account
                    && oldItem.description ==newItem.description
                    && oldItem.category ==newItem.category
                    && oldItem.uid ==newItem.uid
                    && oldItem.lat == newItem.lat
                    && oldItem.lon == newItem.lon
                    && oldItem.profileImageUrl == newItem.profileImageUrl

        }

    }

    interface ClickListener {
        fun onItemClicked(worker: Worker,position: Int)
    }



}