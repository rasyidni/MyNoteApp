package com.example.mynoteapp.ui.insert

import android.Manifest
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.mynoteapp.R
import com.example.mynoteapp.database.Note
import com.example.mynoteapp.databinding.ActivityNoteAddUpdateBinding
import com.example.mynoteapp.helper.DateHelper
import com.example.mynoteapp.helper.ViewModelFactory
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import java.text.SimpleDateFormat
import java.util.*

class NoteAddUpdateActivity : AppCompatActivity() {

    companion object {
        const val ALERT_DIALOG_CLOSE = 10
    }



    private lateinit var editTextDate: EditText
    private val calendar: Calendar = Calendar.getInstance()

    private lateinit var locationCallback: LocationCallback
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted.
                    Toast.makeText(this, "Akses lokasi diterima", Toast.LENGTH_SHORT).show()
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted.
                    Toast.makeText(this, "Akses lokasi diterima", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // No location access granted.
                    Toast.makeText(this, "Akses lokasi ditolak", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private var note: Note? = null

    private lateinit var noteAddUpdateViewModel: NoteAddUpdateViewModel

    private var _activityNoteAddUpdateBinding: ActivityNoteAddUpdateBinding? = null
    private val binding get () = _activityNoteAddUpdateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _activityNoteAddUpdateBinding = ActivityNoteAddUpdateBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        noteAddUpdateViewModel = obtainViewModel(this@NoteAddUpdateActivity)

        note = Note()

        val actionBarTitle: String = getString(R.string.add)
        val btnTitle: String = getString(R.string.save)

        supportActionBar?.title = actionBarTitle
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                locationResult ?: return

                for (location in locationResult.locations) {
                    // Update UI with location data
                    binding?.edtAlamat?.setText(getAddressFromLatLng(location.latitude, location.longitude))

                }
            }

        }

        binding?.btnAlamat?.setOnClickListener {
            startLocationUpdates()
        }

        editTextDate = findViewById(R.id.editTextDate)

        binding?.btnSubmit?.text = btnTitle

        binding?.btnSubmit?.setOnClickListener {

            val radioButtonId = binding?.radioGender?.checkedRadioButtonId
            val radioButtonGender = binding?.root?.findViewById<RadioButton>(radioButtonId!!)

            val nik = binding?.edtNik?.text.toString().trim()
            val nama = binding?.edtNama?.text.toString().trim()
            val nohp = binding?.edtNohp?.text.toString().trim()
            val gender = radioButtonGender?.text.toString()
            val alamat = binding?.edtAlamat?.text.toString().trim()
            val tanggaLahir = binding?.editTextDate?.text.toString().trim()
            when {
                nik.isEmpty() -> {
                    binding?.edtNik?.error = getString(R.string.empty)
                }
                nama.isEmpty() -> {
                    binding?.edtNama?.error = getString(R.string.empty)
                }
                nohp.isEmpty() -> {
                    binding?.edtNohp?.error = getString(R.string.empty)
                }
                gender.isEmpty() -> {
                    Toast.makeText(this, "Jenis kelamin tidak boleh kosong", Toast.LENGTH_SHORT).show()
                }
                alamat.isEmpty() -> {
                    binding?.edtAlamat?.error = getString(R.string.empty)
                }

                tanggaLahir.isEmpty() -> {
                    binding?.editTextDate?.error = getString(R.string.empty)
                }

                else -> {

                    note.let { note ->
                        note?.nik = nik
                        note?.nama = nama
                        note?.nohp = nohp
                        note?.gender = gender
                        note?.alamat = alamat
                        note?.tanggallahir = tanggaLahir
                    }
                        note.let { note ->
                            note?.tanggal = DateHelper.getCurrentDate()
                        }
                        noteAddUpdateViewModel.insert(note as Note)
                        showToast(getString(R.string.added))
//                    }
                    finish()
                }
            }
        }

    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> showAlertDialog(ALERT_DIALOG_CLOSE)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        showAlertDialog(ALERT_DIALOG_CLOSE)
    }

    private fun showAlertDialog(type: Int) {
        val isDialogClose = type == ALERT_DIALOG_CLOSE

        val dialogTitle: String = getString(R.string.cancel)
        val dialogMessage: String = getString(R.string.message_cancel)
        val alertDialogBuilder = AlertDialog.Builder(this)

        with(alertDialogBuilder){
            setTitle(dialogTitle)
            setMessage(dialogMessage)
            setCancelable(false)
            setPositiveButton(getString(R.string.yes)) { _, _ ->
                    noteAddUpdateViewModel.delete(note as Note)
                    showToast(getString(R.string.delete))
                finish()
            }
            setNegativeButton(getString(R.string.no)) {dialog, _ ->
                dialog.cancel()
            }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _activityNoteAddUpdateBinding = null
    }

    fun showDatePickerDialog(view: android.view.View) {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val selectedDate = formatDate(year, monthOfYear, dayOfMonth)
                editTextDate.setText(selectedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    private fun formatDate(year: Int, month: Int, day: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)

        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }


    private fun obtainViewModel(activity: AppCompatActivity) : NoteAddUpdateViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(NoteAddUpdateViewModel::class.java)
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
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

            requestLocationPermission()

            return
        }

        fusedLocationClient.requestLocationUpdates(
            LocationRequest.create().apply {
                interval = 60000L
                fastestInterval = 30000L
            },
            locationCallback,

            Looper.getMainLooper()  //Change thread to main thread
        )
    }

    //Check and request location permission
    private fun requestLocationPermission() {


        // ...

        // Before you perform the actual permission request, check whether your app
        // already has the permissions, and whether your app needs to show a permission
        // rationale dialog. For more details, see Request permissions.
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    fun getAddressFromLatLng(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        var address = ""

        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses?.isNotEmpty() == true) {
                val fetchedAddress = addresses[0]
                val stringBuilder = StringBuilder()

                for (i in 0..fetchedAddress.maxAddressLineIndex) {
                    stringBuilder.append(fetchedAddress.getAddressLine(i)).append(" ")
                }

                address = stringBuilder.toString()
            }
        } catch (e: Exception) {
            Log.e("Geocoding", "Error getting address from LatLng: ${e.message}")
        }

        return address
    }

    override fun onStart() {
        super.onStart()
        startLocationUpdates()
    }


}