package com.example.appdiag

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import retrofit2.*
import java.io.ByteArrayOutputStream
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.converter.gson.GsonConverterFactory

class PanneauxActivity : AppCompatActivity() {

    private val REQUEST_IMAGE_CAPTURE = 1
    private var photoPart: MultipartBody.Part? = null
    private lateinit var imageViewPreview: ImageView
    private var intersectionId: Long = 1L // récupéré dynamiquement selon tes besoins

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_panneaux)

        imageViewPreview = findViewById(R.id.cameraIcon)

        val nomIntersection = intent.getStringExtra("intersection")
        val textView = findViewById<TextView>(R.id.nomTextView)
        textView.text = "Intersection : ${nomIntersection ?: "non définie"}"

        // 📷 Caméra
        imageViewPreview.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (intent.resolveActivity(packageManager) != null) {
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
            }
        }

        // ✅ Bouton Valider
        findViewById<Button>(R.id.btnValider).setOnClickListener {
            envoyerInfos()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val bitmap = data?.extras?.get("data") as? Bitmap
            imageViewPreview.setImageBitmap(bitmap)

            bitmap?.let {
                val stream = ByteArrayOutputStream()
                it.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                val bytes = stream.toByteArray()

                val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), bytes)
                photoPart = MultipartBody.Part.createFormData("photos", "image.jpg", requestBody)
            }
        }
    }

    private fun envoyerInfos() {
        val remarqueText = findViewById<EditText>(R.id.editRemarqueGaucheNord).text.toString()
        val remarque = RequestBody.create("text/plain".toMediaTypeOrNull(), remarqueText)

        val etatParts = mutableListOf<MultipartBody.Part>()

        val checkF = findViewById<CheckBox>(R.id.checkF1)
        val checkCa = findViewById<CheckBox>(R.id.checkCa1)
        val checkAA = findViewById<CheckBox>(R.id.checkAA1)
        val checkDF = findViewById<CheckBox>(R.id.checkDF1)

        if (checkF.isChecked) etatParts.add(MultipartBody.Part.createFormData("etats", "F"))
        if (checkCa.isChecked) etatParts.add(MultipartBody.Part.createFormData("etats", "Ca"))
        if (checkAA.isChecked) etatParts.add(MultipartBody.Part.createFormData("etats", "AA"))
        if (checkDF.isChecked) etatParts.add(MultipartBody.Part.createFormData("etats", "DF"))

        val photosList = if (photoPart != null) listOf(photoPart!!) else emptyList()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.250:8083/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        apiService.envoyerInfosPanneau(intersectionId, remarque, etatParts, photosList)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    Toast.makeText(this@PanneauxActivity, "✅ Données enregistrées", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@PanneauxActivity, "❌ Erreur : ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }
}
