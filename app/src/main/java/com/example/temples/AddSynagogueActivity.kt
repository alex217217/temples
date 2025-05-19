package com.alexproject.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

// Dynamically add a new synaguge, and update external storage (firestore, etc.)
// Some fields are optional.
//
// Stragegy is taken from Models.kt file where defaults are set for empty fields.
//
//
// Locations default are taken from Google map API. Although users can override
// the defdaults for the new synagugle added.
class AddSynagogueActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_synagogue)

        val nameEditText = findViewById<EditText>(R.id.editTextName)
		//required
        val addressEditText = findViewById<EditText>(R.id.editTextAddress)
		//required
        val denominationEditText = findViewById<EditText>(R.id.editTextDenomination)
		// optional
        val websiteEditText = findViewById<EditText>(R.id.editTextWebsite)
		// optional
        val phoneEditText = findViewById<EditText>(R.id.editTextPhone)
		// optional
        val emailEditText = findViewById<EditText>(R.id.editTextEmail)
		// optional
        val servicesEditText = findViewById<EditText>(R.id.editTextServices)

        val saveButton = findViewById<Button>(R.id.buttonSave)
        saveButton.setOnClickListener {
            val resultIntent = Intent().apply {
                putExtra("name", nameEditText.text.toString())
                putExtra("address", addressEditText.text.toString())
                putExtra("denomination", denominationEditText.text.toString())
                putExtra("website", websiteEditText.text.toString())
                putExtra("phone", phoneEditText.text.toString())
                putExtra("email", emailEditText.text.toString())
                putExtra("services", servicesEditText.text.toString())
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}
