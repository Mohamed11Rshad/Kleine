package com.example.kleineyt.fragments.loginRegister

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.kleineyt.R
import com.example.kleineyt.activities.LoginRegisterActivity
import com.example.kleineyt.databinding.FragmentSplashBinding
import com.example.kleineyt.viewmodel.IntroductionViewModel

class SplashScreen : Fragment() {

    lateinit var binding: FragmentSplashBinding
    val viewModel by lazy {
        IntroductionViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashBinding.inflate(inflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


            Handler().postDelayed({
                findNavController().navigate(R.id.action_splashScreen_to_introductionFragment)
            }, 1500)

    }

}