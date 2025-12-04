package com.example.habittracker.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.habittracker.NavigationHost
import com.example.habittracker.Screen
import com.example.habittracker.data.UserCredentials
import com.example.habittracker.databinding.FragmentStartBinding
import com.example.habittracker.data.SessionManager


class StartFragment : Fragment() {

    private var _binding: FragmentStartBinding? = null
    private val binding get() = _binding!!
    private lateinit var navigationHost: NavigationHost

    // Inicjalizacja ViewModel
    private val viewModel: LoginViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is NavigationHost) {
            navigationHost = context
        } else {
            throw RuntimeException("$context must implement NavigationHost")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Przycisk Logowania
        binding.loginButton.setOnClickListener {
            performAuth(isRegister = false)
        }

        // Przycisk Rejestracji
        binding.registerButton.setOnClickListener {
            performAuth(isRegister = true)
        }

        // Obserwacja wyników z ViewModelu
        viewModel.authResult.observe(viewLifecycleOwner) { response ->
            Toast.makeText(requireContext(), response.message, Toast.LENGTH_LONG).show()
            if (response.success) {
                // Udane logowanie/rejestracja, przejdź do Dashboard
                navigationHost.navigateTo(Screen.Dashboard)
            }
        }

        // Obserwacja stanu ładowania
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.loginButton.isEnabled = !isLoading
            binding.registerButton.isEnabled = !isLoading
        }

        viewModel.authResult.observe(viewLifecycleOwner) { response ->
            if (response.success) {
                val sessionManager = SessionManager(requireContext())
                sessionManager.createLoginSession(response.userId ?: 0, binding.usernameEditText.text.toString())

                Toast.makeText(requireContext(), "Zalogowano!", Toast.LENGTH_SHORT).show()
                navigationHost.navigateTo(Screen.Dashboard)
            } else {
                Toast.makeText(requireContext(), response.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun performAuth(isRegister: Boolean) {
        val username = binding.usernameEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Pola nazwy użytkownika i hasła nie mogą być puste.", Toast.LENGTH_SHORT).show()
            return
        }

        val credentials = UserCredentials(username, password)

        if (isRegister) {
            viewModel.register(credentials)
        } else {
            viewModel.login(credentials)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = StartFragment()
    }
}