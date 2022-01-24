package com.example.nearbyworkers.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nearbyworkers.R
import com.example.nearbyworkers.database.ContactDatabase
import com.example.nearbyworkers.database.ContactViewAdapter
import com.example.nearbyworkers.databinding.ContactsFragmentBinding
import com.example.nearbyworkers.databinding.SignupFragmentBinding

class ContactsFragment : Fragment() {
    private lateinit var contactViewModel:ContactsViewModel
    private lateinit var binding: ContactsFragmentBinding
    private lateinit var contactRecycler:RecyclerView

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
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ContactsViewModel::class.java)
        var recyclerViewAdapter=ContactViewAdapter()
        contactRecycler.apply {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            adapter=recyclerViewAdapter
        }

        contactViewModel.getAllContacts().observe(viewLifecycleOwner,{
            if(it.isEmpty())
            {

            }

          recyclerViewAdapter.submitList(it)
        })



        // TODO: Use the ViewModel
    }




}