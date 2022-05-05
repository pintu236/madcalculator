package com.demo.madcalculator.login

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.demo.madcalculator.MainActivity
import com.demo.madcalculator.NetworkResponse
import com.demo.madcalculator.R
import com.demo.madcalculator.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater);
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java];

        if (viewModel.isUserLoggedIn()) {
            startActivity(MainActivity.createIntent(this))
            finishAffinity()
        }

        binding.btnSubmit.setOnClickListener {
            if (binding.edtEmail.text?.isEmpty() == true) {
                showMessage(getString(R.string.msg_enter_mail));
                return@setOnClickListener
            } else if (binding.edtPassword.text?.isEmpty() == true) {
                showMessage(getString(R.string.msg_enter_password));
                return@setOnClickListener
            } else {
                viewModel.loginUser(
                    this,
                    binding.edtEmail.text.toString().trim(),
                    binding.edtPassword.text.toString().trim()
                );
            }
        }

        viewModel.loginLiveData.observe(this) { response ->
            when (response.status) {
                NetworkResponse.Status.LOADING -> {
                    setLoadingState(true)
                }
                NetworkResponse.Status.ERROR -> {
                    setLoadingState(false)
                    showMessage(response.message)
                }
                NetworkResponse.Status.SUCCESS -> {
                    setLoadingState(false)
                    startActivity(MainActivity.createIntent(this))
                    finishAffinity()
                }
            }
        }

    }

    private fun setLoadingState(state: Boolean) {
        if (state) {
            binding.btnSubmit.text = "";
            binding.progressCircular.visibility = View.VISIBLE
        } else {
            binding.btnSubmit.text = getString(R.string.submit);
            binding.progressCircular.visibility = View.GONE
        }

    }

    private fun showMessage(string: String?) {
        if (string == null) return
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
    }
}