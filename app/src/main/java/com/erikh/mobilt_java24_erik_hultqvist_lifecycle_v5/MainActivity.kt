package com.erikh.mobilt_java24_erik_hultqvist_lifecycle_v5

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore


class MainActivity : AppCompatActivity() {
    private val TAG = "ERIK"
    lateinit var loginButton : Button
    lateinit var loginName : TextInputEditText
    lateinit var loginPassword : EditText
    lateinit var registerTextView : TextView

    lateinit var main : ConstraintLayout
    lateinit var bf:BlankFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        loginButton = findViewById<Button>(R.id.button)
        loginName = findViewById<TextInputEditText>(R.id.textInputEditText)
        loginPassword = findViewById<EditText>(R.id.editTextTextPassword)

        loadLoginFields()

        var theme = "light"
        loginButton.setOnClickListener {
            val inputEmail = loginName.text.toString()
            val inputPassword = loginPassword.text.toString()

            val db = Firebase.firestore

            db.collection("users")
                .whereEqualTo("email", inputEmail)
                .get()
                .addOnSuccessListener { documents ->

                    if (documents.isEmpty) {
                        Toast.makeText(this, "Login Failed: Email not found", Toast.LENGTH_LONG).show()
                    } else {
                        var loginSuccess = false
                        var matchedUserId: String? = null

                        for (document in documents) {
                            val storedPassword = document.getString("password")
                            if (storedPassword == inputPassword) {
                                loginSuccess = true
                                matchedUserId = document.id
                                theme = document.getString("theme") ?: "Light"

                                break
                            }
                        }

                        if (loginSuccess && matchedUserId != null) {
                            Toast.makeText(this, "Login Success!!!", Toast.LENGTH_SHORT).show()

                            saveLoginFields(inputEmail)

                            val intent = Intent(this, MainActivity2::class.java)
                            intent.putExtra("FIRESTORE_ID", matchedUserId)
                            intent.putExtra("THEME_MODE", theme)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Login Failed: Incorrect password", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error querying user", exception)
                    Toast.makeText(this, "Login Failed: Database error", Toast.LENGTH_LONG).show()
                }
        }

        registerTextView = findViewById<TextView>(R.id.textView3)

        bf= BlankFragment()
        registerTextView.setOnClickListener {
            supportFragmentManager.commit {
                add(R.id.fragmentContainerView, bf)
            }
        }

        main = findViewById(R.id.main)
        main.setOnClickListener {
            supportFragmentManager.commit {
                remove(bf)
            }
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onPause() {
        super.onPause()
        val email = loginName.text.toString()
        val password = loginPassword.text.toString()
        saveLoginFields(email)
    }

    private fun saveLoginFields(email: String) {
        val prefs = getSharedPreferences("login_prefs", MODE_PRIVATE)
        prefs.edit()
            .putString("email", email)
            .apply()
    }

    private fun loadLoginFields() {
        val prefs = getSharedPreferences("login_prefs", MODE_PRIVATE)
        loginName.setText(prefs.getString("email", ""))
    }


}