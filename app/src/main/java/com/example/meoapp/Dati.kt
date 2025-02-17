package com.example.meoapp

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object GlobalState {
    var utente: Utente? = null
    var gatto: gatto? = null
    var username: String = ""
    var routine: orario = orario()
}


data class Utente (
    val nome: String = "",
    //val email: String? = "",
    val password: String = "",
    val nomeUtente: String? = null,
    val gatti: Any = emptyMap<String, gatto>(),
    val dispensers: List<dispenser>? = null,
    //val Tema: String,
    val notifichePush: Boolean? = null,
    val notifiche: notifiche? = null,
    val loggato: Boolean? = false
)

data class notifiche(
    val ora: String = "",   // formato HH:mm
    val data: String? = null, // formato "dd/MM/yyyy"
    val testo: String = ""
)

data class gatto (
    var nome: String = "",
    var peso: String = "",
    val dataNascita: String = "",
    val dispenserId: Int = 0,
    val routine: List<orario> = emptyList(),
    val cronologia: List<orario> = emptyList(),
    val ultimoPasto: orario? = orario("00:00", "0"),
    val icona: String = "",
    val sesso: String = "",
)

data class orario (
    val ora: String = "",   // formato HH:mm
    val quantita: String = "", // formato "0" o "1" (mezzo o pieno)
    val giorno: String? = null, // formato "dd/MM/yyyy"
    val mangiato: String? = null
)

data class dispenser(
    val dispenserId: Int = 0,
    val nome: String,
    //val livelloBatteria: Int,
    val livelloCiboCiotola: Int,
    val livelloCiboDispenser: Int
    //val gatti: List<gatto>
)


var gattiList = mutableStateListOf<gatto>()

fun fetchGattiFromFirebase() {
    val user = Utente("annalisa", "ciao1")
    if (user != null) {
        val database = FirebaseDatabase.getInstance()
        val gattiRef = database.getReference("Utenti").child(user.nome).child("gatti")
        var gatti: Map<String, Map<String, Any>> = emptyMap()

        gattiRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                gattiList.clear()
                for (gattoSnapshot in snapshot.children) {
                    try {
                        val gattoMap = gattoSnapshot.value as? Map<String, Any>
                        gattoMap?.let {
                            val gatto = gatto(
                                nome = it["nome"] as? String ?: "",
                                peso = it["peso"] as? String ?: "",
                                sesso = it["sesso"] as? String ?: "",
                                dataNascita = it["dataNascita"] as? String ?: "",
                                dispenserId = (it["dispenserId"] as? Long)?.toInt() ?: 0,
                                routine = (it["routine"] as?  Map<String, Map<String, String>>)?.values?.map { orarioMap ->
                                    orario(
                                        ora = orarioMap["ora"] ?: "",
                                        quantita = orarioMap["quantita"] ?: ""
                                    )
                                } ?: emptyList(),
                                cronologia = (it["cronologia"] as? Map<String, Map<String, String>>)?.values?.map { orarioMap ->
                                    orario(
                                        ora = orarioMap["ora"] ?: "",
                                        quantita = orarioMap["quantita"] ?: "",
                                        mangiato = orarioMap["mangiato"] ?: "",
                                        giorno = orarioMap["giorno"] ?: ""
                                    )
                                } ?: emptyList(),
                                ultimoPasto = it["ultimoPasto"]?.let { ultimoPastoMap ->
                                    val map = ultimoPastoMap as Map<String, String>
                                    orario(
                                        ora = map["ora"] ?: "",
                                        quantita = map["quantita"] ?: ""
                                    )
                                } ?: orario("00:00", "0"),
                                icona = it["icona"] as? String ?: ""
                            )
                            Log.d("Firebase", "Gatto aggiunto: $gatto")
                            gattiList.add(gatto)
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
//        gattiRef.orderByChild("nome").equalTo(user.nome).addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val userSnapshot = snapshot.children.firstOrNull()
//                if (userSnapshot != null) {
//                    userSnapshot.child("gatti").let {
//                        for (gattoSnapshot in it.children) {
//                            gatti = it.children.associate { it.key!! to it.value as Map<String, Any> }
//                        }
//                    }
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {}
//        })
    }
}