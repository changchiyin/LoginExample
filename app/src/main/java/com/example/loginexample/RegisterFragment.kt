package com.example.loginexample

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.loginexample.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import timber.log.Timber

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firebaseAuth = FirebaseAuth.getInstance()
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btRegister.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val password_confirm = binding.etPasswordConfirm.text.toString()
            val username = binding.etUsername.text.toString()
            if (username.isNotBlank() && email.isNotBlank() && password.isNotBlank() && password_confirm.isNotBlank()) {
                if (password != password_confirm) {
                    //密碼輸入不一樣
                    binding.etPasswordConfirm.requestFocus()
                    binding.etPasswordConfirm.error = getString(R.string.please_type_same_password)
                } else {
                    //嘗試註冊
                    hideKeyboard()
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btRegister.visibility = View.INVISIBLE
                    binding.etEmail.visibility = View.INVISIBLE
                    binding.etPassword.visibility = View.INVISIBLE
                    binding.etUsername.visibility = View.INVISIBLE
                    binding.etPasswordConfirm.visibility = View.INVISIBLE
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task1 ->
                            if (task1.isSuccessful) {
                                //如果註冊成功
                                val profileUpdates = userProfileChangeRequest {
                                    displayName = binding.etUsername.text.toString()
                                }
                                firebaseAuth.currentUser!!.updateProfile(profileUpdates)
                                    .addOnCompleteListener { task2 ->
                                        if (task2.isSuccessful) {
                                            Toast.makeText(
                                                context,
                                                getString(R.string.successful_registration),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            findNavController().navigate(R.id.action_RegisterFragment_to_ProfileFragment)
                                        }
                                    }
                            } else {
                                //如果註冊失敗
                                binding.progressBar.visibility = View.INVISIBLE
                                binding.btRegister.visibility = View.VISIBLE
                                binding.etEmail.visibility = View.VISIBLE
                                binding.etPassword.visibility = View.VISIBLE
                                binding.etUsername.visibility = View.VISIBLE
                                binding.etPasswordConfirm.visibility = View.VISIBLE
                                val exception = task1.exception!!.toString()
                                Timber.e(exception)
                                when {
                                    exception.contains("email address is already in use") -> {
                                        binding.etEmail.requestFocus()
                                        binding.etEmail.error =
                                            getString(R.string.email_already_use)
                                    }
                                    exception.contains("The email address is badly formatted.") -> {
                                        binding.etEmail.requestFocus()
                                        binding.etEmail.error =
                                            getString(R.string.invalid_email_format)
                                    }
                                    exception.contains("The password is invalid") -> {
                                        binding.etPassword.requestFocus()
                                        binding.etPassword.error =
                                            getString(R.string.invalid_password_format)
                                    }
                                    exception.contains("WeakPassword") -> {
                                        binding.etPassword.requestFocus()
                                        binding.etPassword.error =
                                            getString(R.string.password_is_too_weak)
                                    }
                                    else -> {
                                        Toast.makeText(context, exception, Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                }
                            }
                        }
                }
            } else {
                when {
                    username.isBlank() -> {
                        binding.etUsername.requestFocus()
                        binding.etUsername.error = getString(R.string.please_type_username)
                    }
                    email.isBlank() -> {
                        binding.etEmail.requestFocus()
                        binding.etEmail.error = getString(R.string.please_type_email)
                    }
                    password.isBlank() -> {
                        binding.etPassword.requestFocus()
                        binding.etPassword.error = getString(R.string.please_type_password)
                    }
                    password_confirm.isBlank() -> {
                        binding.etPasswordConfirm.requestFocus()
                        binding.etPasswordConfirm.error =
                            getString(R.string.please_type_confirm_password)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}