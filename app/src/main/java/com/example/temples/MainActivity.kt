// Change the project name to alexproject
package com.alexproject.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.alexproject.app.Synagogue
import com.alexproject.app.Location

// Very important is the following standsard to update a synagogue.
// The following convention is enforced both by firestore and by the code here.
// Each synaogoue has the following data:
//
//    val name        : String
//    val address     : String
//    val denomination: String
//    val website     : String
//    val phone       : String
//    val email       : String
//    val services    : String
//    val location    : Location
//
// Please note, that location has the following fields:
//    val latitude: Double
//    val longitude: Double

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

	// instant for google map
    private lateinit var googleMap: GoogleMap

	// count of how many synaogues were found.
	// 1) either by loading from firestore.
	// 2) when a new synagogue is added by a user, we update the count.
    private lateinit var countTextView: TextView

	// Please note, we have 3 buttons we overlay on the map.
	// 1) for register
	// 2) for login
	// 3) for logout.
	//
	// Please note, that login is necessary only for adding a new synagogue.
	// A user can scan the synagoues, without any authorization.
	//
	//
	// Authorization is supported, by:
	// 1) username and password
	// 2) by using 3rd party-authentication, like Google, etc.
    private lateinit var registerButton: Button
    private lateinit var loginButton: Button
    private lateinit var logoutButton: Button

	// A user can add a Synagogue, after authorization. We plan to overlay the button on the map itself.
    private lateinit var addSynagogueButton: Button
    private lateinit var googleSignInClient: GoogleSignInClient

	// By default, we show the location in melboune, and we show all the marks of all synaogues on melbourne.
    private val melbourne = LatLng(-37.8136, 144.9631)
    private val zoomLevel = 12f

	// This is only for backup.
	//
	// Synaogogues are read from firestore.
	// For offline view, we show the following synagogues.
    private val synagogues = mutableListOf(
        Synagogue("East Melbourne Hebrew Congregation", "488 Albert Street, East Melbourne VIC 3002", "Orthodox", "https://www.melbournecitysynagogue.com", "+61 3 9662 1372", "office@melbournecitysynagogue.com", "Regular Shabbat and festival services. Contact for daily schedule.", Location(-37.811, 144.984)),
        Synagogue("Temple Beth Israel", "76–82 Alma Road, St Kilda VIC 3182", "Progressive", "https://tbi.org.au", "+61 3 9537 3495", null, "Friday: 6:15 PM, Saturday: 10:00 AM", Location(-37.867, 144.984)),
        Synagogue("Yeshivah Centre", "92 Hotham Street, St Kilda East VIC 3183", "Orthodox (Chabad-Lubavitch)", "https://www.yeshivahcentre.org", "+61 3 9522 8222", "info@yeshivahcentre.org", "Regular daily and Shabbat services. Contact for schedule.", Location(-37.867, 145.0)),
        Synagogue("Elwood Talmud Torah Hebrew Congregation", "39 Dickens Street, Elwood VIC 3184", "Orthodox", "https://www.elwoodshule.org", "+61 3 9531 1547", "office@elwoodshule.org", "Daily morning and afternoon services. Contact for times.", Location(-37.88, 144.985)),
        Synagogue("Ark Centre", "7 Cato Street, Hawthorn East VIC 3123", "Modern Orthodox", "https://www.arkcentre.com.au", "+61 3 9272 5611", "info@arkcentre.com.au", "Offers regular services. Contact for specific times.", Location(-37.828, 145.038))
    )

	// this is used both for register and login
    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
		// we Support Oauth2 for authentication.
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
		// We have to find a strategy for failure.
		// Users are identifiied as Anonymous either for chat purposes or for scanning synagugoes
        try {
            val account = task.result
            Toast.makeText(this, "Signed in as ${account.email}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Sign in failed", Toast.LENGTH_SHORT).show()
        }
    }

    private val addSynagogueLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val data = result.data!!
            val newSynagogue = Synagogue(
				//Please note, that each instance should comply with the following
				//data class Synagogue(
				//	val name: String,
				//	val address: String,
				//	val denomination: String,
				//	val website: String,
				//	val phone: String,
				//	val email: String?,
				//	val services: String,
				//	val location: Location
				//)

				//data class Location(
				//	val latitude: Double,
				//	val longitude: Double
				//)

                name = data.getStringExtra("name") ?: "",
                address = data.getStringExtra("address") ?: "",
                denomination = data.getStringExtra("denomination") ?: "",
                website = data.getStringExtra("website") ?: "",
                phone = data.getStringExtra("phone") ?: "",
                email = data.getStringExtra("email") ?: "",
                services = data.getStringExtra("services") ?: "",
				// By default, we get the location for a new synagugoe from google maps API. but user can override it.
                location = Location(
                    data.getDoubleExtra("latitude", 0.0),
                    data.getDoubleExtra("longitude", 0.0)
                )
            )
            synagogues.add(newSynagogue)
            updateMapMarkers()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
		// Entry point, we set up the map
		// Overlay the buttons.
		// Move the camera to Melbourne.
        setContentView(R.layout.activity_main)


		// A button for chat. we will implement it later
        val chatButton = findViewById<Button>(R.id.button_chat)
		chatButton.setOnClickListener {
			val intent = Intent(this, ChatActivity::class.java)
			startActivity(intent)
		}

		// number of synagugoes found
        countTextView = findViewById(R.id.synagogueCountTextView)
		// button for register
        registerButton = findViewById(R.id.registerButton)
		// button for login
        loginButton = findViewById(R.id.loginButton)
		// button for logout
        logoutButton = findViewById(R.id.logoutButton)
        addSynagogueButton = findViewById(R.id.addSynagogueButton)

        addSynagogueButton.setOnClickListener {
            val intent = Intent(this, AddSynagogueActivity::class.java)
            addSynagogueLauncher.launch(intent)
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

//		implemenation for register
        registerButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            signInLauncher.launch(signInIntent)
        }

		// implementation for login
        loginButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            signInLauncher.launch(signInIntent)
        }

        logoutButton.setOnClickListener {
            googleSignInClient.signOut().addOnCompleteListener {
                Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show()
            }
        }

		// by Default, the app shows gooele maps and then moves the camera to show Melbounre.
		// User can change view.
		// This is important, because the user can move the location, and add a new synoagogue.
		// We prefer to do it visually, becasue it gives a visual feedback for user, as soon as he adds a new synaoguge.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
		// This is tricky! because firestore connection can fail.
		// We either load from firestore, or add statically from the list of synagugoes stores on disk
        updateMapMarkers()
    }

	// We mark on the map all the synaogues found.
	// Marker is put on the file.
	// This view is dynamically updated whenever the user adds a new synagogue.
	//
	//
    private fun updateMapMarkers() {
        googleMap.clear()
        for (synagogue in synagogues) {
			// Each marker has title taken from name.
			// Please note that latitude and longitude are embedded fields inside locations.
			// This is important when dynamically adding a new synaoguge.
            val latLng = LatLng(synagogue.location.latitude, synagogue.location.longitude)
            googleMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(synagogue.name)
                    .snippet(synagogue.address)
            )
        }

		// We show by default the location in melboune.
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(melbourne, zoomLevel))


		// We should update the count of synaogouges found.
        countTextView.text = "Synagogues listed: ${synagogues.size}"
    }
}
