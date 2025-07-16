package com.example.appdiag

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.flexbox.FlexboxLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var autoCompleteRue: AutoCompleteTextView
    private lateinit var titreIntersections: TextView
    private lateinit var intersectionContainer: FlexboxLayout
    private val intersectionsMap = mutableMapOf<String, List<String>>()

    // ✅ DTO pour Retrofit (dans ce même fichier ou en fichier séparé)
    data class RueResponse(val rue: String, val intersections: List<String>)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        titreIntersections = findViewById(R.id.titreIntersections)
        intersectionContainer = findViewById(R.id.intersectionContainer)
        autoCompleteRue = findViewById(R.id.autoCompleteRue)

        val adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_dropdown_item_1line,
            mutableListOf()
        )
        autoCompleteRue.setAdapter(adapter)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.250:8083/") // change ceci avec ton URL réelle
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        // 🔁 Appel API sur changement de texte
        autoCompleteRue.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length >= 2) {
                    apiService.getSuggestions(s.toString())
                        .enqueue(object : Callback<List<String>> {
                            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                                if (response.isSuccessful) {
                                    val suggestions = response.body() ?: emptyList()
                                    adapter.clear()
                                    adapter.addAll(suggestions)
                                    adapter.notifyDataSetChanged()
                                    autoCompleteRue.showDropDown()
                                }
                            }

                            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                                t.printStackTrace()
                            }
                        })
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })

        // Gérer la sélection
        autoCompleteRue.setOnItemClickListener { _, _, position, _ ->
            val rueChoisie = adapter.getItem(position)
            val intersections = intersectionsMap[rueChoisie]
            afficherIntersections(intersections)
        }
    }

    private fun afficherIntersections(intersections: List<String>?) {
        intersectionContainer.removeAllViews()

        if (!intersections.isNullOrEmpty()) {
            titreIntersections.visibility = View.VISIBLE

            intersections.forEach { nom ->
                val bouton = Button(this).apply {
                    text = nom
                    textSize = 14f
                    setAllCaps(false)
                    setTextColor(Color.BLACK)
                    setBackgroundResource(R.drawable.button_background)
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        topMargin = 8
                    }

                    setOnClickListener {
                        val intent = Intent(this@MainActivity, PanneauxActivity::class.java)
                        intent.putExtra("intersection", nom)
                        startActivity(intent)
                    }
                }
                intersectionContainer.addView(bouton)
            }
        } else {
            titreIntersections.visibility = View.GONE
        }
    }
}
