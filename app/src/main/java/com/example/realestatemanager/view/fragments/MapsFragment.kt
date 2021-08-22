package com.example.realestatemanager.view.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.realestatemanager.R
import com.example.realestatemanager.model.myObjects.RealEstate
import com.example.realestatemanager.utils.getLocationByAddress
import com.example.realestatemanager.view.myInterface.CommunicatorInterface
import com.example.realestatemanager.viewmodel.Injection
import com.example.realestatemanager.viewmodel.mainActivity.MainActivityViewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class MapsFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private var myLocation: LatLng? = null
    private lateinit var mGoogleMap: GoogleMap
    private lateinit var mContext: Context
    private lateinit var fusedLocationProvider: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var mapsViewModel: MainActivityViewModel
    private lateinit var communicatorInterface: CommunicatorInterface

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        communicatorInterface = activity as CommunicatorInterface
    }

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->

        mGoogleMap = googleMap
        val androidLocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val androidLocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location?) {
                if (location != null) {
                    myLocation = LatLng(location.latitude, location.longitude)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation!!, 15.0f))
                }
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            }

            override fun onProviderEnabled(provider: String?) {
            }

            override fun onProviderDisabled(provider: String?) {
            }

        }
        if (!hasLocationPermission()) {
            requestLocationPermission()
            return@OnMapReadyCallback
        } else {
            androidLocationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000, 5f,
                androidLocationListener
            )
        }

        if (myLocation == null)
            getLastLocation()

    }

    private fun hasLocationPermission(): Boolean =
        EasyPermissions.hasPermissions(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )


    private fun requestLocationPermission() {
        EasyPermissions.requestPermissions(
            this,
            "You need to accept the location in order to let the app work correctly",
            132,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_maps, container, false)
        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(mContext)
        mapsViewModel = activity?.let {
            ViewModelProviders.of(it, Injection.provideMainActivityViewModelFactory(it)).get(
                MainActivityViewModel::class.java
            )
        }!!

        mapsViewModel.getAllApartment().observe(viewLifecycleOwner, { fullList ->
            mapsViewModel.getFilter().observe(viewLifecycleOwner, {
                updateMarker(mapsViewModel.updateListFilter(it, fullList))
            })
        })

        return view
    }

    private fun updateMarker(listRealEstate: List<RealEstate>) {
        mGoogleMap.clear()
        for (i in listRealEstate.indices) {
            val sentence: String =
                listRealEstate[i].numberStreet.toString() + " " + listRealEstate[i].address + " " +
                        listRealEstate[i].city + " " + listRealEstate[i].zipcode + " " + listRealEstate[i].country
            val markerOptions = MarkerOptions()
            val mLatLng = getLocationByAddress(mContext, sentence)
            if (mLatLng != null) {
                markerOptions.position(mLatLng).title(sentence)
                mGoogleMap.addMarker(markerOptions).tag = listRealEstate[i].id.toString()
            }
        }
        mGoogleMap.setOnMarkerClickListener { marker ->
            communicatorInterface.passData(marker.tag.toString().toInt())
            return@setOnMarkerClickListener false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (!hasLocationPermission()) {
            requestLocationPermission()
            return
        }
        fusedLocationProvider.lastLocation.addOnCompleteListener { task ->
            val location = task.result
            if (location != null) {
                myLocation = LatLng(location.latitude, location.longitude)
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation!!, 15.0f))
            } else
                getNewLocation()
        }
    }

    private fun getNewLocation() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 2
        if (ActivityCompat.checkSelfPermission(
                mContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
            return
        }
        fusedLocationProvider.requestLocationUpdates(
            locationRequest, locationCallback,
            Looper.myLooper()
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult?) {
            super.onLocationResult(p0)
            var lastLocation = p0?.lastLocation
            if (lastLocation != null) {
                myLocation = LatLng(lastLocation.latitude, lastLocation.longitude)
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation!!, 15.0f))
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(requireActivity()).build().show()
        } else
            requestLocationPermission()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Toast.makeText(mContext, "permission granted", Toast.LENGTH_SHORT).show()
    }
}