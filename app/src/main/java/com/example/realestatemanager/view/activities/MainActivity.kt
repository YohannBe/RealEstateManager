package com.example.realestatemanager.view.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.realestatemanager.R
import com.example.realestatemanager.model.myObjects.RealEstateAgent
import com.example.realestatemanager.view.fragments.ListApartmentFragment
import com.example.realestatemanager.view.myInterface.OnButtonClickedListener
import com.example.realestatemanager.viewmodel.Injection
import com.example.realestatemanager.viewmodel.RealEstateAgentViewModel
import com.google.android.material.navigation.NavigationView
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity(), OnButtonClickedListener,
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private var account: RealEstateAgent? = null
    private var id by Delegates.notNull<Int>()
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        id = intent.getIntExtra("account", -1)
        val viewModelFactory = Injection.provideViewModelFactory(this)
        val realEstateAgentViewModel = ViewModelProviders.of(this, viewModelFactory).get(
            RealEstateAgentViewModel::class.java
        )
        sharedPreferences = getSharedPreferences("MYACCOUNTSAVED", Context.MODE_PRIVATE)
        if (id == -1) {
            id = sharedPreferences.getInt("idSaved", -1)
        }

        Toast.makeText(this, id.toString(), Toast.LENGTH_SHORT).show()
        if (id != -1) {

                val editor = sharedPreferences.edit()
                editor.putInt("idSaved", id)
                editor.apply()

            realEstateAgentViewModel.getMyAgent(id)
                .observe(this, Observer { myAccount: RealEstateAgent? ->
                    account = myAccount
                    Toast.makeText(this, account?.firstName, Toast.LENGTH_SHORT).show()
                    Toast.makeText(this, account?.lastName, Toast.LENGTH_SHORT).show()
                })
        }
        else {
            startActivity(Intent(this, LogInActivity::class.java))
            finish()
        }
        configureElement()
        configureDrawerLayout()
        showMainFragment()

    }

    private fun showMainFragment() {
        val mainFragment = ListApartmentFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_activity_frame_layout, mainFragment)
            .commit()
    }

    private fun configureElement() {
        toolbar = findViewById(R.id.include)
        setSupportActionBar(toolbar)

    }

    private fun configureDrawerLayout() {
        this.drawerLayout = findViewById(R.id.main_activity_drawer_layout)
        val toggle =
            ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
            )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navigationView = findViewById(R.id.main_activity_navigation_view)
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(@NonNull item:MenuItem): Boolean{

        when(item.itemId) {
            R.id.log_out_item -> sendBackToLogin()
        }
        return true
    }



    private fun sendBackToLogin() {
        id =-1
        val editor = sharedPreferences.edit()
        editor.putInt("idSaved", id)
        editor.apply()
        startActivity(Intent(this, LogInActivity::class.java))
        finish()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.app_bar_search -> {

                super.onOptionsItemSelected(item)
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onButtonClicked(view: View?) {
        val toAddApartmentActivity = Intent(this, AddApartmentActivity::class.java)
        toAddApartmentActivity.putExtra("idAgent", id)
        startActivity(toAddApartmentActivity)
    }
}