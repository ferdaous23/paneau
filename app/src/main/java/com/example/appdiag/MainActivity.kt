package com.example.appdiag

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.preference.PreferenceManager
import com.google.android.flexbox.FlexboxLayout
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import android.graphics.Color

class MainActivity : AppCompatActivity() {

    private lateinit var autoCompleteRue: AutoCompleteTextView
    private lateinit var titreIntersections: TextView
    private lateinit var intersectionContainer: FlexboxLayout
    private lateinit var map: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        autoCompleteRue = findViewById(R.id.autoCompleteRue)
        titreIntersections = findViewById(R.id.titreIntersections)
        intersectionContainer = findViewById(R.id.intersectionContainer)

        // Données source : liste d’intersections
        val intersectionsMap = mapOf(
            "Avenue des AUBEPINES" to listOf("LAFARGUE P.", "VIOLETTES (des)", "ORMES (des)"),
            "Rue AUDAT (P.)" to listOf("LATTRE DE TASSIGNY (Mal de)", "ACCES PARKING", "VOIE SANS ISSUE", "VOIE SANS ISSUE", "ACCES TENNIS"),
            "Allée AUDIBERTI J." to listOf("MELIES G.", "EINSTEIN A."),
            "Avenue des AULNES" to listOf("REPUBLIQUE (de la)", "SOLIDARITE (de la)", "VAILLANT C.")
        )

        // Demande de permission localisation
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }

        // Remplir l'AutoCompleteTextView
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, intersectionsMap.keys.toList())
        autoCompleteRue.setAdapter(adapter)

        // Gérer la sélection
        autoCompleteRue.setOnItemClickListener { _, _, position, _ ->
            val rueChoisie = adapter.getItem(position)
            val intersections = intersectionsMap[rueChoisie]
            afficherIntersections(intersections)
        }

        // Initialisation de la carte
        map = findViewById(R.id.map)

        Configuration.getInstance().load(applicationContext, PreferenceManager.getDefaultSharedPreferences(applicationContext))

        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setBuiltInZoomControls(true)
        map.setMultiTouchControls(true)

        val mapController = map.controller
        mapController.setZoom(15.0)
        val startPoint = GeoPoint(48.8583, 2.2944) // Tour Eiffel
        mapController.setCenter(startPoint)

        // ✅ Ajout de la position actuelle sur la carte
        val myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), map)
        myLocationOverlay.enableMyLocation()
        map.overlays.add(myLocationOverlay)

        // ✅ Centrage automatique sur position GPS
        myLocationOverlay.runOnFirstFix {
            runOnUiThread {
                map.controller.animateTo(myLocationOverlay.myLocation)
            }
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
                }
                intersectionContainer.addView(bouton)
            }
        } else {
            titreIntersections.visibility = View.GONE
        }
    }
}
