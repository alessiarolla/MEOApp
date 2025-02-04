package com.example.meoapp

import android.util.Half.toFloat
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
import java.util.concurrent.TimeUnit

@Composable
fun Homepage(navController: NavController) {
    var userEmail = "annalisa"
    var gatti by remember { mutableStateOf<Map<String, Map<String, Any>>>(emptyMap()) }
    var dispensers by remember { mutableStateOf<Map<String, Map<String, Any>>>(emptyMap()) }
    var currentDispenserIndex by remember { mutableStateOf(0) }
    var currentGattoIndex by remember { mutableStateOf(0) }
    var currentTime by remember { mutableStateOf(getCurrentTime()) }
    var lastMealTime by remember { mutableStateOf("") }
    var lastMealQuantity by remember { mutableStateOf("") }

    var timeSinceLastMeal by remember { mutableStateOf("") }
    var timeBetweenMeals by remember { mutableStateOf("") }

    val capacitàDispenser = 100

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

    // Calculate last meal time and time since last meal
    LaunchedEffect(currentGattoIndex, gatti, currentTime) {
        val currentGatto = gatti.values.toList().getOrNull(currentGattoIndex)
        if (currentGatto != null) {
            lastMealTime = calcolaUltimoPasto(currentGatto)
            lastMealQuantity = calcolaUltimoPastoQuantità(currentGatto)
            timeSinceLastMeal = calcolaTempoTrascorsoUltimoPasto(lastMealTime, currentTime)
            timeBetweenMeals = calcolaTempoTraPasti(currentGatto, currentTime)

        }
    }



    Column {
        Text("Ultimo pasto:  $lastMealTime quantità: $lastMealQuantity" )
        Text("Tempo trascorso dall'ultimo pasto: $timeSinceLastMeal")
        Text("Tempo tra l'ultimo pasto e il prossimo pasto: $timeBetweenMeals")

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
                val lastMealQuantityFloat = lastMealQuantity.toFloatOrNull() ?: 100f
                val livelloCiboCiotola = ((currentDispenser["livelloCiboCiotola"] as? Long ?: 0).toFloat() / lastMealQuantityFloat) * 100
                val livelloCiboDispenser = ((currentDispenser["livelloCiboDispenser"] as? Long ?: 0).toFloat() / capacitàDispenser * 100)
                val labelCiboCiotola = ((currentDispenser["livelloCiboCiotola"] as? Long ?: 0).toString())
                val labelCiboDispenser = ((currentDispenser["livelloCiboDispenser"] as? Long ?: 0).toString())
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    CircularProgressIndicator(livelloCiboDispenser, "Cibo Dispenser: $labelCiboDispenser g")
                    CircularProgressIndicator(livelloCiboCiotola, "   Cibo Ciotola: $labelCiboCiotola g  ")
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
            Text(
                text = label,
                textAlign = TextAlign.Center,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

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
    return (gatto["ultimoPasto"] as? Map<String, Any>)?.get("ora") as? String ?: "00:00"
}

fun calcolaUltimoPastoQuantità(gatto: Map<String, Any>): String {
    return (gatto["ultimoPasto"] as? Map<String, Any>)?.get("quantità") as? String ?: "100"
}




fun calcolaTempoTrascorsoUltimoPasto(lastMealTime: String, currentTime: String): String {
    val format = SimpleDateFormat("HH:mm", Locale.getDefault())
    return try {
        val lastMealDate = format.parse(lastMealTime)
        val currentDate = format.parse(currentTime)

        if (lastMealDate != null && currentDate != null) {
            // Se l'ultimo pasto ha un orario maggiore di quello attuale, lo consideriamo il giorno precedente
            if (lastMealDate.after(currentDate)) {
                val calendar = Calendar.getInstance()
                calendar.time = lastMealDate
                calendar.add(Calendar.DAY_OF_MONTH, -1) // Aggiungi un giorno indietro
                lastMealDate.time = calendar.timeInMillis
            }

            val diff = currentDate.time - lastMealDate.time
            val ore = TimeUnit.MILLISECONDS.toHours(diff)
            val minuti = TimeUnit.MILLISECONDS.toMinutes(diff) % 60
            "$ore ore e $minuti minuti"
        } else {
            "00:00"
        }
    } catch (e: Exception) {
        "00:00"
    }
}

fun calcolaTempoTraPasti(gatto: Map<String, Any>, currentTime: String): String {
    val lastMealTime = calcolaUltimoPasto(gatto)
    val routine = gatto["routine"] as? Map<String, Map<String, Any>> ?: emptyMap()
    val nextMealTime = calcolaProssimoPasto(routine, currentTime)

    val format = SimpleDateFormat("HH:mm", Locale.getDefault())
    return try {
        val lastMealDate = format.parse(lastMealTime)
        val nextMealDate = format.parse(nextMealTime)
        val currentDate = format.parse(currentTime)

        if (lastMealDate != null && nextMealDate != null && currentDate != null) {
            // Se l'ultimo pasto è dopo l'attuale, o dopo il prossimo pasto, consideriamolo relativo al giorno precedente
            if (lastMealDate.after(currentDate) ) {
                val calendar = Calendar.getInstance()
                calendar.time = lastMealDate
                calendar.add(Calendar.DAY_OF_MONTH, -1) // Aggiungi un giorno indietro
                lastMealDate.time = calendar.timeInMillis
            }

            // Se il prossimo pasto è prima dell'orario attuale, consideriamolo come domani
            if (nextMealDate.before(currentDate)) {
                val calendar = Calendar.getInstance()
                calendar.time = nextMealDate
                calendar.add(Calendar.DAY_OF_MONTH, 1) // Aggiungi un giorno avanti
                nextMealDate.time = calendar.timeInMillis
            }

            val diff = nextMealDate.time - lastMealDate.time
            val ore = TimeUnit.MILLISECONDS.toHours(diff)
            val minuti = TimeUnit.MILLISECONDS.toMinutes(diff) % 60
            "$ore ore e $minuti minuti"
        } else {
            "00:00"
        }
    } catch (e: Exception) {
        "00:00"
    }
}





