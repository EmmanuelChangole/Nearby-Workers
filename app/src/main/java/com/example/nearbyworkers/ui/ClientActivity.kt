package com.example.nearbyworkers.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.nearbyworkers.R
import com.example.nearbyworkers.databinding.ActivityClientBinding

class ClientActivity : AppCompatActivity() {
    private lateinit var imgAdd:ImageView
    private lateinit var binding:ActivityClientBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityClientBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initWidgets()
        setUpFragment(ContactsFragment())

    }

    private fun initWidgets()
    {
        imgAdd=binding.imgAdd
        imgAdd.setOnClickListener{
            setUpFragment(LocationFragment())

        }

    }


    fun setUpFragment(fragment: Fragment)
    {
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container,fragment)
        fragmentTransaction.commit()
    }
}