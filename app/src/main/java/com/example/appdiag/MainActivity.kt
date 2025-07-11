package com.example.appdiag

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var map: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Config OSMDroid
        Configuration.getInstance().load(applicationContext, PreferenceManager.getDefaultSharedPreferences(applicationContext))
        Configuration.getInstance().userAgentValue = packageName

        setContentView(R.layout.activity_main)

        // Initialisation de la carte
        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        // Liste d'adresses à géocoder
        val addresses = listOf(
            "2 Avenue des Acacias, Paris, France",
            "3 Allée Adamov, Paris, France",
            "6 Allée d'Alembert, Paris, France",
            "10 Square Allais, Paris, France"
        )
        val geocoder = Geocoder(this, Locale.getDefault())

        // Géocodage en arrière-plan
        Thread {
            for (address in addresses) {
                try {
                    val results = geocoder.getFromLocationName("$address, France", 1)
                    if (!results.isNullOrEmpty()) {
                        val location = results[0]
                        val geoPoint = GeoPoint(location.latitude, location.longitude)

                        runOnUiThread {
                            addMarkerWithPopup(geoPoint, address)
                            if (address == addresses[0]) {
                                map.controller.setZoom(16.0)
                                map.controller.setCenter(geoPoint)
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("GeocodeError", "Failed to geocode $address: ${e.message}")
                }
            }
        }.start()
    }

    // Fonction pour ajouter un marqueur avec popup
    private fun addMarkerWithPopup(point: GeoPoint, label: String) {
        val marker = Marker(map)
        marker.position = point
        marker.title = label
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

        marker.setOnMarkerClickListener { m, _ ->
            AlertDialog.Builder(this)
                .setTitle("Panneau consulté ?")
                .setMessage("Avez-vous consulté le panneau à : $label ?")
                .setPositiveButton("Oui") { _, _ ->
                    m.icon = ContextCompat.getDrawable(this, R.drawable.marker_checked)
                    m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    map.invalidate()
                }
                .setNegativeButton("Non", null)
                .show()
            true
        }

        map.overlays.add(marker)
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }
}
