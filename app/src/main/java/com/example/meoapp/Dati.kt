package com.example.meoapp

import android.net.Uri

object GlobalState {
    var utente: Utente? = null
}


data class Utente (
    val nome: String,
    val email: String,
    val password: String,
    val gatti: List<gatto>,
    val dispenders: List<dispenser>,
    val Tema: String,
    val Notifichepush: Boolean,
    val Lingua: String,
    val DimensioneTesto: Int,
)

data class gatto (
    val nome: String,
    val peso: String,
    val foto: Uri,
    val dataNascita: String,
    val dispenderId: Int,
    val routine: List<orario>,
)

data class orario (
    val ora: String,
    val quantita: Int,
)

data class dispenser(
    val id: Int,
    val nome: String,
    //val livelloBatteria: Int,
    val livelloCibo: Int,
    val stato: Boolean,
    //val gatti: List<gatto>
)