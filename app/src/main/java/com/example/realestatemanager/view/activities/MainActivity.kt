package com.example.realestatemanager.view.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.realestatemanager.R
import com.example.realestatemanager.model.myObjects.RealEstateAgent
import com.example.realestatemanager.utils.idRealEstate
import com.example.realestatemanager.utils.intentIdAgent
import com.example.realestatemanager.view.fragments.DetailsFragment
import com.example.realestatemanager.view.fragments.ListApartmentFragment
import com.example.realestatemanager.view.fragments.ProfileFragment
import com.example.realestatemanager.view.myInterface.CommunicatorInterface
import com.example.realestatemanager.view.myInterface.OnButtonClickedListener
import com.example.realestatemanager.viewmodel.Injection
import com.example.realestatemanager.viewmodel.RealEstateAgentViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity(), OnButtonClickedListener,
    NavigationView.OnNavigationItemSelectedListener, CommunicatorInterface {

    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private var account: RealEstateAgent? = null
    private var id by Delegates.notNull<Int>()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var currentFragment: Fragment
    private lateinit var bottomNavigationView: BottomNavigationView
    private var mSelectedItem = 0
    private var tablet = false


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
        configureBottomNav()

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
        } else {
            startActivity(Intent(this, LogInActivity::class.java))
            finish()
        }
        configureElement()
        configureDrawerLayout()
        showMainFragment()
        configureSecondFragment()
    }

    private fun configureBottomNav() {
        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        val navController = findNavController(R.id.main_activity_frame_layout)
        navView.setupWithNavController(navController)
    }

    private fun configureSecondFragment() {
        var detailFragment =
            supportFragmentManager.findFragmentById(R.id.main_activity_frame_layout_details)

        tablet = findViewById<View>(R.id.main_activity_frame_layout_details) != null

        if (detailFragment == null && tablet) {
            detailFragment = DetailsFragment()
            val bundle = Bundle()
            bundle.putInt(idRealEstate, -1)
            detailFragment.arguments = bundle
            supportFragmentManager.beginTransaction()
                .add(R.id.main_activity_frame_layout_details, detailFragment)
                .commit()
        }
    }

    private fun showMainFragment() {
        val mainFragment = ListApartmentFragment()
        currentFragment = mainFragment
        supportFragmentManager.beginTransaction()
            .add(R.id.main_activity_frame_layout, mainFragment)
            .commit()
    }

    private fun configureElement() {
        toolbar = findViewById(R.id.include)
        setSupportActionBar(toolbar)

    }


    private fun updatePage(
        itemId: Int,
        fragment: Fragment
    ) {
        mSelectedItem = itemId
        currentFragment = fragment
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.main_activity_frame_layout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
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

    override fun onNavigationItemSelected(@NonNull item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.log_out_item -> sendBackToLogin()
            R.id.setting_item -> passId(id)
        }
        return true
    }


    private fun sendBackToLogin() {
        id = -1
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
            R.id.app_bar_search -> buildDialogSearch()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun buildDialogSearch(): Boolean {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_filter, null)
        dialogBuilder.setView(dialogView)
        val alertDialog = dialogBuilder.create()
        alertDialog.show()
        return true
    }

    override fun onButtonClicked(view: View?) {
        val toAddApartmentActivity = Intent(this, AddApartmentActivity::class.java)
        toAddApartmentActivity.putExtra(intentIdAgent, id)
        startActivity(toAddApartmentActivity)
    }

    override fun passData(input: Int) {
        if (tablet) {
            val bundle = Bundle()
            bundle.putInt(idRealEstate, input)
            val transaction = this.supportFragmentManager.beginTransaction()
            val mFragment = DetailsFragment()
            mFragment.arguments = bundle
            transaction.replace(R.id.main_activity_frame_layout_details, mFragment)
            transaction.commit()
        } else {
            val intent = Intent(this, DetailsActivity::class.java)
            intent.putExtra(idRealEstate, input)
            startActivity(intent)
        }
    }

    override fun passId(input: Int) {
        val bundle = Bundle()
        bundle.putInt("idRealEstateAgent", id)
        val transaction = this.supportFragmentManager.beginTransaction()
        val mFragment = ProfileFragment()
        mFragment.arguments = bundle
        transaction.replace(R.id.main_activity_frame_layout, mFragment)
        transaction.addToBackStack(null)
        transaction.commit()
        drawerLayout.closeDrawer(
            GravityCompat.START
        )
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.closeDrawer(
            GravityCompat.START
        ) else super.onBackPressed()
    }

}