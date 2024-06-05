package com.nafi.nafstory.ui

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.nafi.nafstory.R
import com.nafi.nafstory.data.DataRepository
import com.nafi.nafstory.data.remote.network.ApiClient
import com.nafi.nafstory.databinding.ActivityMapsBinding
import com.nafi.nafstory.utils.NetworkResult
import com.nafi.nafstory.utils.PrefsManager
import com.nafi.nafstory.utils.ViewModelFactory
import com.nafi.nafstory.viewmodel.MapsViewModel

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var viewModel: MapsViewModel
    private lateinit var binding: ActivityMapsBinding
    private lateinit var prefsManager: PrefsManager
    private var token: String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefsManager = PrefsManager(this)
        val dataRepository = DataRepository(ApiClient.getInstance())
        viewModel = ViewModelProvider(this, ViewModelFactory(dataRepository))[MapsViewModel::class.java]
        fetchData(prefsManager.token)
        token = prefsManager.token

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val dicodingSpace = LatLng(-6.8957643, 107.6338462)
        mMap.addMarker(
            MarkerOptions()
                .position(dicodingSpace)
                .title("Dicoding Space")
                .snippet("Batik Kumeli No.50")
        )
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(dicodingSpace, 15f))

        getMyLocation()
        token?.let {
            viewModel.apply {
                fetchListStory(it)
                responseListStory.observe(this@MapsActivity) {
                    when (it) {
                        is NetworkResult.Success -> {
                            if (it.data?.listStory != null) {
                                it.data?.listStory.forEach { tourism ->
                                    val latLng = LatLng(tourism.lat!!, tourism.lon!!)
                                    mMap.addMarker(
                                        MarkerOptions().position(latLng).title(tourism.name)
                                    )
                                }
                            }
                        }

                        is NetworkResult.Loading -> {
//                        setLoadingState(true)
//                        binding.swipeRefresh.isRefreshing = true
                        }

                        is NetworkResult.Error -> {
//                        setLoadingState(false)
//                        binding.rvStory.visibility = View.GONE
//                        binding.tvNotFound.visibility = View.GONE
//                        binding.tvError.visibility = View.VISIBLE
//                        binding.btnTry.visibility = View.VISIBLE
//                        binding.swipeRefresh.isRefreshing = false
                        }
                    }
                }

            }
        }
//        addManyMarker()
    }

    data class TourismPlace(
        val name: String,
        val latitude: Double,
        val longitude: Double
    )

    private fun addManyMarker() {
        val tourismPlace = listOf(
            TourismPlace("Floating Market Lembang", -6.8168954,107.6151046),
            TourismPlace("The Great Asia Africa", -6.8331128,107.6048483),
            TourismPlace("Rabbit Town", -6.8668408,107.608081),
            TourismPlace("Alun-Alun Kota Bandung", -6.9218518,107.6025294),
            TourismPlace("Orchid Forest Cikole", -6.780725, 107.637409),
        )
        tourismPlace.forEach { tourism ->
            val latLng = LatLng(tourism.latitude, tourism.longitude)
            mMap.addMarker(MarkerOptions().position(latLng).title(tourism.name))
        }
    }
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }
    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    private fun fetchData(auth: String) {
//        viewModel.apply {
//            fetchListStory(auth)
//            responseListStory.observe(this@MapsActivity) {
//                when(it) {
//                    is NetworkResult.Success -> {
//                        if(it.data?.listStory != null) {
//                            it.data?.listStory.forEach { tourism ->
//                                val latLng = LatLng(tourism.lat!!, tourism.lon!!)
//                                mMap.addMarker(MarkerOptions().position(latLng).title(tourism.name))
//                            }
//                        }
//                    }
//                    is NetworkResult.Loading -> {
////                        setLoadingState(true)
////                        binding.swipeRefresh.isRefreshing = true
//                    }
//                    is NetworkResult.Error -> {
////                        setLoadingState(false)
////                        binding.rvStory.visibility = View.GONE
////                        binding.tvNotFound.visibility = View.GONE
////                        binding.tvError.visibility = View.VISIBLE
////                        binding.btnTry.visibility = View.VISIBLE
////                        binding.swipeRefresh.isRefreshing = false
//                    }
//                }
//            }
//
//        }
    }

}