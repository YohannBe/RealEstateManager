package com.example.realestatemanager.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.example.realestatemanager.databinding.ActivityLoginBinding
import com.example.realestatemanager.model.myObjects.RealEstateAgent
import com.example.realestatemanager.utils.buildDialogRegistration
import com.example.realestatemanager.viewmodel.Injection
import com.example.realestatemanager.viewmodel.login.LoginViewModel

class LogInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initViewModel()
        initClick(initViewModel())
    }

    private fun initClick(loginViewModel: LoginViewModel) {
        binding.login.setOnClickListener {
            if (!TextUtils.isEmpty(binding.username.text.toString()) && !TextUtils.isEmpty(
                    binding.password.text.toString()
                )
            ) {
                loginViewModel.loadRealEstateAgentAccount(
                    binding.username.text.toString(),
                    binding.password.text.toString()
                ).observe(this, { account: RealEstateAgent? ->
                    if (account != null) {
                        if (account.mail == binding.username.text.toString() && account.password == binding.password.text.toString()) {
                            intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("account", account.id)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        loginViewModel.findExistingMail(binding.username.text.toString())
                            .observe(this, {
                                if (it == null) {
                                    buildDialogRegistration(
                                        this,
                                        binding.username,
                                        binding.password,
                                        this
                                    )
                                } else {
                                    Toast.makeText(
                                        this,
                                        "Mail exist but the password is wrong",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            })
                    }
                })
            } else Toast.makeText(this, "Empty fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initViewModel(): LoginViewModel {
        val viewModelFactory = Injection.provideLoginViewModelFactory(this)
        return ViewModelProviders.of(this, viewModelFactory).get(
            LoginViewModel::class.java
        )
    }

}