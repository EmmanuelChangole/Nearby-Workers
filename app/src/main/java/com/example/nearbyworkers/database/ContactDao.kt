package com.example.nearbyworkers.database

import androidx.lifecycle.LiveData
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

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

}