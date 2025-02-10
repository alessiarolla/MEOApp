package com.example.meoapp

import android.util.Half.toFloat
import android.widget.ProgressBar
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.res.fontResource
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun Homepage(navController: NavController) {
    var user = "annalisa"
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

    
    LaunchedEffect(user) {
        database.orderByChild("nomeUtente").equalTo(user).addValueEventListener(object : ValueEventListener {
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
            timeBetweenMeals = calcolaTempoTraPasti(currentGatto, currentTime, timeSinceLastMeal)

        }
    }


    /*
    Column(modifier = Modifier.
    fillMaxSize().background(Color(0xFFF3D6A9)).padding(16.dp)
    ) {
        Text("Ultimo pasto:  $lastMealTime quantità: $lastMealQuantity" )
        val timeBetweenMealsMillis = convertToMillis(timeBetweenMeals)
        val timeSinceLastMealMillis = convertToMillis(timeSinceLastMeal)
        Text("T dall'ultimo pasto: $timeSinceLastMeal  $timeSinceLastMealMillis")
        Text("T tra l'ultimo e prossimo: $timeBetweenMeals $timeBetweenMealsMillis")
        val perc = timeSinceLastMealMillis.toFloat() / timeBetweenMealsMillis.toFloat()
        Text("%: $perc")
    }
    */

    Column(modifier = Modifier.
        fillMaxSize().background(Color(0xFFF3D6A9)).padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_app),
                contentDescription = "App Logo",
                modifier = Modifier
                    .padding(4.dp)
                    .size(80.dp) // Adjust the size as needed
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

            if (gatti.size == 0){
                Text(
                    text = "Nessun gatto inserito",
                    fontFamily = customFontFamily,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            //freccia scorrimento gatti a <--
            if (gatti.size > 1) {
                Image(
                    painter = painterResource(id = R.drawable.arrow_sx),
                    contentDescription = "Indietro",
                    modifier = Modifier
                        .size(36.dp)
                        .clickable {
                            currentGattoIndex = (currentGattoIndex - 1 + gatti.size) % gatti.size
                            // Reset dispenserIndex when switching to a new cat
                            currentDispenserIndex = 0
                        }
                )
            }

            LazyColumn(modifier = Modifier.weight(1f)) {
                gatti.keys.toList().getOrNull(currentGattoIndex)?.let { gattoKey ->
                    val gattoData = gatti[gattoKey] ?: emptyMap()
                    val nome = gattoData["nome"] as? String ?: ""
                    val routine = gattoData["routine"] as? Map<String, Map<String, Any>> ?: emptyMap()
                    val timeBetweenMealsMillis = convertToMillis(timeBetweenMeals)
                    val timeSinceLastMealMillis = convertToMillis(timeSinceLastMeal)
                    val perc = timeSinceLastMealMillis.toFloat() / timeBetweenMealsMillis.toFloat()

                    val imageRes = when {
                        perc <= 0.33 -> R.drawable.percprossimopastobassa
                        perc <= 0.66 -> R.drawable.percprossimopastomedia
                        else -> R.drawable.percprossimopastoalta
                    }
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .border(2.dp, Color.Black, shape = RoundedCornerShape(25.dp)),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF7E2C3)),
                            shape = RoundedCornerShape(25.dp)

                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Image(
                                    painter = painterResource(id = imageRes),
                                    contentDescription = "Indicatore prossimità pasto",
                                    modifier = Modifier.size(190.dp).align(Alignment.CenterHorizontally)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = " $nome",
                                    fontFamily = customFontFamily,
                                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )

                                val prossimoPasto = calcolaProssimoPasto(routine, currentTime)
                                val timeBetweenMealsMillis = convertToMillis(timeBetweenMeals)
                                val timeSinceLastMealMillis = convertToMillis(timeSinceLastMeal)
                                val perc = timeSinceLastMealMillis.toFloat() / timeBetweenMealsMillis.toFloat()

                                val routine = gattoData["routine"] as? Map<String, Map<String, Any>> ?: emptyMap()

                                if (routine.isEmpty()) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .border(1.5.dp, Color.Black, shape = RoundedCornerShape(25.dp)),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFCA9E8B)),
                                        shape = RoundedCornerShape(25.dp)
                                    ) {
                                        Text(
                                            text = "Nessuna routine programmata",
                                            fontFamily = customFontFamily,
                                            style = MaterialTheme.typography.titleLarge.
                                            copy(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                                            modifier = Modifier
                                                .padding(16.dp)
                                                .fillMaxWidth(),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                    } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp)
                                    ) {
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .border(1.5.dp, Color.Black, shape = RoundedCornerShape(25.dp)),
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFFCA9E8B)),
                                            shape = RoundedCornerShape(25.dp)
                                        ) {
                                            Text(
                                                text = "Prossimo pasto tra...",
                                                fontFamily = customFontFamily,
                                                style = MaterialTheme.typography.titleLarge.
                                                copy(fontSize = 16.sp, fontWeight = FontWeight.Bold),
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

                                                    Clock(currentTime = currentTime)


                                                    Spacer(modifier = Modifier.width(10.dp))


                                                    // Horizontal progress bar
                                                    Box(
                                                        modifier = Modifier
                                                            .height(8.dp)
                                                            .weight(1f)
                                                            .border(1.dp, Color.Black)
                                                    ) {

                                                        Canvas(modifier = Modifier.fillMaxSize()) {
                                                            val timeBetweenMealsMillis = convertToMillis(timeBetweenMeals)
                                                            val timeSinceLastMealMillis = convertToMillis(timeSinceLastMeal)
                                                            val perc = timeSinceLastMealMillis.toFloat() / timeBetweenMealsMillis.toFloat()

                                                            //crea funzione per calcolare questo valore da mettere al posto di questo:
                                                            val progress = perc
                                                            //val progress = timeSinceLastMeal.toFloat() / (timeSinceLastMeal.toFloat() + prossimoPasto.toFloat())

                                                            drawRect(
                                                                color = Color(0xFFEFC37F),
                                                                size = Size(size.width * progress, size.height)
                                                            )
                                                        }
                                                    }

                                                    Spacer(modifier = Modifier.width(4.dp))

                                                    Column(
                                                        horizontalAlignment = Alignment.CenterHorizontally,
                                                        modifier = Modifier.width(60.dp)
                                                    ) {


                                                        Text(
                                                            text = " $prossimoPasto",
                                                            fontFamily = customFontFamily,
                                                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 10.sp),
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
                }
            }

            //freccia scorrimento gatti a -->
            if (gatti.size > 1) {
                Image(
                    painter = painterResource(id = R.drawable.arrow_dx),
                    contentDescription = "Avanti",
                    modifier = Modifier
                        .size(36.dp)
                        .clickable {
                            currentGattoIndex = (currentGattoIndex + 1) % gatti.size
                            // Reset dispenserIndex when switching to a new cat
                            currentDispenserIndex = 0
                        }
                )
            }

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
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("dispenserDetail/$dispenserId") },
                    horizontalArrangement = Arrangement.SpaceEvenly) {
                    CircularProgressIndicator(livelloCiboDispenser, "Cibo Dispenser: $labelCiboDispenser g",
                    )
                    CircularProgressIndicator(livelloCiboCiotola, "   Cibo Ciotola: $labelCiboCiotola g  ",
                    )
                }
            }
        }

    }
}

@Composable
fun Clock(currentTime: String) {
    val timeParts = currentTime.split(":").map { it.toInt() }
    val hours = timeParts[0]
    val minutes = timeParts[1]
    val seconds = timeParts[2]

    Canvas(modifier = Modifier.size(20.dp)) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2

        // Draw clock face
        drawCircle(
            color = Color.Black,
            center = center,
            radius = radius,
            style = Stroke(width = 1.dp.toPx())
        )

        // Draw hour hand
        val hourAngle = (hours % 12 + minutes / 60f) * 30f - 90f
        drawLine(
            color = Color.Black,
            start = center,
            end = Offset(
                x = center.x + cos(Math.toRadians(hourAngle.toDouble())).toFloat() * radius * 0.5f,
                y = center.y + sin(Math.toRadians(hourAngle.toDouble())).toFloat() * radius * 0.5f
            ),
            strokeWidth = 1.dp.toPx()
        )

        // Draw minute hand
        val minuteAngle = minutes * 6f - 90f
        drawLine(
            color = Color.Black,
            start = center,
            end = Offset(
                x = center.x + cos(Math.toRadians(minuteAngle.toDouble())).toFloat() * radius * 0.7f,
                y = center.y + sin(Math.toRadians(minuteAngle.toDouble())).toFloat() * radius * 0.7f
            ),
            strokeWidth = 0.5.dp.toPx()
        )

        // Draw second hand
        val secondAngle = seconds * 6f - 90f
        drawLine(
            color = Color.Red,
            start = center,
            end = Offset(
                x = center.x + cos(Math.toRadians(secondAngle.toDouble())).toFloat() * radius * 0.9f,
                y = center.y + sin(Math.toRadians(secondAngle.toDouble())).toFloat() * radius * 0.9f
            ),
            strokeWidth = 0.3.dp.toPx()
        )
    }
}

@Composable
fun CircularProgressIndicator(percentage: Float, label: String) {
    Card(
        modifier = Modifier
            .padding(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1CC93))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = label,
                fontFamily = customFontFamily,
                textAlign = TextAlign.Center,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(90.dp)
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
                        useCenter = true,
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
                    fontFamily = customFontFamily,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
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
    if (routine.isEmpty()) {
        return "00:00:00"
    }
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
            val secondi = TimeUnit.MILLISECONDS.toSeconds(diff) % 60
            String.format("%02d:%02d:%02d", ore, minuti, secondi)
        } else {
            "00:00:00"
        }
    } catch (e: Exception) {
        "00:00:00"
    }
}

fun calcolaTempoTraPasti(gatto: Map<String, Any>, currentTime: String, timeSinceLastMeal: String): String {
    val routine = gatto["routine"] as? Map<String, Map<String, Any>> ?: emptyMap()
    if (routine.isEmpty()) {
        return "00:00:00"
    }
    val nextMealTime = calcolaProssimoPasto(routine, currentTime)

    // Funzione per convertire una durata "hh:mm:ss" in millisecondi
    fun convertToMillis(duration: String): Long {
        val parts = duration.split(":").map { it.toInt() }
        val hours = parts.getOrElse(0) { 0 }
        val minutes = parts.getOrElse(1) { 0 }
        val seconds = parts.getOrElse(2) { 0 }
        return TimeUnit.HOURS.toMillis(hours.toLong()) + TimeUnit.MINUTES.toMillis(minutes.toLong()) + TimeUnit.SECONDS.toMillis(seconds.toLong())
    }

    // Convertiamo entrambe le durate in millisecondi
    val lastMealMillis = convertToMillis(timeSinceLastMeal)
    val nextMealMillis = convertToMillis(nextMealTime)

    // Sommiamo i tempi
    val totalMillis = nextMealMillis + lastMealMillis

    // Calcoliamo le ore, minuti e secondi
    val ore = TimeUnit.MILLISECONDS.toHours(totalMillis)
    val minuti = TimeUnit.MILLISECONDS.toMinutes(totalMillis) % 60
    val secondi = TimeUnit.MILLISECONDS.toSeconds(totalMillis) % 60

    // Formattiamo la risposta
    return String.format("%02d:%02d:%02d", ore, minuti, secondi)
}

fun convertToMillis(time: String): Long {
    return try {
        val parts = time.split(":").map { it.toIntOrNull() ?: 0 }
        val hours = parts.getOrElse(0) { 0 }
        val minutes = parts.getOrElse(1) { 0 }
        val seconds = parts.getOrElse(2) { 0 }
        TimeUnit.HOURS.toMillis(hours.toLong()) + TimeUnit.MINUTES.toMillis(minutes.toLong()) + TimeUnit.SECONDS.toMillis(seconds.toLong())
    } catch (e: Exception) {
        0L
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DispenserDetail(navController: NavController, dispenserId: Long) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "DISPENSER",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily(Font(R.font.autouroneregular)),
                            color = Color(0xFF7F5855),
                            fontSize = 26.sp
                        ),
                        modifier = Modifier.padding(top = 25.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF3D6A9) // Sostituisci con il colore desiderato
                ),
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.padding(top = 25.dp)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF7F5855))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF3D6A9))
                .padding(innerPadding)
                .padding(16.dp)
        ) {

            Divider(
                modifier = Modifier.padding(bottom = 10.dp),
                color = Color(0xFF7F5855),
                thickness = 2.dp
            )


            var user = "annalisa"
            var gatti by remember { mutableStateOf<Map<String, Map<String, Any>>>(emptyMap()) }
            var dispensers by remember { mutableStateOf<Map<String, Map<String, Any>>>(emptyMap()) }
            var currentDispenserIndex by remember { mutableStateOf(0) }
            var currentGattoIndex by remember { mutableStateOf(0) }
            var lastMealQuantity by remember { mutableStateOf("") }

            val capacitàDispenser = 100

            val database = FirebaseDatabase.getInstance().reference.child("Utenti")


            LaunchedEffect(user) {
                database.orderByChild("nomeUtente").equalTo(user).addValueEventListener(object : ValueEventListener {
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


            // Calculate last meal time and time since last meal
            LaunchedEffect(currentGattoIndex, gatti) {
                val currentGatto = gatti.values.toList().getOrNull(currentGattoIndex)
                if (currentGatto != null) {
                    lastMealQuantity = calcolaUltimoPastoQuantità(currentGatto)

                }
            }


            /*
            Column(modifier = Modifier.
            fillMaxSize().background(Color(0xFFF3D6A9)).padding(16.dp)
            ) {
                Text("Ultimo pasto:  $lastMealTime quantità: $lastMealQuantity" )
                val timeBetweenMealsMillis = convertToMillis(timeBetweenMeals)
                val timeSinceLastMealMillis = convertToMillis(timeSinceLastMeal)
                Text("T dall'ultimo pasto: $timeSinceLastMeal  $timeSinceLastMealMillis")
                Text("T tra l'ultimo e prossimo: $timeBetweenMeals $timeBetweenMealsMillis")
                val perc = timeSinceLastMealMillis.toFloat() / timeBetweenMealsMillis.toFloat()
                Text("%: $perc")
            }
            */

            Column(modifier = Modifier.
            fillMaxSize().background(Color(0xFFF3D6A9)).padding(16.dp)
            ) {
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
                        Row(modifier = Modifier
                            .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly) {
                            CircularProgressIndicator(livelloCiboDispenser, "Cibo Dispenser: $labelCiboDispenser g",
                            )
                            CircularProgressIndicator(livelloCiboCiotola, "  Cibo Ciotola: $labelCiboCiotola g ",
                            )
                        }
                }

            }
        }
    }
}}