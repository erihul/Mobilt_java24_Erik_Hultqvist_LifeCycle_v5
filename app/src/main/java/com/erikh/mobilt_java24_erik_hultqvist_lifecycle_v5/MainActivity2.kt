// MainActivity2.kt
package com.erikh.mobilt_java24_erik_hultqvist_lifecycle_v5

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
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
    private lateinit var chipGroup: ChipGroup

    private var userFireId: String? = null
    private var email: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
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

        chipGroup


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
        chipGroup = findViewById(R.id.chip_group)

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

                    val favoriteTeams = doc.get("favoriteTeams") as? List<String> ?: emptyList()

                    for (i in 0 until chipGroup.childCount) {
                        val chip = chipGroup.getChildAt(i) as? Chip
                        if (chip != null) {
                            chip.isChecked = favoriteTeams.contains(chip.text.toString())
                        }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveProfileData() {
        val db = Firebase.firestore

        val nameInput = name.text.toString().trim()
        val ageInput = age.text.toString().trim()
        val phoneInput = phone.text.toString().trim()

        val nameRegex = Regex("^[a-zA-ZåäöÅÄÖ ]{2,50}$")
        val phoneRegex = Regex("^\\+?[0-9\\- ]{7,20}$")

        if (!nameRegex.matches(nameInput)) {
            Toast.makeText(this, "Please enter a valid name (letters only)", Toast.LENGTH_SHORT).show()
            return
        }

        val ageValue = ageInput.toIntOrNull()
        if (ageValue == null || ageValue !in 0..120) {
            Toast.makeText(this, "Please enter a valid age (0–120)", Toast.LENGTH_SHORT).show()
            return
        }

        if (!phoneRegex.matches(phoneInput)) {
            Toast.makeText(this, "Please enter a valid phone number (7-15 digits)", Toast.LENGTH_SHORT).show()
            return
        }

        val cleanedPhone = phoneInput.replace(Regex("[^+0-9]"), "")

        val updatedTheme = themeSpinner.selectedItem.toString()

        val selectedTeams = mutableListOf<String>()
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as? Chip
            if (chip?.isChecked == true) {
                selectedTeams.add(chip.text.toString())
            }
        }

        val updatedData = mapOf(
            "name" to nameInput,
            "age" to ageValue,
            "phone" to cleanedPhone,
            "newsletter" to newsletter.isChecked,
            "theme" to updatedTheme,
            "favoriteTeams" to selectedTeams
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