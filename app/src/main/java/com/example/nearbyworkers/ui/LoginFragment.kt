package com.example.nearbyworkers.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.nearbyworkers.R
import com.example.nearbyworkers.databinding.LoginFragmentBinding
import com.example.nearbyworkers.viewmodel.LoginViewModel

class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var viewModel: LoginViewModel
    private lateinit var binding:LoginFragmentBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= LoginFragmentBinding.inflate(inflater,container,false)


        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        val email=binding.emailEdittextLogin
        val password=binding.passwordEdittextLogin
        val createAccount=binding.backToRegisterTextview
        val butLogin=binding.loginButtonLogin

        butLogin.setOnClickListener{
            Toast.makeText(context,"Login",Toast.LENGTH_LONG).show()
        }
        createAccount.setOnClickListener{
            (activity as LoginActivity).setUpFragment(SignupFragment())
        }






    }

}