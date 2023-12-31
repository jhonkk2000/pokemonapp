package com.jhonkk.network

import android.util.Log
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import com.jhonkk.common.model.MyLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.internal.wait
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LocationNetwork @Inject constructor(private val firestore: FirebaseFirestore) {

    suspend fun insertLocation(myLocation: MyLocation) = suspendCoroutine { cont ->
        firestore.collection("last_location")
            .add(myLocation)
            .addOnSuccessListener {
                cont.resume(true)
            }.addOnFailureListener { cont.resume(false) }
    }

    fun loadListener(onNewItem: (MyLocation) -> Unit) = firestore.collection("last_location")
        .orderBy("datetime", Query.Direction.DESCENDING)
        .addSnapshotListener { value, error ->
            if (error != null) {
                return@addSnapshotListener
            }
            value?.let { querySnapshot ->
                for (docChange in querySnapshot.documentChanges) {
                    when (docChange.type) {
                        DocumentChange.Type.ADDED -> docChange.document.toObject<MyLocation>().let {
                            onNewItem(it.copy(id = docChange.document.id))
                        }
                        DocumentChange.Type.MODIFIED -> docChange.document.toObject<MyLocation>().let {
                            onNewItem(it.copy(id = docChange.document.id))
                        }
                        else -> {}
                    }
                }
            }
        }

}