package com.example.projecttwo

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import java.util.*


private const val TAG = "GeocacheFragment"
class GeocacheFragment: Fragment() {
    private lateinit var geocache: Geocache
    private lateinit var geocacheNameField: EditText
    private lateinit var geocacheCoords: Button
    private lateinit var geocacheCoordsDisplay: TextView
    private lateinit var geocacheHidingSpot: EditText

    // stars for rating
    private lateinit var star1: ImageView
    private lateinit var star2: ImageView
    private lateinit var star3: ImageView
    private lateinit var star4: ImageView
    private lateinit var star5: ImageView

    private lateinit var locationClient: FusedLocationProviderClient

    private val geocacheDetailViewModel: GeocacheDetailViewModel by lazy {
        ViewModelProviders.of(this).get(GeocacheDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        geocache = Geocache()
        val id: UUID = arguments?.getSerializable("geocache_id") as UUID
        geocacheDetailViewModel.loadGeocache(id)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        locationClient = LocationServices.getFusedLocationProviderClient(requireActivity().applicationContext)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_geocache, container, false)
        geocacheNameField = view.findViewById(R.id.geocache_name_field) as EditText
        geocacheCoords = view.findViewById(R.id.geocache_coords_btn) as Button
        geocacheCoordsDisplay = view.findViewById(R.id.geocache_city) as TextView
        geocacheHidingSpot = view.findViewById(R.id.geocache_hiding_spot) as EditText

        star1 = view.findViewById(R.id.star1) as ImageView
        star2 = view.findViewById(R.id.star2) as ImageView
        star3 = view.findViewById(R.id.star3) as ImageView
        star4 = view.findViewById(R.id.star4) as ImageView
        star5 = view.findViewById(R.id.star5) as ImageView

        geocacheCoords.setOnClickListener {
            updateLocation()
        }

        setStarListeners()

        return view
    }

    private fun updateLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity().applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity().applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
                Log.e(TAG, "Location Permissions wasnt enabled")
            return
        }

        val currentLocTask = locationClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
        val geocoder = Geocoder(requireActivity().applicationContext, Locale.getDefault())

        currentLocTask.addOnCompleteListener {
            if (it.isSuccessful) {
                val converted = geocoder.getFromLocation(it.result.latitude, it.result.longitude, 1)
                val cityName = converted[0].locality
                val stateName = converted[0].adminArea
                val countryCode = converted[0].countryCode
                val full = "$cityName, $stateName, $countryCode"
                val latLong = "${it.result?.latitude}, ${it.result?.longitude}"
                geocache.coords = latLong
                geocache.city = full
                geocacheCoordsDisplay.text = "${it.result?.latitude}, ${it.result?.longitude}"
            } else {
                Log.e(TAG, "task was not successful")
            }
        }
        // For getting old location. requires a location update for lastLocation to change
//        locationClient.lastLocation.addOnSuccessListener { location: Location? ->
//            geocache.coords = location
//            val geocoder = Geocoder(requireActivity().applicationContext, Locale.getDefault())
//            val addresses: List<Address> = geocoder.getFromLocation(location!!.latitude, location!!.longitude, 1)
//            val cityName = addresses[0].locality
//            val stateName = addresses[0].adminArea
//            val countryName = addresses[0].countryCode
//            val full = "$cityName, $stateName, $countryName"
//            Log.d(TAG, "Location test: ${location?.latitude}, ${location?.longitude}")
//            Log.d(TAG, "City: $cityName State: $stateName Country: $countryName")
//        }.addOnFailureListener { err: Exception? ->
//            Log.e(TAG, "Location did not succeed: $err")
//        }
    }

    private fun setStarListeners() {
        star1.setOnClickListener {
            geocache.rating = 1
            star1.setImageResource(R.drawable.star_full)
            setStars(arrayOf(star2, star3, star4, star4, star5), "empty")
        }

        star2.setOnClickListener {
            geocache.rating = 2
            setStars(arrayOf(star1, star2), "fill")
            setStars(arrayOf(star3, star4, star5), "empty")
        }

        star3.setOnClickListener {
            geocache.rating = 3
            setStars(arrayOf(star1, star2, star3), "fill")
            setStars(arrayOf(star4, star5), "empty")
        }

        star4.setOnClickListener {
            geocache.rating = 4
            setStars(arrayOf(star1, star2, star3, star4), "fill")
            setStars(arrayOf(star5), "empty")
        }

        star5.setOnClickListener {
            geocache.rating = 5
            setStars(arrayOf(star1, star2, star3, star4, star5), "fill")
        }
    }

    private fun setStars(stars: Array<ImageView>, action: String) {
        if (action == "fill") {
            for (star in stars) {
                star.setImageResource(R.drawable.star_full)
            }
        } else if (action == "empty") {
            for (star in stars) {
                star.setImageResource(R.drawable.star_empty)
            }
        } else {
            Log.e(TAG, "$action is not a setStars action")
        }
    }

    override fun onStart() {
        super.onStart()
        val nameWatcher = object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                geocache.name = p0.toString()
            }
        }

        val hidingWatcher = object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                geocache.hidingPlace = p0.toString()
            }
        }
        geocacheNameField.addTextChangedListener(nameWatcher)
        geocacheHidingSpot.addTextChangedListener(hidingWatcher)
    }

    override fun onStop() {
        super.onStop()
        geocacheDetailViewModel.saveGeocache(geocache)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        geocacheDetailViewModel.geocacheData.observe(
            viewLifecycleOwner,
            Observer { geocache ->
                geocache?.let {
                    this.geocache = geocache
                    updateUI()
                }
            }
        )
    }

    private fun updateUI() {
        geocacheNameField.setText(geocache.name)
        geocacheHidingSpot.setText(geocache.hidingPlace)
        geocacheCoordsDisplay.text = geocache.coords

        when (geocache.rating) {
            0 -> setStars(arrayOf(), "fill")
            1 -> setStars(arrayOf(star1), "fill")
            2 -> setStars(arrayOf(star1, star2), "fill")
            3 -> setStars(arrayOf(star1, star2, star3), "fill")
            4 -> setStars(arrayOf(star1, star2, star3, star4), "fill")
            5 -> setStars(arrayOf(star1, star2, star3, star4, star5), "fill")
            else -> {
                Log.e(TAG, "Unexpected geocache.rating: ${geocache.rating}")
            }
        }
    }

    companion object {
        fun newInstance(geocacheId: UUID): GeocacheFragment {
            val args = Bundle()
            args.putSerializable("geocache_id", geocacheId)
            val fragment = GeocacheFragment()
            fragment.arguments = args
            return fragment
        }
    }
}