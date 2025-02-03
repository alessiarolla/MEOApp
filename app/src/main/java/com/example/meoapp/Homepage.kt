package com.example.meoapp

import android.widget.ProgressBar
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
    var lastMealTime by remember { mutableStateOf("") }
    var timeSinceLastMeal by remember { mutableStateOf("") }


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

    //aggiorna l'orario corrente
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000) // Ogni secondo
            currentTime = getCurrentTime()
        }
    }

    //trova l'orario dell'ultimo pasto dalla cronologia e calcola il tempo trascorso
    LaunchedEffect(currentGattoIndex, gatti) {
        while (true) {
            delay(1000) // Ogni secondo
            val currentGatto = gatti.values.toList().getOrNull(currentGattoIndex)
            lastMealTime = currentGatto?.let { calcolaUltimoPasto(it) } ?: ""
        }
    }


    Column {
        Text("Ultimo pasto: $lastMealTime")
    }



    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "MEO",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 30.sp),
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
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1CC93))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = " $nome",
                                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp)
                                ) {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .border(1.5.dp, Color.Black, shape = RoundedCornerShape(25.dp)),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9E3C3)),
                                        shape = RoundedCornerShape(25.dp)
                                    ) {
                                        Text(
                                            text = "Prossimo pasto tra...",
                                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                                            modifier = Modifier
                                                .padding(16.dp)
                                                .fillMaxWidth(),
                                            textAlign = TextAlign.Center
                                        )
                                    }

                                    Card(
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .padding(top = 50.dp)
                                            .width(200.dp)
                                            .border(1.dp, Color.Black, shape = RoundedCornerShape(15.dp)),
                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                        shape = RoundedCornerShape(15.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(2.dp)) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(8.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.DateRange,
                                                    contentDescription = "Prossimo pasto",
                                                    modifier = Modifier.size(24.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))

                                                // Horizontal progress bar
                                                Box(
                                                    modifier = Modifier
                                                        .height(8.dp)
                                                        .weight(1f)
                                                        .border(1.dp, Color.Black)
                                                ) {
                                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                                        //crea funzione per calcolare questo valore da mettere al posto di questo:
                                                        val progress = 50 / 100f
                                                        //val progress = timeSinceLastMeal.toFloat() / (timeSinceLastMeal.toFloat() + prossimoPasto.toFloat())

                                                        drawRect(
                                                            color = Color(0xFFEFC37F),
                                                            size = Size(size.width * progress, size.height)
                                                        )
                                                    }
                                                }

                                                Spacer(modifier = Modifier.width(4.dp))

                                                Text(
                                                    text = " $prossimoPasto",
                                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                                    modifier = Modifier.padding(top = 4.dp),
                                                    textAlign = TextAlign.Center
                                                )
                                            }

                                        }
                                    }
                                }
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
    Card(
        modifier = Modifier.padding(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1CC93))    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(14.dp)
        ) {
            Text(text = label, textAlign = TextAlign.Center) // Label sopra il cerchio

            Spacer(modifier = Modifier.height(12.dp)) // Add space after the label

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(100.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {

                    //cerchio esterno
                    drawCircle(
                        color = Color(0xFFF9E3C3),
                        radius = size.minDimension * 0.55f // Regola la dimensione del cerchio esterno
                    )

                    // Cerchio di sfondo
                    drawArc(
                        color = Color(0xFFF9E3C3),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = true
                    )

                    // Arco di progresso
                    drawArc(
                        color = Color(0xFFA06558),
                        startAngle = -90f,
                        sweepAngle = 360 * (percentage / 100),
                        useCenter = true
                    )

                    // Cerchio interno per coprire la parte centrale
                    drawCircle(
                        color = Color(0xFFF9E3C3),
                        radius = size.minDimension / 3f // Regola la dimensione del cerchio interno
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
}


fun getCurrentTime(): String {
    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    sdf.timeZone = TimeZone.getDefault()
    return sdf.format(Date())
}


fun calcolaProssimoPasto(routine: Map<String, Map<String, Any>>, currentTime: String): String {
    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val now = sdf.parse(currentTime)
    val tempi = routine.values.mapNotNull { it["ora"] as? String }
        .mapNotNull { sdf.parse("$it:00") } // Aggiunge ":00" per includere i secondi
        .sorted()

    for (tempo in tempi) {
        if (tempo.after(now)) {
            val diff = tempo.time - now.time
            val ore = (diff / (1000 * 60 * 60)) % 24
            val minuti = (diff / (1000 * 60)) % 60
            val secondi = (diff / 1000) % 60
            return String.format("%02d:%02d:%02d", ore, minuti, secondi)
        }
    }

    return if (tempi.isNotEmpty()) {
        val primoPastoDomani = tempi.first()
        val diff = (primoPastoDomani.time + 24 * 60 * 60 * 1000) - now.time
        val ore = (diff / (1000 * 60 * 60)) % 24
        val minuti = (diff / (1000 * 60)) % 60
        val secondi = (diff / 1000) % 60
        String.format("%02d:%02d:%02d", ore, minuti, secondi)
    } else {
        "Nessun pasto programmato"
    }
}

fun calcolaUltimoPasto(gatto: Map<String, Any>): String {
    val cronologia = gatto["cronologia"] as? Map<String, Map<String, Any>> ?: return ""
    val orari = cronologia.values.mapNotNull { it["ora"] as? String }
    return orari.maxOrNull() ?: "00:00"
}

