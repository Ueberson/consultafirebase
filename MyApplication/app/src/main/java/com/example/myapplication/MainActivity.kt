package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private lateinit var etPlaca: AppCompatEditText
    private lateinit var tvStatus: AppCompatTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etPlaca = findViewById(R.id.etPlaca)
        tvStatus = findViewById(R.id.tvStatus)

    }

    //Vamos mudar a função quando colocarmos o texto no "res/string"
    private fun verificarStatus() {
        if (etPlaca.text.isNullOrEmpty()) {
            Snackbar.make(tvStatus, "Preencha a placa", Snackbar.LENGTH_LONG).show()
        } else {
            val message = "A placa ${etPlaca.text} está regular"
            tvStatus.text = message
            tvStatus.visibility = View.VISIBLE
            etPlaca.text!!.clear()
        }
    }
}
