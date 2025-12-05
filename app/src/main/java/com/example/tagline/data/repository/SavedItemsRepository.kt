package com.example.tagline.data.repository

import com.example.tagline.data.model.MediaType
import com.example.tagline.data.model.SavedItem
import com.example.tagline.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SavedItemsRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    
    private val userId: String?
        get() = auth.currentUser?.uid

    private fun savedItemsCollection() = userId?.let {
        firestore.collection("users").document(it).collection("savedItems")
    }

    fun getSavedItems(): Flow<Resource<List<SavedItem>>> = callbackFlow {
        trySend(Resource.Loading())
        
        val collection = savedItemsCollection()
        if (collection == null) {
            trySend(Resource.Error("Utilizador não autenticado"))
            close()
            return@callbackFlow
        }

        val listener = collection
            .orderBy("addedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Erro ao carregar lista"))
                    return@addSnapshotListener
                }

                val items = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(SavedItem::class.java)?.copy(id = doc.id)
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                trySend(Resource.Success(items))
            }

        awaitClose { listener.remove() }
    }

    fun getSavedItemsByType(mediaType: MediaType): Flow<Resource<List<SavedItem>>> = callbackFlow {
        trySend(Resource.Loading())
        
        val collection = savedItemsCollection()
        if (collection == null) {
            trySend(Resource.Error("Utilizador não autenticado"))
            close()
            return@callbackFlow
        }

        val listener = collection
            .whereEqualTo("type", mediaType.name)
            .orderBy("addedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Erro ao carregar lista"))
                    return@addSnapshotListener
                }

                val items = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(SavedItem::class.java)?.copy(id = doc.id)
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                trySend(Resource.Success(items))
            }

        awaitClose { listener.remove() }
    }

    suspend fun addItem(item: SavedItem): Resource<String> {
        return try {
            val collection = savedItemsCollection()
                ?: return Resource.Error("Utilizador não autenticado")

            // Check if item already exists
            val existing = collection
                .whereEqualTo("tmdbId", item.tmdbId)
                .whereEqualTo("type", item.type.name)
                .get()
                .await()

            if (!existing.isEmpty) {
                return Resource.Error("Este item já está na sua lista")
            }

            val docRef = collection.add(item.toMap()).await()
            Resource.Success(docRef.id)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Erro ao adicionar item")
        }
    }

    suspend fun removeItem(itemId: String): Resource<Unit> {
        return try {
            val collection = savedItemsCollection()
                ?: return Resource.Error("Utilizador não autenticado")

            collection.document(itemId).delete().await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Erro ao remover item")
        }
    }

    suspend fun updateItem(item: SavedItem): Resource<Unit> {
        return try {
            val collection = savedItemsCollection()
                ?: return Resource.Error("Utilizador não autenticado")

            collection.document(item.id).set(item.toMap()).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Erro ao atualizar item")
        }
    }

    suspend fun toggleWatched(itemId: String, watched: Boolean): Resource<Unit> {
        return try {
            val collection = savedItemsCollection()
                ?: return Resource.Error("Utilizador não autenticado")

            val updates = mutableMapOf<String, Any?>(
                "watched" to watched
            )
            if (watched) {
                updates["watchedAt"] = com.google.firebase.Timestamp.now()
            } else {
                updates["watchedAt"] = null
            }

            collection.document(itemId).update(updates).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Erro ao atualizar item")
        }
    }

    suspend fun isItemSaved(tmdbId: Int, mediaType: MediaType): Boolean {
        return try {
            val collection = savedItemsCollection() ?: return false

            val existing = collection
                .whereEqualTo("tmdbId", tmdbId)
                .whereEqualTo("type", mediaType.name)
                .get()
                .await()

            !existing.isEmpty
        } catch (e: Exception) {
            false
        }
    }
}

