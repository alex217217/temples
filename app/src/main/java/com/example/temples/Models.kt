package com.alexproject.app

data class Synagogue(
	// REQUIRED
    val name: String,
	// REQUIRED
    val address: String,
	// REQUIRED
    val denomination: String,
	// OPTIONAL
    val website: String,
	// OPTIONAL
    val phone: String,
	// OPTIONAL
    val email: String?,
	// Timetable for pryaers times.
	// In future we may support an embedded class to allow
	// a) Morning prayers
	// b) evening prayers.
    val services: String,
	// The following embedded field
    val location: Location
)

// The following was done in order to give user the option to override the default location
// reporeted by google maps API when the user adds a new synagouge.

// This is an embedded field.
// Firestore enforce this constraint
// Extra care must be taken when a user dynamically ads a new synagogue.
data class Location(
    val latitude: Double,
    val longitude: Double
)
