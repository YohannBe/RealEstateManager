package com.example.realestatemanager.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import androidx.lifecycle.ViewModelProviders
import com.example.realestatemanager.databinding.ActivitySettingBinding
import com.example.realestatemanager.model.myObjects.RealEstateAgent
import com.example.realestatemanager.viewmodel.Injection
import com.example.realestatemanager.viewmodel.setting.SettingViewModel

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val viewModelFactory = Injection.provideSettingViewModelFactory(this)
        val settingViewModel = ViewModelProviders.of(this, viewModelFactory).get(
            SettingViewModel::class.java
        )

        settingViewModel.getMyAgent(intent.getIntExtra("idRealEstateAgent", -1))
            .observe(this, {
                binding.firstnameEdittextSetting.setText(it.firstName)
                binding.lastnameEdittextSetting.setText(it.lastName)
                binding.mailEdittextSetting.setText(it.mail)

                binding.buttonModifyProfile.setOnClickListener { view ->
                    var mPassword = it.password
                    if (!TextUtils.isEmpty(binding.oldPasswordEdittextSetting.text.toString()) && !TextUtils.isEmpty(
                            binding.newPasswordEdittextSetting.text.toString()
                        )
                    )
                        mPassword = binding.newPasswordEdittextSetting.text.toString()
                    val realEstateAgent = RealEstateAgent(
                        it.id,
                        binding.firstnameEdittextSetting.text.toString(),
                        binding.lastnameEdittextSetting.text.toString(),
                        mPassword,
                        binding.mailEdittextSetting.text.toString()
                    )
                    settingViewModel.updateMyAgent(realEstateAgent)

                    finish()
                }
            })

    }
}