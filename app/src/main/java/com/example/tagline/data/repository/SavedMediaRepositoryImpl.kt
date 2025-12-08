package com.example.tagline.data.repository

import com.example.tagline.data.remote.dto.SavedItemEntity
import com.example.tagline.data.remote.dto.toEntity
import com.example.tagline.data.remote.dto.toSavedMedia
import com.example.tagline.domain.model.MediaType
import com.example.tagline.domain.model.SavedMedia
import com.example.tagline.domain.repository.SavedMediaRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SavedMediaRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : SavedMediaRepository {
    
    private val userId: String?
        get() = auth.currentUser?.uid

    private fun savedItemsCollection() = userId?.let {
        firestore.collection("users").document(it).collection("savedItems")
    }

    override fun getSavedItems(): Flow<List<SavedMedia>> = callbackFlow {
        val collection = savedItemsCollection()
        if (collection == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = collection
            .orderBy("addedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val items = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(SavedItemEntity::class.java)?.copy(id = doc.id)?.toSavedMedia()
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                trySend(items)
            }

        awaitClose { listener.remove() }
    }

    override fun getSavedItemsByType(mediaType: MediaType): Flow<List<SavedMedia>> = callbackFlow {
        val collection = savedItemsCollection()
        if (collection == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = collection
            .whereEqualTo("type", mediaType.name)
            .orderBy("addedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val items = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(SavedItemEntity::class.java)?.copy(id = doc.id)?.toSavedMedia()
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                trySend(items)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun addItem(item: SavedMedia): String {
        val collection = savedItemsCollection()
            ?: throw IllegalStateException("Utilizador não autenticado")

        val entity = item.toEntity()
        val docRef = collection.add(entity.toMap()).await()
        return docRef.id
    }

    override suspend fun removeItem(itemId: String) {
        val collection = savedItemsCollection()
            ?: throw IllegalStateException("Utilizador não autenticado")

        collection.document(itemId).delete().await()
    }

    override suspend fun updateItem(item: SavedMedia) {
        val collection = savedItemsCollection()
            ?: throw IllegalStateException("Utilizador não autenticado")

        val entity = item.toEntity()
        collection.document(item.id).set(entity.toMap()).await()
    }

    override suspend fun toggleWatched(itemId: String, watched: Boolean) {
        val collection = savedItemsCollection()
            ?: throw IllegalStateException("Utilizador não autenticado")

        val updates = mutableMapOf<String, Any?>(
            "watched" to watched
        )
        if (watched) {
            updates["watchedAt"] = Timestamp.now()
        } else {
            updates["watchedAt"] = null
        }

        collection.document(itemId).update(updates).await()
    }

    override suspend fun isItemSaved(tmdbId: Int, mediaType: MediaType): Boolean {
        val collection = savedItemsCollection() ?: return false

        val existing = collection
            .whereEqualTo("tmdbId", tmdbId)
            .whereEqualTo("type", mediaType.name)
            .get()
            .await()

        return !existing.isEmpty
    }
}

