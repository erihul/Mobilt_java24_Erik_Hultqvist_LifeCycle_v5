// MainActivity2.kt
package com.erikh.mobilt_java24_erik_hultqvist_lifecycle_v5

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class MainActivity2 : AppCompatActivity() {
    private val TAG = "ERIK"

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var saveBtn: Button
    private lateinit var name: EditText
    private lateinit var age: EditText
    private lateinit var phone: EditText
    private lateinit var newsletter: CheckBox
    private lateinit var themeSpinner: Spinner

    private var userFireId: String? = null
    private var email: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply user theme before super.onCreate
        when (intent.getStringExtra("THEME_MODE")) {
            "Dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        userFireId = intent.getStringExtra("FIRESTORE_ID")
        if (userFireId == null) {
            finish()
            return
        }

        initViews()
        loadUserData()

        saveBtn.setOnClickListener {
            saveProfileData()
        }

        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_logout -> {
                    Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show()

                    // Reset theme to default Light
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initViews() {
        saveBtn = findViewById(R.id.btn_save_profile)
        name = findViewById(R.id.input_name)
        age = findViewById(R.id.input_age)
        phone = findViewById(R.id.input_phone)
        newsletter = findViewById(R.id.checkbox_newsletter)
        themeSpinner = findViewById(R.id.spinner_theme)

        toolbar = findViewById(R.id.toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun loadUserData() {
        val db = Firebase.firestore
        userFireId?.let { id ->
            db.collection("users")
                .document(id)
                .get()
                .addOnSuccessListener { doc ->
                    email = doc.getString("email")
                    findViewById<TextView>(R.id.toolbarTitle).text = "Logged in as $email"

                    name.setText(doc.getString("name") ?: "")
                    age.setText(doc.getLong("age")?.toString() ?: "")
                    phone.setText(doc.getString("phone") ?: "")
                    newsletter.isChecked = doc.getBoolean("newsletter") ?: false

                    val theme = doc.getString("theme") ?: "Light"
                    val options = resources.getStringArray(R.array.theme_colors)
                    val index = options.indexOf(theme)
                    if (index != -1) themeSpinner.setSelection(index)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveProfileData() {
        val db = Firebase.firestore

        val updatedTheme = themeSpinner.selectedItem.toString()
        val updatedData = mapOf(
            "name" to name.text.toString(),
            ("age" to age.text.toString().toIntOrNull() ?: 0) as Pair<String, Any>,
            "phone" to phone.text.toString(),
            "newsletter" to newsletter.isChecked,
            "theme" to updatedTheme
        )

        userFireId?.let { id ->
            db.collection("users").document(id)
                .update(updatedData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile saved", Toast.LENGTH_SHORT).show()

                    val newThemeMode = when (updatedTheme) {
                        "Dark" -> AppCompatDelegate.MODE_NIGHT_YES
                        else -> AppCompatDelegate.MODE_NIGHT_NO
                    }

                    if (newThemeMode != AppCompatDelegate.getDefaultNightMode()) {

                        intent.putExtra("THEME_MODE", updatedTheme)

                        AppCompatDelegate.setDefaultNightMode(newThemeMode)
                        recreate()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save profile", Toast.LENGTH_SHORT).show()
                }
        }
    }
}



/*
package com.erikh.mobilt_java24_erik_hultqvist_lifecycle_v5

import android.content.Intent
import  android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore


val TAG = "ERIK"
lateinit var loggedText : TextView

private lateinit var drawerLayout: DrawerLayout
private lateinit var navView: NavigationView
private lateinit var toolbar: Toolbar
private lateinit var saveBtn: Button
private lateinit var name : EditText
private lateinit var age : EditText
private lateinit var newsletter : CheckBox
private lateinit var theme : Spinner

private var email: String? = null


class MainActivity2 : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        val themeMode = intent.getStringExtra("THEME_MODE") ?: "Light"
        when (themeMode) {
            "Dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)

        val userFireId = intent.getStringExtra("FIRESTORE_ID")
        Log.i(TAG, "onCreate: UserFireId: " + userFireId)

        if (userFireId != null) {

            saveBtn = findViewById<Button>(R.id.btn_save_profile)

            saveBtn.setOnClickListener {
                userFireId?.let { id ->
                    saveProfileData(id)
                }
            }

            Firebase.firestore.collection("users")
                .document(userFireId!!)
                .get()
                .addOnSuccessListener { doc ->
                    email = doc.getString("email")
                    val password = doc.getString("password")

                    val name = doc.getString("name")
                    val age = doc.getLong("age")?.toInt()
                    val phone = doc.getString("phone")
                    val newsletter = doc.getBoolean("newsletter") ?: false
                    val theme = doc.getString("theme") ?: "Light"

                    Log.i(TAG, "onCreate: firestorID: $userFireId email: $email password: $password")
                    val toolbarTitle = findViewById<TextView>(R.id.toolbarTitle)

                    toolbarTitle.text = "Logged in as $email"

                    findViewById<EditText>(R.id.input_name).setText(name ?: "")
                    findViewById<EditText>(R.id.input_age).setText(age?.toString() ?: "")
                    findViewById<EditText>(R.id.input_phone).setText(phone ?: "")
                    findViewById<CheckBox>(R.id.checkbox_newsletter).isChecked = newsletter

                    val themeSpinner = findViewById<Spinner>(R.id.spinner_theme)
                    val themeOptions = resources.getStringArray(R.array.theme_colors)
                    val themeIndex = themeOptions.indexOf(theme)
                    if (themeIndex != -1) {
                        themeSpinner.setSelection(themeIndex)
                        */
/*when (theme) {
                            "Light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                            "Dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        }*//*

                    }
                }
                .addOnFailureListener {
                    Log.e(TAG, "Failed to load user profile", it)
                    Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show()
                }

            drawerLayout = findViewById(R.id.drawer_layout)
            navView = findViewById(R.id.nav_view)
            toolbar = findViewById(R.id.toolbar)

            setSupportActionBar(toolbar)
            supportActionBar?.title = ""


            val toggle = ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
            )
            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()

            navView.setNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.nav_profile -> {
                        Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show()
                    }
                    */
/*R.id.nav_settings -> {
                        Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show()
                    }*//*

                    R.id.nav_logout -> {
                        Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show()
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                }
                drawerLayout.closeDrawer(GravityCompat.START)
                true
            }

        } else {
            Log.i(TAG, "onCreate: UserFireId is null")
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun saveProfileData(userFireId: String) {
        val db = Firebase.firestore

        val name = findViewById<EditText>(R.id.input_name).text.toString()
        val age = findViewById<EditText>(R.id.input_age).text.toString().toIntOrNull() ?: 0
        val phone = findViewById<EditText>(R.id.input_phone).text.toString()
        val newsletter = findViewById<CheckBox>(R.id.checkbox_newsletter).isChecked
        val theme = findViewById<Spinner>(R.id.spinner_theme).selectedItem.toString()

        val profileData = hashMapOf(
            "name" to name,
            "age" to age,
            "phone" to phone,
            "newsletter" to newsletter,
            "theme" to theme
        )

        db.collection("users")
            .document(userFireId)
            .update(profileData as Map<String, Any>)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile saved successfully", Toast.LENGTH_SHORT).show()

                val newTheme = when (theme) {
                    "Dark" -> AppCompatDelegate.MODE_NIGHT_YES
                    else -> AppCompatDelegate.MODE_NIGHT_NO
                }

                val currentTheme = AppCompatDelegate.getDefaultNightMode()

                if (newTheme != currentTheme) {
                    AppCompatDelegate.setDefaultNightMode(newTheme)

                    // Delay recreate slightly to allow delegate to set before redraw
                    window.decorView.post {
                        recreate()
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save profile: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


}*/
