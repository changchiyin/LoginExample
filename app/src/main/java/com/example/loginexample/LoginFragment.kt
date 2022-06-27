package com.example.loginexample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.loginexample.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firebaseAuth = FirebaseAuth.getInstance()
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(firebaseAuth.currentUser != null){
            findNavController().navigate(R.id.action_LoginFragment_to_ProfileFragment)
        }

        binding.btLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            if(email.isNotBlank() && password.isNotBlank()){
                binding.progressBar.visibility = View.VISIBLE
                binding.btLogin.visibility = View.INVISIBLE
                binding.btRegister.visibility = View.INVISIBLE
                binding.etEmail.visibility = View.INVISIBLE
                binding.etPassword.visibility = View.INVISIBLE
                Timber.e("email:" + email)
                Timber.e("password:" + password)
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        //如果登入成功
                        Timber.e("successful")
                        Toast.makeText(context, getString(R.string.login_successful), Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_LoginFragment_to_ProfileFragment)
                    } else{
                        //如果登入失敗
                        Toast.makeText(context, getString(R.string.email_or_password_error), Toast.LENGTH_SHORT).show()
                        binding.progressBar.visibility = View.INVISIBLE
                        binding.btLogin.visibility = View.VISIBLE
                        binding.btRegister.visibility = View.VISIBLE
                        binding.etEmail.visibility = View.VISIBLE
                        binding.etPassword.visibility = View.VISIBLE
                    }
                }
            } else{
                if(email.isBlank()){
                    binding.etEmail.requestFocus()
                    binding.etEmail.error = getString(R.string.please_type_email)
                } else{
                    binding.etPassword.requestFocus()
                    binding.etPassword.error = getString(R.string.please_type_password)
                }
            }
        }
        binding.btRegister.setOnClickListener {
            findNavController().navigate(R.id.action_LoginFragment_to_RegisterFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}