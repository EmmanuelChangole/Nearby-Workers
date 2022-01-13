package com.example.nearbyworkers.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.nearbyworkers.R
import com.example.nearbyworkers.database.ContactDatabase
import com.example.nearbyworkers.databinding.ContactsFragmentBinding
import com.example.nearbyworkers.databinding.SignupFragmentBinding

class ContactsFragment : Fragment() {
    private lateinit var contactViewModel:ContactsViewModel
    private lateinit var binding: ContactsFragmentBinding

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





        return binding.root




    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ContactsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}