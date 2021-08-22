package com.example.realestatemanager.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.realestatemanager.R
import com.example.realestatemanager.databinding.ActivityProfileSettingBinding
import com.example.realestatemanager.model.myObjects.RealEstateAgent
import com.example.realestatemanager.viewmodel.Injection
import com.example.realestatemanager.viewmodel.RealEstateAgentViewModel

class ProfileSettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileSettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileSettingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val realEstateAgentViewModel = initViewModel()

        initElements()

        binding.finishRegisterButton.setOnClickListener {
            val agent = RealEstateAgent(
                binding.firstnameEdittext.text.toString(),
                binding.lastnameEdittext.text.toString(),
                binding.passwordEdittext.text.toString(),
                binding.mailEdittext.text.toString()
            )
            realEstateAgentViewModel.insertAgent(agent)
            realEstateAgentViewModel.loadRealEstateAgentAccount(
                binding.mailEdittext.text.toString(),
                binding.passwordEdittext.text.toString()
            ).observe(this,
                {
                    if (it != null) {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("account", it.id)
                        startActivity(intent)
                        finish()
                    }
                })
        }
    }

    private fun initElements() {
        val mail = intent.getStringExtra("mail")
        val password = intent.getStringExtra("password")
        binding.mailEdittext.setText(mail)
        binding.passwordEdittext.setText(password)
    }

    private fun initViewModel(): RealEstateAgentViewModel {
        val viewModelFactory = Injection.provideViewModelFactory(this)
        return ViewModelProviders.of(this, viewModelFactory).get(
            RealEstateAgentViewModel::class.java
        )
    }
}