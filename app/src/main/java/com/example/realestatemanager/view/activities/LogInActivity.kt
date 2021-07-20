package com.example.realestatemanager.view.activities

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import com.example.realestatemanager.R
import com.example.realestatemanager.model.myObjects.RealEstateAgent
import com.example.realestatemanager.viewmodel.Injection
import com.example.realestatemanager.viewmodel.RealEstateAgentViewModel
import com.google.android.material.dialog.MaterialDialogs

class LogInActivity : AppCompatActivity() {

    private lateinit var mailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var realEstateAgentViewModel: RealEstateAgentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initElements()
        initClick()
    }

    private fun initClick() {
        loginButton.setOnClickListener {
            if (!TextUtils.isEmpty(mailEditText.text.toString()) && !TextUtils.isEmpty(
                    passwordEditText.text.toString()
                )
            ) {
                realEstateAgentViewModel.loadRealEstateAgentAccount(
                    mailEditText.text.toString(),
                    passwordEditText.text.toString()
                )?.observe(this, Observer { account: RealEstateAgent? ->
                    if (account != null) {
                        if (account.mail == mailEditText.text.toString() && account.password == passwordEditText.text.toString()) {
                            intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("account", account.id)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        realEstateAgentViewModel.findExistingMail(mailEditText.text.toString())
                            .observe(this, Observer {
                                if (it == null) {
                                    buildDialog()
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


    private fun initElements() {
        mailEditText = findViewById(R.id.username)
        passwordEditText = findViewById(R.id.password)
        loginButton = findViewById(R.id.login)

        val viewModelFactory = Injection.provideViewModelFactory(this)
        realEstateAgentViewModel = ViewModelProviders.of(this, viewModelFactory).get(
            RealEstateAgentViewModel::class.java
        )
    }

    private fun buildDialog() {

        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_login, null)
        dialogBuilder.setView(dialogView)
        val cancel: Button = dialogView.findViewById(R.id.cancel_button_dialog)
        val register: Button = dialogView.findViewById(R.id.register_button_dialog)
        val alertDialog = dialogBuilder.create()
        alertDialog.show()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        register.setOnClickListener {
            val intent = Intent(this, ProfileSettingActivity::class.java)
            intent.putExtra("mail", mailEditText.text.toString())
            intent.putExtra("password", passwordEditText.text.toString())
            startActivity(intent)
            finish()
        }
        cancel.setOnClickListener {
            alertDialog.dismiss()
        }
    }
}