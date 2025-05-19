package com.alexproject.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn

class ChatActivity : AppCompatActivity() {

	// supported by layout/activity_chat.xml
	// 1) we allow users to send
	// 2 also for history, where we go over the history of chat.
    private lateinit var messageInput: EditText
    private lateinit var sendButton: Button
    private lateinit var chatHistory: TextView
	// Ideally, users should login before chatting. by default, it is anonymous.
    private var username: String = "Anonymous"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        messageInput = findViewById(R.id.messageInput)
        sendButton = findViewById(R.id.sendButton)
        chatHistory = findViewById(R.id.chatHistory)

        // Get signed-in user email or name
        val account = GoogleSignIn.getLastSignedInAccount(this)
        username = account?.email ?: account?.displayName ?: "Anonymous"

        sendButton.setOnClickListener {
			// Trim whitespace. Only add if message is not empty.
			//
			// Every time we send a message, we clear up the input, to allow users
			// to send a new message.
            val message = messageInput.text.toString().trim()
            if (message.isNotEmpty()) {
                chatHistory.append("\n$username: $message")
                messageInput.text.clear()
            }
        }
    }
}
