package com.example.kleineyt.fragments.loginRegister

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.kleineyt.R
import com.example.kleineyt.activities.ShoppingActivity
import com.example.kleineyt.databinding.FragmentLoginBinding
import com.example.kleineyt.dialog.setupBottomSheetDialog
import com.example.kleineyt.util.Resource
import com.example.kleineyt.viewmodel.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login){

    lateinit var binding : FragmentLoginBinding
    private val viewModel : LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvDontHaveAnAccount.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

            binding.apply {
                buttonLoginLogin.setOnClickListener {
                    if(!edEmailLogin.text.isEmpty()&&!edPasswordLogin.text.isEmpty())
                    {
                        val email = edEmailLogin.text.toString().trim()
                        val password = edPasswordLogin.text.toString()
                        viewModel.login(email, password)
                    }

                }

            }

        binding.tvForgotPassword.setOnClickListener {
            setupBottomSheetDialog { email ->
                viewModel.resetPassword(email)
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.resetPassword.collect{
                    when(it){
                        is Resource.Loading -> {

                        }
                        is Resource.Success -> {
                            Snackbar.make(requireView(), "Reset password link sent to your email", Snackbar.LENGTH_LONG).show()
                        }
                        is Resource.Error -> {
                            Snackbar.make(requireView(), "Error : ${it.message}", Snackbar.LENGTH_LONG).show()
                        }
                        else -> Unit
                    }
                }
            }
        }

            lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                    viewModel.login.collect{
                        when(it) {
                            is Resource.Loading -> {
                                binding.buttonLoginLogin.startAnimation()
                            }

                            is Resource.Success -> {
                                binding.buttonLoginLogin.revertAnimation()
                                Intent(
                                    requireActivity(),
                                    ShoppingActivity::class.java
                                ).also { intent ->
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                }
                            }

                            is Resource.Error -> {
                                binding.buttonLoginLogin.revertAnimation()
                                Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                            }
                            else -> Unit
                        }
                    }
                }
            }


        }
    }
