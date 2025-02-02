package com.example.meoapp

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    var currentGattoIndex by remember { mutableStateOf(0) }
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "MEO",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Center
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Indietro", modifier = Modifier.clickable {
                currentGattoIndex = (currentGattoIndex - 1 + gatti.size) % gatti.size
                // Reset dispenserIndex when switching to a new cat
                currentDispenserIndex = 0
            })
            LazyColumn(modifier = Modifier.weight(1f)) {
                gatti.keys.toList().getOrNull(currentGattoIndex)?.let { gattoKey ->
                    val gattoData = gatti[gattoKey] ?: emptyMap()
                    val nome = gattoData["nome"] as? String ?: ""
                    val routine = gattoData["routine"] as? Map<String, Map<String, Any>> ?: emptyMap()
                    val prossimoPasto = calcolaProssimoPasto(routine, currentTime)
                    item {
                        Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = " $nome", style = MaterialTheme.typography.titleMedium,
                                    textAlign = TextAlign.Center)
                                Text(text = "Prossimo pasto tra... $prossimoPasto")
                            }
                        }
                    }
                }
            }
            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Avanti", modifier = Modifier.clickable {
                currentGattoIndex = (currentGattoIndex + 1) % gatti.size
                // Reset dispenserIndex when switching to a new cat
                currentDispenserIndex = 0
            })
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            val currentGatto = gatti.values.toList().getOrNull(currentGattoIndex)
            val dispenserId = currentGatto?.get("dispenserId") as? Long ?: 0
            val filteredDispensers = dispensers.values.filter { it["dispenserId"] == dispenserId }

            if (filteredDispensers.isNotEmpty()) {
                val currentDispenser = filteredDispensers.getOrNull(currentDispenserIndex) ?: emptyMap()
                val livelloCiboCiotola = (currentDispenser["livelloCiboCiotola"] as? Long ?: 0).toFloat()
                val livelloCiboDispenser = (currentDispenser["livelloCiboDispenser"] as? Long ?: 0).toFloat()

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    CircularProgressIndicator(livelloCiboDispenser, "Cibo Dispenser")
                    CircularProgressIndicator(livelloCiboCiotola, "Cibo Ciotola")
                }
            }
        }

    }
}

@Composable
fun CircularProgressIndicator(percentage: Float, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = label, textAlign = TextAlign.Center) // Label sopra il cerchio

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(100.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Cerchio di sfondo
                drawArc(
                    color = Color.Gray.copy(alpha = 0.2f),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = true
                )

                // Arco di progresso
                drawArc(
                    color = Color.Gray,
                    startAngle = -90f,
                    sweepAngle = 360 * (percentage / 100),
                    useCenter = true
                )

                // Cerchio interno per coprire la parte centrale
                drawCircle(
                    color = Color.White, // Cambia colore se serve
                    radius = size.minDimension / 2.5f // Regola la dimensione del cerchio interno
                )
            }

            // Percentuale al centro
            Text(
                text = "${percentage.toInt()}%",
                textAlign = TextAlign.Center
            )
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