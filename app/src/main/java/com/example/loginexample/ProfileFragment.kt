package com.example.loginexample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.loginexample.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firebaseAuth = FirebaseAuth.getInstance()
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(firebaseAuth.currentUser == null){
            //如果還沒登入
            findNavController().navigate(R.id.action_ProfileFragment_to_LoginFragment)
        } else{
            binding.tvProfile.text = getString(R.string.hello) + firebaseAuth.currentUser!!.displayName
        }

        binding.btChangePassword.setOnClickListener {
            findNavController().navigate(R.id.action_ProfileFragment_to_PasswordFragment)
        }

        binding.btLogout.setOnClickListener {
            firebaseAuth.signOut()
            Toast.makeText(context, getString(R.string.logout_successful), Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_ProfileFragment_to_LoginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}