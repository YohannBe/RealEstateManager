package com.example.realestatemanager.view.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.annotation.NonNull
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.realestatemanager.R
import com.example.realestatemanager.databinding.DialogFilterBinding
import com.example.realestatemanager.model.myObjects.RealEstateAgent
import com.example.realestatemanager.utils.idRealEstate
import com.example.realestatemanager.utils.intentIdAgent
import com.example.realestatemanager.view.fragments.DetailsFragment
import com.example.realestatemanager.view.fragments.ListApartmentFragment
import com.example.realestatemanager.view.fragments.ProfileFragment
import com.example.realestatemanager.view.fragments.Simulator
import com.example.realestatemanager.view.myInterface.CommunicatorInterface
import com.example.realestatemanager.view.myInterface.OnButtonClickedListener
import com.example.realestatemanager.viewmodel.Injection
import com.example.realestatemanager.viewmodel.mainActivity.MainActivityViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.datepicker.MaterialDatePicker
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
    private var tablet = false
    private lateinit var binding: DialogFilterBinding
    private var listFilter = ArrayList<String>()
    private var valueFromPrice = -1
    private var valueToPrice = -1
    private var valueFromSurface = -1
    private var valueToSurface = -1
    private var filter = false
    private val listFilterMap = HashMap<String, String>()
    private lateinit var mainActivityViewModel: MainActivityViewModel
    private var closeButtonFragment: ImageButton? = null

    private val toLeft: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.to_left
        )
    }

    private val toRight: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.to_right
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        id = intent.getIntExtra("account", -1)
        val viewModelFactory = Injection.provideMainActivityViewModelFactory(this)
        mainActivityViewModel = ViewModelProviders.of(this, viewModelFactory).get(
            MainActivityViewModel::class.java
        )

        sharedPreferences = getSharedPreferences("MYACCOUNTSAVED", Context.MODE_PRIVATE)
        if (id == -1) {
            id = sharedPreferences.getInt("idSaved", -1)
        }
        configureBottomNav()

        if (id != -1) {

            val editor = sharedPreferences.edit()
            editor.putInt("idSaved", id)
            editor.apply()

            mainActivityViewModel.getMyAgent(id)
                .observe(this, { myAccount: RealEstateAgent? ->
                    account = myAccount
                })
        } else {
            startActivity(Intent(this, LogInActivity::class.java))
            finish()
        }
        configureElement()
        configureDrawerLayout()
        configureSecondFragment()

        configureFilter()
    }

    private fun configureFilter() {
        mainActivityViewModel.passFilter(
            false,
            listFilterMap,
            valueFromPrice,
            valueToPrice,
            valueFromSurface,
            valueToSurface
        )
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
            val secondFragmentLayout: FrameLayout =
                findViewById(R.id.main_activity_frame_layout_details)
            secondFragmentLayout.visibility = View.GONE
            detailFragment = DetailsFragment()
            val bundle = Bundle()
            bundle.putInt(idRealEstate, -1)
            detailFragment.arguments = bundle
            supportFragmentManager.beginTransaction()
                .add(R.id.main_activity_frame_layout_details, detailFragment)
                .commit()
        }
        closeButtonFragment = if (tablet) findViewById(R.id.closefragment_button) else null
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

    override fun onNavigationItemSelected(@NonNull item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.log_out_item -> sendBackToLogin()
            R.id.setting_item -> passId(id)
            R.id.simulator_item -> startSimulatorFragment()
        }
        return true
    }

    private fun startSimulatorFragment() {
        val transaction = this.supportFragmentManager.beginTransaction()
        val mFragment = Simulator()
        transaction.replace(R.id.main_activity_frame_layout, mFragment)
        transaction.addToBackStack(null)
        transaction.commit()
        drawerLayout.closeDrawer(
            GravityCompat.START
        )
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
        binding = DialogFilterBinding.inflate(layoutInflater)
        val dialogView = binding.root
        dialogBuilder.setView(dialogView)
        val alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        mainActivityViewModel.getFilter().observe(this, {
            if (it.getBoolean("filterPut")) {
                val listFilter: HashMap<String, String> =
                    it.getSerializable("mapFilter") as HashMap<String, String>
                val priceFrom: Int = it.getInt("priceFrom")
                val priceTo: Int = it.getInt("priceTo")
                val surfaceFrom: Int = it.getInt("surfaceFrom")
                val surfaceTo: Int = it.getInt("surfaceTo")
                if (listFilter.containsKey("appartment")) binding.chipApartmentapartment.isChecked =
                    true
                if (listFilter.containsKey("duplex")) binding.chipApartmentduplex.isChecked =
                    true
                if (listFilter.containsKey("house")) binding.chipApartmenthouse.isChecked = true
                if (listFilter.containsKey("loft")) binding.chipApartmentloft.isChecked = true
                if (listFilter.containsKey("villa")) binding.chipApartmentvilla.isChecked = true
                if (listFilter.containsKey("room1")) binding.chipRoom1.isChecked = true
                if (listFilter.containsKey("room2")) binding.chipRoom2.isChecked = true
                if (listFilter.containsKey("room3")) binding.chipRoom3.isChecked = true
                if (listFilter.containsKey("room4")) binding.chipRoom4.isChecked = true
                if (listFilter.containsKey("room5")) binding.chipRoom5.isChecked = true
                if (listFilter.containsKey("bedroom1")) binding.chipBedroom1.isChecked = true
                if (listFilter.containsKey("bedroom2")) binding.chipBedroom2.isChecked = true
                if (listFilter.containsKey("bedroom3")) binding.chipBedroom3.isChecked = true
                if (listFilter.containsKey("bedroom4")) binding.chipBedroom4.isChecked = true
                if (listFilter.containsKey("bedroom5")) binding.chipBedroom5.isChecked = true
                if (listFilter.containsKey("bathroom1")) binding.chipBathroom1.isChecked = true
                if (listFilter.containsKey("bathroom2")) binding.chipBathroom2.isChecked = true
                if (listFilter.containsKey("bathroom3")) binding.chipBathroom3.isChecked = true

                val values: List<Float> = listOf(priceFrom.toFloat(), priceTo.toFloat())
                binding.priceRangeSlider.values = values

                val valuesSurface: List<Float> =
                    listOf(surfaceFrom.toFloat(), surfaceTo.toFloat())
                binding.surfaceRangeSlider.values = valuesSurface


            }
        })
        binding.beforeTimePicker.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setTheme(R.style.ThemeOverlay_RealEstateManager_DatePicker)
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
            datePicker.show(supportFragmentManager, "tag")
            datePicker.addOnPositiveButtonClickListener {
                binding.afterTimePicker.chipBackgroundColor = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        this,
                        R.color.colorPrimaryBackground
                    )
                )
                binding.intervalTimePicker.chipBackgroundColor = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        this,
                        R.color.colorPrimaryBackground
                    )
                )
                binding.beforeTimePicker.chipBackgroundColor =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.chip_color))
                binding.textviewDateShow.text =
                    "" + binding.textviewDateShow.text + datePicker.headerText.toString()

                datePicker.selection

            }

            datePicker.addOnNegativeButtonClickListener {
                binding.beforeTimePicker.chipBackgroundColor = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        this,
                        R.color.colorPrimaryBackground
                    )
                )
                datePicker.dismiss()
            }
        }

        binding.intervalTimePicker.setOnClickListener {
            val dateRangePicker =
                MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText("Select dates")
                    .setTheme(R.style.ThemeOverlay_RealEstateManager_DatePicker)
                    .setSelection(
                        Pair(
                            MaterialDatePicker.thisMonthInUtcMilliseconds(),
                            MaterialDatePicker.todayInUtcMilliseconds()
                        )
                    )
                    .build()
            dateRangePicker.show(supportFragmentManager, "tag")
            dateRangePicker.addOnPositiveButtonClickListener {
                binding.afterTimePicker.chipBackgroundColor = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        this,
                        R.color.colorPrimaryBackground
                    )
                )
                binding.beforeTimePicker.chipBackgroundColor = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        this,
                        R.color.colorPrimaryBackground
                    )
                )
                binding.intervalTimePicker.chipBackgroundColor =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.chip_color))
                binding.textviewDateShow.text =
                    "" + binding.textviewDateShow.text + dateRangePicker.headerText.toString()
            }
            dateRangePicker.addOnNegativeButtonClickListener {
                binding.intervalTimePicker.chipBackgroundColor = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        this,
                        R.color.colorPrimaryBackground
                    )
                )
                dateRangePicker.dismiss()
            }
        }

        binding.afterTimePicker.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setTheme(R.style.ThemeOverlay_RealEstateManager_DatePicker)
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
            datePicker.show(supportFragmentManager, "tag")
            datePicker.addOnPositiveButtonClickListener {
                binding.beforeTimePicker.chipBackgroundColor = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        this,
                        R.color.colorPrimaryBackground
                    )
                )
                binding.intervalTimePicker.chipBackgroundColor = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        this,
                        R.color.colorPrimaryBackground
                    )
                )
                binding.afterTimePicker.chipBackgroundColor =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.chip_color))
                binding.textviewDateShow.text =
                    "" + binding.textviewDateShow.text + datePicker.headerText.toString()
            }

            datePicker.addOnNegativeButtonClickListener {
                binding.afterTimePicker.chipBackgroundColor = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        this,
                        R.color.colorPrimaryBackground
                    )
                )
                datePicker.dismiss()
            }
        }

        binding.buttonResetDate.setOnClickListener {
            binding.textviewDateShow.text = ""
            binding.beforeTimePicker.chipBackgroundColor =
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        this,
                        R.color.colorPrimaryBackground
                    )
                )
            binding.intervalTimePicker.chipBackgroundColor =
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        this,
                        R.color.colorPrimaryBackground
                    )
                )
            binding.afterTimePicker.chipBackgroundColor =
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        this,
                        R.color.colorPrimaryBackground
                    )
                )
        }

        binding.cancelButtonDialogFilter.setOnClickListener {
            filter = false
            alertDialog.dismiss()
        }

        binding.buttonResetDialog.setOnClickListener {
            mainActivityViewModel.passFilter(
                false,
                null, -1, -1, -1, -1
            )
            alertDialog.dismiss()
        }
        binding.buttonFilterDialog.setOnClickListener {
            listFilter = ArrayList<String>()
            listFilterMap.clear()

            if (binding.chipApartmentapartment.isChecked) listFilterMap["appartment"] =
                binding.chipApartmentapartment.text.toString()
            if (binding.chipApartmentduplex.isChecked) listFilterMap["duplex"] =
                binding.chipApartmentduplex.text.toString()
            if (binding.chipApartmenthouse.isChecked) listFilterMap["house"] =
                binding.chipApartmenthouse.text.toString()
            if (binding.chipApartmentloft.isChecked) listFilterMap["loft"] =
                binding.chipApartmentloft.text.toString()
            if (binding.chipApartmentvilla.isChecked) listFilterMap["villa"] =
                binding.chipApartmentvilla.text.toString()
            if (binding.chipRoom1.isChecked) listFilterMap["room1"] =
                binding.chipRoom1.text.toString()
            if (binding.chipRoom2.isChecked) listFilterMap["room2"] =
                binding.chipRoom2.text.toString()
            if (binding.chipRoom3.isChecked) listFilterMap["room3"] =
                binding.chipRoom3.text.toString()
            if (binding.chipRoom4.isChecked) listFilterMap["room4"] =
                binding.chipRoom4.text.toString()
            if (binding.chipRoom5.isChecked) listFilterMap["room5"] =
                binding.chipRoom5.text.toString()
            if (binding.chipBedroom1.isChecked) listFilterMap["bedroom1"] =
                binding.chipBedroom1.text.toString()
            if (binding.chipBedroom2.isChecked) listFilterMap["bedroom2"] =
                binding.chipBedroom2.text.toString()
            if (binding.chipBedroom3.isChecked) listFilterMap["bedroom3"] =
                binding.chipBedroom3.text.toString()
            if (binding.chipBedroom4.isChecked) listFilterMap["bedroom4"] =
                binding.chipBedroom4.text.toString()
            if (binding.chipBedroom5.isChecked) listFilterMap["bedroom5"] =
                binding.chipBedroom5.text.toString()
            if (binding.chipBathroom1.isChecked) listFilterMap["bathroom1"] =
                binding.chipBathroom1.text.toString()
            if (binding.chipBathroom2.isChecked) listFilterMap["bathroom2"] =
                binding.chipBathroom2.text.toString()
            if (binding.chipBathroom3.isChecked) listFilterMap["bathroom3"] =
                binding.chipBathroom3.text.toString()

            /*when {
                !TextUtils.isEmpty(binding.textviewDateShow.text) -> listFilter.add(binding.textviewDateShow.text.toString())
            }*/
            valueFromPrice = binding.priceRangeSlider.values[0].toInt()
            valueToPrice = binding.priceRangeSlider.values[1].toInt()
            valueFromSurface = binding.surfaceRangeSlider.values[0].toInt()
            valueToSurface = binding.surfaceRangeSlider.values[1].toInt()
            filter = true
            mainActivityViewModel.passFilter(
                filter,
                listFilterMap,
                valueFromPrice,
                valueToPrice,
                valueFromSurface,
                valueToSurface
            )
            alertDialog.dismiss()

        }
        return true
    }

    override fun onButtonClicked(view: View?) {
        val toAddApartmentActivity = Intent(this, AddApartmentActivity::class.java)
        toAddApartmentActivity.putExtra(intentIdAgent, id)
        startActivity(toAddApartmentActivity)
    }

    override fun passData(input: Int) {
        if (tablet) {
            closeButtonFragment?.visibility = View.VISIBLE

            val secondFragmentLayout: FrameLayout =
                findViewById(R.id.main_activity_frame_layout_details)
            if (secondFragmentLayout.visibility != View.VISIBLE) {
                secondFragmentLayout.startAnimation(toLeft)
            }
            secondFragmentLayout.visibility = View.VISIBLE
            closeButtonFragment?.setOnClickListener {
                secondFragmentLayout.startAnimation(toRight)
                secondFragmentLayout.visibility = View.GONE
                closeButtonFragment!!.visibility = View.GONE
            }
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