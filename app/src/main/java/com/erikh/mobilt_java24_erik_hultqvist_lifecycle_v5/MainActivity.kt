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
import androidx.constraintlayout.widget.ConstraintSet
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
                        // No user with this email found
                        Toast.makeText(this, "Login Failed: Email not found", Toast.LENGTH_LONG).show()
                    } else {
                        // User found, check password
                        var loginSuccess = false
                        var matchedUserId: String? = null
                        /*var theme = "Light"*/

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
}

/*
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
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

*/
/*Godkänt kriterier:

Första Activiten: En login sida som gå vidare till nästa Activity/fragment när man loggar in. (hårdkodat credentials är ok minst 2 inputs t.ex lösen ,password, username, personnummer, telenummer)
Andra Activiten/fragment:

ett “formulär” som låter användaren fylla i 5 olika typer av data/ui
componenter & kan skicka/submita datan. (t.ex. åldern : skriv in
siffror ,  har körkort : tickar in en checkbox, radiobox för olika
alternativ, email: textinput )
Ni ska dessutom skapa en meny (av
valfri typ som funkar som en meny) som kan navigara till alla
Activities/fragment när man är inloggad. (credentials finns skapat sedan innan/hårdkodat är ok)
Ha en custom icon för appen.
Spara data:  visar upp någon sparad data om man har fyllt i, om den finns , ska spara den om man pausar/lägger den i "app tray" + när man dödar appen också.
Har koden skriven i Kotlin (minst 50% kodradsmässigt).


Väl godkänt kriterier uppfyll 3 av 6 punkterna :

Extra Activiten/fragment för en registreringssida med input för credentials t.ex lösen , email , användarnamn , personnummer .
Använd
Regex eller custom kod för att validera inputen från användaren för
respektive inputfält, går ej att submitta annars (android kan också
kolla utan hjälp från regexen, men ha majoriteten med regex).
Sparar alla saker man har fyllt i om man skulle backa, spara den aktiv inputen som man hann fylla i sist. (hint:SavedInstanceState eller SharedPreferences)
Spara
all data även när man stänger appen & ha autoinlogg så att man kan
    skippa skriva in credentials igen (hint: vissa UI element spara info by
default, SavedInstanceState eller
SharedPreferences) eller firebaseRealtimeDatabase/firestore
Ha koden 100% helt i kotlin.
Använd en databas för att spara credentials / personuppgifter (säg till att andra kan testa den).
--*//*


class MainActivity : AppCompatActivity() {
    private val TAG = "ERIK"
    lateinit var loginButton : Button
    lateinit var loginName : TextInputEditText
    lateinit var loginPassword : EditText
    lateinit var registerTextView : TextView

    */
/*lateinit var main : ConstraintLayout
    lateinit var bf:BlankFragment*//*



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        loginButton = findViewById<Button>(R.id.button)
        loginName = findViewById<TextInputEditText>(R.id.textInputEditText)
        loginPassword = findViewById<EditText>(R.id.editTextTextPassword)
        loginButton.setOnClickListener {
            val inputEmail = loginName.text.toString()
            val inputPassword = loginPassword.text.toString()

            val db = Firebase.firestore

            db.collection("users")
                .whereEqualTo("email", inputEmail)
                .get()
                .addOnSuccessListener { documents ->

                    if (documents.isEmpty) {
                        // No user with this email found
                        Toast.makeText(this, "Login Failed: Email not found", Toast.LENGTH_LONG).show()
                    } else {
                        // User found, check password
                        var loginSuccess = false
                        var matchedUserId: String? = null
                        var theme = "Light"

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

                            val intent = Intent(this, MainActivity2::class.java)
                            intent.putExtra("FIRESTORE_ID", matchedUserId)
                            intent.putExtra("THEME_MODE", theme)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Login Failed: Incorrect password", Toast.LENGTH_LONG).show()
                        }

                        */
/*for (document in documents) {
                            val storedPassword = document.getString("password")
                            if (storedPassword == inputPassword) {
                                loginSuccess = true
                                break
                            }
                        }
                        if (loginSuccess) {
                            Toast.makeText(this, "Login Success!!!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity2::class.java)
                            //intent.putExtra("USER_EMAIL", inputEmail)
                            intent.putExtra("FIRESTORE_ID", documents.documents[0].id)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "Login Failed: Incorrect password", Toast.LENGTH_LONG).show()
                        }*//*

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
*/
