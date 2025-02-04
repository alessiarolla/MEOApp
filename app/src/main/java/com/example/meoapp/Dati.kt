package com.example.meoapp

import android.net.Uri
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object GlobalState {
    var utente: Utente? = null
    var gatto: gatto? = null
}


data class Utente (
    val nome: String,
    val email: String,
    val password: String,
    val gatti: List<gatto>,
    val dispensers: List<dispenser>,
    val Tema: String,
    val Notifichepush: Boolean,
    val Lingua: String,
    val DimensioneTesto: Int,
)

data class gatto (
    val nome: String,
    val peso: String,
    val foto: Any,
    val dataNascita: String,
    val dispenserId: Int,
    val routine: List<orario>,
    val cronologia: List<orario>,
    val ultimoPasto: orario,
    val icona: String,
)

data class orario (
    val ora: String,
    val quantita: String,
)

data class dispenser(
    val id: Int,
    val nome: String,
    //val livelloBatteria: Int,
    val livelloCiboCiotola: Int,
    val livelloCiboDispenser: Int,
    val stato: Boolean,
    //val gatti: List<gatto>
)


var gattiList = mutableListOf<gatto>()

fun fetchGattiFromFirebase() {
    val user = GlobalState.utente
    if (user != null) {
        val database = FirebaseDatabase.getInstance()
        val gattiRef = database.getReference("utenti").child(user.email.replace(".", ",")).child("gatti")

        gattiRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                gattiList.clear()
                for (gattoSnapshot in snapshot.children) {
                    try {
                        // Recupera l'intero oggetto gatto
                        val gatto = gattoSnapshot.getValue(gatto::class.java)
                        // Aggiungi l'oggetto gatto alla lista
                        gatto?.let {
                            gattiList.add(it)
                        }
                    } catch (e: Exception) {
                        Log.e("Firebase", "Errore durante il parsing di un gatto: ${e.message}")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors.
            }
        })
    }
}