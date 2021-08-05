package com.example.realestatemanager.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.realestatemanager.R
import com.example.realestatemanager.utils.idRealEstate
import com.example.realestatemanager.view.fragments.DetailsFragment
import com.example.realestatemanager.view.myInterface.CommunicatorInterface

class DetailsActivity : AppCompatActivity(), CommunicatorInterface {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val id = intent.getIntExtra(idRealEstate, -1)
        passData(id)
    }

    override fun passData(input: Int) {
        val bundle = Bundle()
        bundle.putInt(idRealEstate, input)
        val transaction = this.supportFragmentManager.beginTransaction()
        val mFragment = DetailsFragment()
        mFragment.arguments = bundle
        transaction.add(R.id.fragment_detail_try, mFragment)
        transaction.commit()
    }

    override fun passId(input: Int) {

    }
}