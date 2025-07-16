package com.example.appdiag

data class PanneauRequest(
    val intersection: String,
    val position: String,   // Exemple : "Gauche-Nord"
    val remarque: String,
    val types: List<String> // Exemples : ["F", "AA"]
)