package com.example.appdiag

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Paint
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.widget.Toast
import android.widget.ImageView

class PanneauxActivity : AppCompatActivity() {

    private val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_panneaux)

        val spinner: Spinner = findViewById(R.id.spinnerSituation)

        val etats = listOf(
            "🌟 Super état",
            "✅ Bon état",
            "⚠️ Endommagé",
            "❌ Hors d’usage"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, etats)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // 🔙 Action de retour
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Panneaux"
        // ✅ Récupérer le nom de l’intersection

        val nomIntersection = intent.getStringExtra("intersection")
        val textView = findViewById<TextView>(R.id.nomTextView)
        textView?.apply {
            text = "Intersection : ${nomIntersection ?: "non définie"}"
            paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
        }

        // 📸 Gestion de la caméra
        val cameraIcon = findViewById<ImageView>(R.id.cameraIcon)
        cameraIcon.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            } else {
                Toast.makeText(this, "Impossible d’ouvrir la caméra", Toast.LENGTH_SHORT).show()
            }
        }
    }
    //🔙 Gestion du bouton "retour"
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
    // 🔐 Permissions
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission accordée", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permission refusée", Toast.LENGTH_SHORT).show()
        }
    }
}