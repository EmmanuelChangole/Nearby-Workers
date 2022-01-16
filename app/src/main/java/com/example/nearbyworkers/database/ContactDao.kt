package com.example.nearbyworkers.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ContactDao
{
    @Insert
    fun insert(contact: Contact)

    @Update
    fun update(contact: Contact)

    @Delete
    fun delete(contact: Contact)

    @Query("delete from contact_database")
    fun deleteAllContact()

    @Query("select * from contact_database order by id desc")
    fun getAllContact(): LiveData<List<Contact>>

    @Query("SELECT * FROM contact_database WHERE uid LIKE :uid")
    fun checkContact(uid:String): List<Contact>


}