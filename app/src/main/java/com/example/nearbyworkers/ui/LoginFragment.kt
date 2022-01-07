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
import com.example.nearbyworkers.databinding.LoginFragmentBinding
import com.example.nearbyworkers.viewmodel.LoginViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var viewModel: LoginViewModel
    private lateinit var binding:LoginFragmentBinding
    private lateinit var auth: FirebaseAuth
    private var TAG=SignupFragment.javaClass.simpleName


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
        auth = FirebaseAuth.getInstance()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        val edEmail=binding.emailEdittextLogin
        val edPassword=binding.passwordEdittextLogin
        val createAccount=binding.backToRegisterTextview
        val butLogin=binding.loginButtonLogin
        val progress=binding.loadingView

        butLogin.setOnClickListener{
           val email=edEmail.text
           val password=edPassword.text

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
          progress.visibility=View.VISIBLE
          auth.signInWithEmailAndPassword(email.toString(),password.toString())
              .addOnCompleteListener(requireActivity()){
                  if(it.isSuccessful)
                  {
                      Log.d(TAG, "signInWithEmail:success")
                      progress.visibility=View.GONE
                      var intent= Intent(context,MainActivity::class.java)
                      intent.flags= Intent.FLAG_ACTIVITY_CLEAR_TOP
                      intent.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK
                      startActivity(intent)
                      requireActivity().finish()

                  }
                  else{
                      progress.visibility=View.GONE
                      Log.w(TAG, "signInWithEmail:failure", it.exception)
                      Toast.makeText(context, "Authentication failed.",
                          Toast.LENGTH_SHORT).show()

                  }
              }

        }
        createAccount.setOnClickListener{
            (activity as LoginActivity).setUpFragment(SignupFragment())
        }






    }

}