package com.example.vireya.firebase

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.example.vireya.model.PrescriptionData


object FirebaseHelper {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    fun saveUserDetails(uid: String, userData: Map<String, Any>, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("users").document(uid)
            .set(userData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun uploadPrescriptionImage(
        uid: String,
        imageUri: Uri,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val ref = storage.reference.child("prescriptions/$uid/${System.currentTimeMillis()}.jpg")
        ref.putFile(imageUri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    onSuccess(uri.toString())
                }
            }
            .addOnFailureListener { e -> onFailure(e) }
    }


    fun saveMedicineSchedule(uid: String, scheduleData: Map<String, Any>, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("users").document(uid)
            .collection("schedule")
            .add(scheduleData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun saveQuizResult(uid: String, resultData: Map<String, Any>, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("users").document(uid)
            .collection("quizResults")
            .add(resultData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun assignBadge(uid: String, badgeName: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val badgeData = hashMapOf(
            "badgeName" to badgeName,
            "earnedAt" to System.currentTimeMillis()
        )
        db.collection("users").document(uid)
            .collection("badges")
            .add(badgeData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun saveQuizResultByType(
        uid: String,
        quizType: String,
        answers: Map<String, String>,
        result: String,
        tips: List<String>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val resultMap = hashMapOf(
            "answers" to answers,
            "result" to result,
            "tips" to tips,
            "timestamp" to System.currentTimeMillis()
        )
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("quizResults")
            .document(quizType)
            .set(resultMap)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }


    fun savePrescriptions(
        uid: String,
        prescriptions: List<PrescriptionData>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val batch = db.batch()
        val userPrescriptionsRef = db.collection("users").document(uid).collection("prescriptions")

        prescriptions.forEach { prescription ->
            val newDoc = userPrescriptionsRef.document()
            batch.set(newDoc, mapOf(
                "name" to prescription.name,
                "time" to prescription.time,
                "tips" to prescription.tips,
                "days" to prescription.days,
                "duration" to prescription.duration
            ))
        }

        batch.commit()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }


}

