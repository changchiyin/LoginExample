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
import com.example.loginexample.databinding.FragmentPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber

class PasswordFragment : Fragment() {
    private var _binding: FragmentPasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firebaseAuth = FirebaseAuth.getInstance()
        _binding = FragmentPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btSubmit.setOnClickListener {
            val password_new = binding.etPasswordNew.text.toString()
            val password_confirm = binding.etPasswordConfirm.text.toString()
            if (password_new.isNotBlank() && password_confirm.isNotBlank()) {
                if (password_new != password_confirm) {
                    //密碼輸入不一樣
                    binding.etPasswordConfirm.requestFocus()
                    binding.etPasswordConfirm.error = getString(R.string.please_type_same_password)
                } else {
                    hideKeyboard()
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btSubmit.visibility = View.INVISIBLE
                    binding.etPasswordNew.visibility = View.INVISIBLE
                    binding.etPasswordConfirm.visibility = View.INVISIBLE
                    firebaseAuth.currentUser!!.updatePassword(password_new)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                firebaseAuth.signOut()
                                Toast.makeText(
                                    context,
                                    getString(R.string.your_password_has_changed),
                                    Toast.LENGTH_SHORT
                                ).show()
                                findNavController().navigate(R.id.action_PasswordFragment_to_LoginFragment)
                            } else {
                                binding.progressBar.visibility = View.INVISIBLE
                                binding.btSubmit.visibility = View.VISIBLE
                                binding.etPasswordNew.visibility = View.VISIBLE
                                binding.etPasswordConfirm.visibility = View.VISIBLE
                                val exception = task.exception!!.toString()
                                Timber.e(exception)
                                when {
                                    exception.contains("The password is invalid") -> {
                                        binding.etPasswordNew.requestFocus()
                                        binding.etPasswordNew.error =
                                            getString(R.string.invalid_password_format)
                                    }
                                    exception.contains("WeakPassword") -> {
                                        binding.etPasswordNew.requestFocus()
                                        binding.etPasswordNew.error =
                                            getString(R.string.password_is_too_weak)
                                    }
                                    exception.contains("Log in again") -> {
                                        binding.etPasswordNew.requestFocus()
                                        binding.etPasswordNew.error =
                                            getString(R.string.please_log_in_again_and_try_again)
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
                if (password_new.isBlank()) {
                    binding.etPasswordNew.requestFocus()
                    binding.etPasswordNew.error = getString(R.string.please_type_new_password)
                } else {
                    binding.etPasswordConfirm.requestFocus()
                    binding.etPasswordConfirm.error =
                        getString(R.string.please_type_confirm_password)
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