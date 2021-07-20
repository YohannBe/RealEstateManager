package com.example.realestatemanager.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.realestatemanager.R
import com.example.realestatemanager.model.myObjects.RealEstateAgent
import com.example.realestatemanager.viewmodel.Injection
import com.example.realestatemanager.viewmodel.RealEstateAgentViewModel

class ProfileSettingActivity : AppCompatActivity() {

    private lateinit var firstName: EditText
    private lateinit var lastName: EditText
    private lateinit var pMail: EditText
    private lateinit var pPassword: EditText
    private lateinit var register: Button
    private lateinit var realEstateAgentViewModel: RealEstateAgentViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_setting)

        val viewModelFactory = Injection.provideViewModelFactory(this)
        realEstateAgentViewModel = ViewModelProviders.of(this, viewModelFactory).get(
            RealEstateAgentViewModel::class.java
        )

        firstName = findViewById(R.id.firstname_edittext)
        lastName = findViewById(R.id.lastname_edittext)
        pMail = findViewById(R.id.mail_edittext)
        pPassword = findViewById(R.id.password_edittext)

        val mail = intent.getStringExtra("mail")
        val password = intent.getStringExtra("password")

        pMail.setText(mail)
        pPassword.setText(password)

        register = findViewById(R.id.finish_register_button)

        register.setOnClickListener{
            val agent =RealEstateAgent(firstName.text.toString(), lastName.text.toString(), pPassword.text.toString(), pMail.text.toString())
            realEstateAgentViewModel.insertAgent(agent)
            realEstateAgentViewModel.loadRealEstateAgentAccount(pMail.text.toString() ,pPassword.text.toString()).observe(this, Observer {
                if (it != null) {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("account", it.id)
                    startActivity(intent)
                    finish()
                }
            })
        }

    }
}