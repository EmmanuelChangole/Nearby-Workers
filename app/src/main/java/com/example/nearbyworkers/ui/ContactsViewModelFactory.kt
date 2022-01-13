package com.example.nearbyworkers.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.nearbyworkers.database.ContactDao
import java.lang.IllegalArgumentException

class ContactsViewModelFactory(
    private val datasource: ContactDao,
    private val application: Application
):ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T
    {
        if(modelClass.isAssignableFrom(ContactsViewModel::class.java))
        {
            return ContactsViewModel(datasource,application) as T

        }
        throw IllegalArgumentException("Unknown viewModel class")

    }


}