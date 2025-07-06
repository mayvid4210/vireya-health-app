package com.example.vireya

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

fun registerUserAndSaveDetails(
    fullName: String,
    userName: String,
    email: String,
    password: String,
    age: Int,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
) {
    val auth = FirebaseAuth.getInstance()

    auth.createUserWithEmailAndPassword(email, password)
        .addOnSuccessListener { authResult ->
            val uid = authResult.user?.uid
            if (uid == null) {
                val e = Exception("UID is null after registration")
                Log.e("Firebase", "Registration failed - UID null", e)
                onFailure(e)
                return@addOnSuccessListener
            }

            val userData = mapOf(
                "fullName" to fullName,
                "username" to userName,
                "email" to email,
                "age" to age,
                "createdAt" to System.currentTimeMillis()
            )

            FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .set(userData)
                .addOnSuccessListener {
                    Log.d("Firestore", "User profile saved successfully for $uid")
                    onSuccess()
                }
                .addOnFailureListener { firestoreError ->
                    Log.e("Firestore", "Failed to save user profile", firestoreError)
                    onFailure(firestoreError)
                }
        }
        .addOnFailureListener { authError ->
            Log.e("FirebaseAuth", "User registration failed", authError)
            onFailure(authError)
        }
}
