package com.example.meoapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.database.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun Homepage(navController: NavController) {
    var userEmail = "annalisa"
    var gatti by remember { mutableStateOf<Map<String, Map<String, Any>>>(emptyMap()) }
    var dispensers by remember { mutableStateOf<Map<String, Map<String, Any>>>(emptyMap()) }
    var currentDispenserIndex by remember { mutableStateOf(0) }
    var currentTime by remember { mutableStateOf(getCurrentTime()) }

    val database = FirebaseDatabase.getInstance().reference.child("Utenti")

    LaunchedEffect(userEmail) {
        database.orderByChild("email").equalTo(userEmail).addValueEventListener(object : ValueEventListener {
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

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(60000)
            currentTime = getCurrentTime()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Prossimi pasti", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(8.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            gatti.forEach { (gattoKey, gattoData) ->
                val nome = gattoData["nome"] as? String ?: ""
                val routine = gattoData["routine"] as? Map<String, Map<String, Any>> ?: emptyMap()
                val prossimoPasto = calcolaProssimoPasto(routine, currentTime)
                item {
                    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Nome: $nome", style = MaterialTheme.typography.titleSmall)
                            Text(text = "Prossimo pasto tra: $prossimoPasto")
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
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Dispenser precedente",
                            modifier = Modifier.clickable {
                                currentDispenserIndex = (currentDispenserIndex - 1 + dispenserKeys.size) % dispenserKeys.size
                            }
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Dispenser successivo",
                            modifier = Modifier.clickable {
                                currentDispenserIndex = (currentDispenserIndex + 1) % dispenserKeys.size
                            }
                        )
                    }
                }
            }
        } else {
            Text(text = "Nessun dispenser trovato.", modifier = Modifier.padding(8.dp))
        }
    }
}

fun getCurrentTime(): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    sdf.timeZone = TimeZone.getDefault()
    return sdf.format(Date())
}

fun calcolaProssimoPasto(routine: Map<String, Map<String, Any>>, currentTime: String): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    val now = sdf.parse(currentTime)
    val tempi = routine.values.mapNotNull { it["ora"] as? String }
        .mapNotNull { sdf.parse(it) }
        .sorted()

    for (tempo in tempi) {
        if (tempo.after(now)) {
            val diff = tempo.time - now.time
            val ore = (diff / (1000 * 60 * 60)) % 24
            val minuti = (diff / (1000 * 60)) % 60
            return String.format("%02d:%02d", ore, minuti)
        }
    }

    return if (tempi.isNotEmpty()) {
        val primoPastoDomani = tempi.first()
        val diff = (primoPastoDomani.time + 24 * 60 * 60 * 1000) - now.time
        val ore = (diff / (1000 * 60 * 60)) % 24
        val minuti = (diff / (1000 * 60)) % 60
        "$ore ore e $minuti minuti"
    } else {
        "Nessun pasto programmato"
    }
}
