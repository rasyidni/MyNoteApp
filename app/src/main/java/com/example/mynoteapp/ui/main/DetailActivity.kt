package com.example.mynoteapp.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mynoteapp.R
import com.example.mynoteapp.database.Note
import com.example.mynoteapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    private var data: Note? = null

    companion object{
        val EXTRA_NOTE = "extra note"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Detail Pemilih"

        data = intent.getParcelableExtra(EXTRA_NOTE)

        with(binding) {
            if(data != null){
                tvNama.text = data?.nama
                tvAlamat.text = data?.alamat
                tvGender.text= data?.gender
                tvNik.text = data?.nik
                tvNohp.text = data?.nohp
                tvTanggallahir.text = data?.tanggallahir
            }
        }
    }
}