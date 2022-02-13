package com.example.nearbyworkers.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nearbyworkers.R
import com.example.nearbyworkers.database.Contact
import com.example.nearbyworkers.database.ContactDatabase
import com.example.nearbyworkers.database.ContactViewAdapter
import com.example.nearbyworkers.database.OnItemClick
import com.example.nearbyworkers.databinding.ContactsFragmentBinding
import com.example.nearbyworkers.databinding.SignupFragmentBinding

class ContactsFragment : Fragment(),OnItemClick {
    private lateinit var contactViewModel:ContactsViewModel
    private lateinit var binding: ContactsFragmentBinding
    private lateinit var contactRecycler:RecyclerView
    private lateinit var imgAdd: ImageView
    private lateinit var tvName:TextView

    companion object {
        fun newInstance() = ContactsFragment()
    }

    private lateinit var viewModel: ContactsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= ContactsFragmentBinding.inflate(inflater,container,false)

        val application = requireNotNull(this.activity).application
        val contactDataSource=ContactDatabase.getInstance(application).contactsDatabase
        val viewModelFactory=ContactsViewModelFactory(contactDataSource,application)
        contactViewModel=ViewModelProvider(this,viewModelFactory).get(ContactsViewModel::class.java)
        contactRecycler=binding.contactRecycler
        imgAdd=binding.imgAdd
        tvName=binding.tvName
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ContactsViewModel::class.java)
        imgAdd.setOnClickListener{
            (activity as ClientActivity).setUpFragment(LocationFragment())
        }

        var recyclerViewAdapter=ContactViewAdapter(this)
        contactRecycler.apply {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            adapter=recyclerViewAdapter
        }

        contactViewModel.getAllContacts().observe(viewLifecycleOwner,{
            if(it.isEmpty())
            {
                tvName.visibility=View.VISIBLE

            }
            else
            {
                tvName.visibility=View.GONE
            }

          recyclerViewAdapter.submitList(it)
        })

        // TODO: Use the ViewModel
    }

    override fun ItemClick(currentItem: Contact)
    {
        Toast.makeText(requireContext(),currentItem.name,Toast.LENGTH_LONG).show()


    }


}