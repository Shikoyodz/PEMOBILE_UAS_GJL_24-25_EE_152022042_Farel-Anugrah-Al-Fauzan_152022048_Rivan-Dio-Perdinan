package com.shoesis.e_commerce.ui.sign_in

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.shoesis.e_commerce.R
import com.shoesis.e_commerce.databinding.FragmentSignInBinding
import com.shoesis.e_commerce.ui.admin.AdminPanelActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : Fragment() {

    private val signInViewModel: SignInViewModel by viewModels()
    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        observer()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navigate to the SignUpFragment
        binding.tvSignInToSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }

        // Handle login button click
        binding.btnLogin.setOnClickListener {
            val etMail = binding.etSignInEmail.text.toString()
            val etPassword = binding.etSignInPassword.text.toString()

            signInViewModel.signInWithEmail(etMail, etPassword)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observer() {
        signInViewModel.checkCurrentUser.observe(viewLifecycleOwner) { isLoggedIn ->
            if (isLoggedIn) {
                // After successful login, check if it's the admin account
                val email = binding.etSignInEmail.text.toString()
                val password = binding.etSignInPassword.text.toString()

                if (email == "admin@gmail.com" && password == "123456") {
                    // Redirect to Admin Panel Activity
                    val intent = Intent(requireContext(), AdminPanelActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    // Redirect to main graph (or another activity for regular users)
                    findNavController().navigate(R.id.main_graph)
                }
            } else {
                // Handle case when user is not logged in
                // For example, show an error message
            }
        }
    }
}
