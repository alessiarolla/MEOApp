package com.example.meoapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.database.*
import androidx.compose.material3.Typography
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


@Composable
fun Homepage(navController: NavController) {
    var userEmail = "annalisa"
    var gatti by remember { mutableStateOf<Map<String, Map<String, Any>>>(emptyMap()) }
    var dispensers by remember { mutableStateOf<Map<String, Map<String, Any>>>(emptyMap()) }
    var currentDispenserIndex by remember { mutableStateOf(0) }

    val database = FirebaseDatabase.getInstance().reference.child("Utenti")

    LaunchedEffect(userEmail) {
        database.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userSnapshot = snapshot.children.firstOrNull()
                if (userSnapshot != null) {
                    userSnapshot.child("gatti").let {
                        gatti = it.children.associate { it.key!! to it.value as Map<String, Any> }
                    }
                    userSnapshot.child("dispensers").let {
                        dispensers = it.children.associate { it.key!! to it.value as Map<String, Any> }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Prossimi pasti", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(8.dp))
        val currentTime = remember {
            val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())


            sdf.format(Date())
        }
        Text(text = "Ora attuale: $currentTime")

        LazyColumn(modifier = Modifier.weight(1f)) {
            gatti.forEach { (gattoKey, gattoData) ->
                val nome = gattoData["nome"] as? String ?: ""
                val routine = gattoData["routine"] as? Map<String, Map<String, Any>> ?: emptyMap()
                item {
                    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Nome: $nome", style = MaterialTheme.typography.titleSmall)
                            routine.forEach { (orarioKey, orarioData) ->
                                val ora = orarioData["ora"] as? String ?: ""
                                val quantità = orarioData["quantità"] as? Long ?: 0
                                Text(text = "- Orario: $ora, Quantità: $quantità")
                            }
                        }
                    }
                }
            }
        }


        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "I tuoi dispenser", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(8.dp))
        if (dispensers.isNotEmpty()) {
            val dispenserKeys = dispensers.keys.toList()
            val currentDispenser = dispensers[dispenserKeys[currentDispenserIndex]] ?: emptyMap()
            val nome = currentDispenser["nome"] as? String ?: ""
            val livelloCibo = currentDispenser["livelloCibo"] as? Long ?: 0
            val stato = currentDispenser["stato"] as? Boolean ?: false

            Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Nome: $nome", style = MaterialTheme.typography.titleSmall)
                    Text(text = "Livello Cibo: $livelloCibo")
                    Text(text = "Stato: ${if (stato) "Attivo" else "Inattivo"}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Cambia Dispenser",
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable {
                                currentDispenserIndex = (currentDispenserIndex + 1) % dispenserKeys.size
                            },
                    )
                }
            }
        } else {
            Text(text = "Nessun dispenser trovato.", modifier = Modifier.padding(8.dp))
        }
    }
}
