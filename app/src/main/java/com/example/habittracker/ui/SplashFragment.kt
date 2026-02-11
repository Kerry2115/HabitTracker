package com.example.habittracker.ui

import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment // Jeśli brakuje importu dla klasy bazowej Fragment
import com.example.habittracker.databinding.FragmentSplashBinding
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.habittracker.NavigationHost // Poprawiony import!
import com.example.habittracker.Screen // Poprawiony import!

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    private lateinit var navigationHost: NavigationHost
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = Runnable {
        navigationHost.navigateTo(Screen.Start)
    }

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
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handler.postDelayed(runnable, 2000L)
    }

    override fun onDestroyView() {
        handler.removeCallbacks(runnable)
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = SplashFragment()
    }
}