package com.example.appdiag

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MainActivity : AppCompatActivity() {

    private lateinit var map: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Config OSMDroid
        Configuration.getInstance().load(applicationContext, androidx.preference.PreferenceManager.getDefaultSharedPreferences(applicationContext))
        Configuration.getInstance().userAgentValue = packageName

        setContentView(R.layout.activity_main)

        // Initialisation de la carte
        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        // Centrage sur un point
        val startPoint = GeoPoint(48.8583, 2.2944) // Tour Eiffel
        val mapController = map.controller
        mapController.setZoom(15.0)
        mapController.setCenter(startPoint)

        // Marqueur
        val marker = Marker(map)
        marker.position = startPoint
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = "Tour Eiffel"


        map.overlays.add(marker)


    }

    override fun onResume() {
        super.onResume()
        map.onResume() // nécessaire pour OSMDroid
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }


}
