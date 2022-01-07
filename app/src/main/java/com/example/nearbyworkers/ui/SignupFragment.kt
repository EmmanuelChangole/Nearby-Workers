package com.example.nearbyworkers.ui

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.nearbyworkers.databinding.SignupFragmentBinding
import com.example.nearbyworkers.viewmodel.SignupViewModel
import com.google.firebase.auth.FirebaseAuth

class SignupFragment : Fragment() {

    companion object {
        fun newInstance() = SignupFragment()
    }

    private lateinit var viewModel: SignupViewModel
    private lateinit var binding:SignupFragmentBinding
    private lateinit var auth:FirebaseAuth
    private var TAG=SignupFragment.javaClass.simpleName

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= SignupFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SignupViewModel::class.java)
        auth = FirebaseAuth.getInstance()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val edUsername=binding.nameEdittextRegister
        val edEmail=binding.emailEdittextRegister
        val edPassword=binding.passwordEdittextRegister
        val edConfirmPassword=binding.confirmPasswordEdittextRegister
        val signup=binding.registerButtonRegister
        val progress=binding.loadingView

        signup.setOnClickListener{
          val username=edUsername.text
          val email=edEmail.text
          val password=edPassword.text
          val confirmPassword=edConfirmPassword.text



         if(username.isEmpty() || username.length<4)
         {
             edUsername.error="Invalid username"
             edUsername.requestFocus()
             return@setOnClickListener
         }

          if(email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
          {
              edEmail.error="Invalid email"
              edEmail.requestFocus()
              return@setOnClickListener
          }

          if(password.isEmpty() || password.length<6)
          {
              edPassword.error="Invalid password,provide at least 6 characters."
              edPassword.requestFocus()
              return@setOnClickListener

          }


          if(!password.contentEquals(confirmPassword))
          {
            edConfirmPassword.error="Password does not match"
            edConfirmPassword.requestFocus()
              return@setOnClickListener

          }
         progress.visibility=View.VISIBLE
         auth.createUserWithEmailAndPassword(email.toString(),password.toString()).
         addOnCompleteListener(requireActivity()){
           if(it.isSuccessful)
           {
               progress.visibility=View.GONE
               Log.d(TAG, "createUserWithEmail:success")
               var intent=Intent(context,DetailsActivity::class.java)
               intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TOP
               intent.putExtra("username",username.toString())
               intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK
               startActivity(intent)
               requireActivity().finish()
           }
             else
             {
                 progress.visibility=View.GONE
                 Log.w(TAG, "createUserWithEmail:failure", it.exception)
                   Toast.makeText(context, "Authentication failed.",
                     Toast.LENGTH_SHORT).show()
             }
         }


        }
        val login=binding.alreadyHaveAccountTextView
        login.setOnClickListener{
            (activity as LoginActivity).setUpFragment(LoginFragment())
        }


    }

}