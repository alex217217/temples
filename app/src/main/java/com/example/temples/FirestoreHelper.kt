// This file serves 2 purposes.
// 1) for setting up a standard for each Synagouge, required fields, their formats, including defaults for emtpy fields.
// 2) allows for storage of synagogues
// 3) allows for users to dynamically add new synagogues.
package com.alexproject.app

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore


// integrate firestore storage.
// The database follows the following format:
//    val name        : String,
//    val address     : String,
//    val denomination: String,
//    val website     : String,
//    val phone       : String,
//    val email       : String?,
//    val services    : String,
//    val location    : Location
)

//data class Location(
//    val latitude : Double,
//    val longitude: Double
//)
//
// Please note that database name used here is synagouges.
// in Future, we may add a new databse for locations and service times, which may follow a different format.

object FirestoreHelper {

    private val db = FirebaseFirestore.getInstance()
    private const val COLLECTION_NAME = "synagogues"

    fun saveSynagogue(synagogue: Synagogue, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection(COLLECTION_NAME)
            .add(synagogue)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

	// this is for loading from database.
	// needed for dynamic list of synagogues.
	// allows users to dynamically load and store new synagouges
    fun loadSynagogues(onComplete: (List<Synagogue>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection(COLLECTION_NAME)
            .get()
            .addOnSuccessListener { result ->
                val list = result.mapNotNull { it.toObject(Synagogue::class.java) }
                onComplete(list)
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreHelper", "Failed to load synagogues", e)
                onFailure(e)
            }
    }
}
