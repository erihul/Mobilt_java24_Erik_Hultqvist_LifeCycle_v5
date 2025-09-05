package com.erikh.mobilt_java24_erik_hultqvist_lifecycle_v5

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore


class BlankFragment : Fragment() {

    val TAG = "ERIK"
    lateinit var emailInput : TextInputEditText
    lateinit var passwordInput : TextInputEditText
    lateinit var registerbtn : Button
    lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val db = Firebase.firestore

        // Create a new user with a first and last name
        val user = hashMapOf(
            "email" to "erik@mail.com",
            "password" to "12345"
        )
        
        

// Add a new document with a generated ID
        /*db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }*/



        Log.i(TAG, "onCreateView: BlankFragment")
        // Inflate the layout for this fragment
        var  v:View = inflater.inflate(R.layout.fragment_blank, container, false)



        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        emailInput   = view.findViewById<TextInputEditText>(R.id.emailEdit)
        passwordInput = view.findViewById<TextInputEditText>(R.id.passwordEdit)
        registerbtn = view.findViewById<Button>(R.id.button2)



        view.setOnClickListener {
            Log.i(TAG, "onViewCreated: nothing!!!")
        }

            val db = Firebase.firestore
        registerbtn.setOnClickListener {

            if(emailInput.text.toString().isEmpty() || passwordInput.text.toString().isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()


            } else {
                Log.i(TAG, "onCreateView: Registerbtn Pushed")
                Log.i(
                    TAG,
                    "onCreateView: email " + emailInput.text.toString() + " password: " + passwordInput.text.toString()

                )

                val user = hashMapOf(
                    "email" to emailInput.text.toString(),
                    "password" to passwordInput.text.toString())
                db.collection("users")
                    .add(user)
                    .addOnSuccessListener { documentReference ->
                        Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                        Toast.makeText(requireContext(), "Registration successful", Toast.LENGTH_SHORT).show()

                        val intent = Intent(requireActivity(), MainActivity2::class.java)
                        intent.putExtra("FIRESTORE_ID", documentReference.id)
                        startActivity(intent)

                        parentFragmentManager.beginTransaction().remove(this).commit()

                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e)
                    }
            }
        }
    }
}