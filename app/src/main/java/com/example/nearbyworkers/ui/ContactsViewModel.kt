package com.example.nearbyworkers.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.nearbyworkers.database.Contact
import com.example.nearbyworkers.database.ContactDao
import com.example.nearbyworkers.database.ContactDatabase
import com.example.utils.subscribeOnBackground
import kotlin.properties.Delegates

class ContactsViewModel(
    var database: ContactDao,
    application: Application
) : AndroidViewModel(application)
{

    private  val db=ContactDatabase.getInstance(application)
    private val contacts=database.getAllContact()
    private var isChecked=false

    fun getAllContacts(): LiveData<List<Contact>>
    {

        return contacts

    }

    fun addContact(contact: Contact)
    {
        subscribeOnBackground {
            database.insert(contact)
        }

    }


    fun deleteAll() {
        subscribeOnBackground {
            database.deleteAllContact()
        }

    }

    fun checkUser(contact:Contact):Boolean
    {


       subscribeOnBackground {
           this.isChecked=database.checkContact(contact.uid).isEmpty()
        }





      /*  subscribeOnBackground {
            if(database.checkContact(contact.uid).isEmpty())
            {
              return@subscribeOnBackground
            }
        }*/

        return isChecked
    }


}