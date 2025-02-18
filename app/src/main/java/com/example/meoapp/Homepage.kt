package com.example.meoapp

import android.util.Half.toFloat
import android.util.Log
import android.widget.ProgressBar
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontVariation.width
import androidx.compose.ui.text.input.KeyboardType
import kotlin.math.cos
import kotlin.math.sin
@OptIn(ExperimentalMaterial3Api::class)



@Composable
fun Homepage(navController: NavController) {
    var user = GlobalState.username
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
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_app),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .padding(4.dp)
                        .size(80.dp) // Adjust the size as needed
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = R.drawable.notification), // Replace with your image resource
                contentDescription = "Notifiche",
                modifier = Modifier
                    .padding(start = 40.dp, bottom = 14.dp)
                    .size(36.dp)
                    .clickable {
                        navController.navigate("home/notification")
                    }
            )

                val currentGatto = gatti.values.toList().getOrNull(currentGattoIndex)
                val dispenserId = currentGatto?.get("dispenserId") as? Long ?: 0

                Image(
                painter = painterResource(id = R.drawable.dispenser_icon), // Replace with your image resource
                contentDescription = "Pagina Dispenser",
                modifier = Modifier
                    .padding(end = 40.dp, bottom = 14.dp)
                    .size(36.dp)
                    .clickable {
                        navController.navigate("home/dispenserDetail/$dispenserId") },

                )



            }



        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

            if (gatti.size == 0){
                Spacer(modifier = Modifier.height(50.dp))

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
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                        Card(
                            modifier = Modifier
                                .height(350.dp)
                                .width(300.dp)
                                .padding(8.dp)
                                .border(2.dp, Color.Black, shape = RoundedCornerShape(25.dp)),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF7E2C3)),
                            shape = RoundedCornerShape(25.dp)

                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Image(
                                    painter = painterResource(id = imageRes),
                                    contentDescription = "Indicatore prossimità pasto",
                                    modifier = Modifier.size(160.dp).align(Alignment.CenterHorizontally)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = " $nome",
                                    fontFamily = customFontFamily,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontSize = 30.sp,
                                        fontWeight = FontWeight.Bold),
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )

                                val prossimoPasto = calcolaProssimoPasto(routine, currentTime)
                                //GlobalState.prossimopasto = prossimoPasto
                                val timeBetweenMealsMillis = convertToMillis(timeBetweenMeals)
                                val timeSinceLastMealMillis = convertToMillis(timeSinceLastMeal)
                                val perc = timeSinceLastMealMillis.toFloat() / timeBetweenMealsMillis.toFloat()

                                val routine = gattoData["routine"] as? Map<String, Map<String, Any>> ?: emptyMap()

                                if (routine.isEmpty()) {
                                    Spacer(modifier = Modifier.height(20.dp))
                                        Text(
                                            text = "Nessuna routine programmata",
                                            fontFamily = customFontFamily,
                                            style = MaterialTheme.typography.titleLarge.
                                            copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold),
                                            modifier = Modifier
                                                .padding(16.dp)
                                                .fillMaxWidth(),
                                            textAlign = TextAlign.Center
                                        )

                                    } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 20.dp) //tra nome gatto e prossimo pasto tra...
                                    ) {

                                            Text(
                                                text = "Prossimo pasto tra...",
                                                fontFamily = customFontFamily,
                                                style = MaterialTheme.typography.titleSmall.
                                                copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold),
                                                modifier = Modifier
                                                    .padding(10.dp)
                                                    .fillMaxWidth(),
                                                textAlign = TextAlign.Center
                                            )


                                        Card(
                                            modifier = Modifier
                                                .align(Alignment.BottomCenter)
                                                .padding(top = 34.dp)
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
                        }}
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
                    .fillMaxWidth(),
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
    return (gatto["ultimoPasto"] as? Map<String, Any>)?.get("quantita") as? String ?: "100"
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

    fun getCurrentTimeFormatted(): String {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }
    var currentTime by remember { mutableStateOf(getCurrentTimeFormatted()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000) // Ogni secondo
            currentTime = getCurrentTimeFormatted()
        }
    }
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
                    containerColor = Color(0xFFF3D6A9)
                ),
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.padding(top = 25.dp)
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF7F5855)
                        )
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

            var user = GlobalState.username
            var gatti by remember { mutableStateOf<Map<String, Map<String, Any>>>(emptyMap()) }
            var dispensers by remember { mutableStateOf<Map<String, Map<String, Any>>>(emptyMap()) }
            var currentGatto by remember { mutableStateOf<Map<String, Any>?>(null) }
            var lastMealQuantity by remember { mutableStateOf("") }
            var dispenserName by remember { mutableStateOf("") }
            var selectedDispenserId by remember { mutableStateOf(dispenserId) }
            var livelloCiboDispenser by remember { mutableStateOf("") }
            var showDialog by remember { mutableStateOf(false) }

            val capacitàDispenser = 1000

            val database = FirebaseDatabase.getInstance().reference.child("Utenti")

            LaunchedEffect(user) {
                database.orderByChild("nomeUtente").equalTo(user)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val userSnapshot = snapshot.children.firstOrNull()
                            if (userSnapshot != null) {
                                userSnapshot.child("gatti").let {
                                    gatti =
                                        it.children.associate { it.key!! to it.value as Map<String, Any> }
                                }
                                userSnapshot.child("dispensers").let {
                                    dispensers =
                                        it.children.associate { it.key!! to it.value as Map<String, Any> }
                                }
                                currentGatto =
                                    gatti.values.firstOrNull { it["dispenserId"] == dispenserId }
                                dispenserName =
                                    dispensers.values.firstOrNull { it["dispenserId"] == dispenserId }
                                        ?.get("nome") as? String ?: ""
                                livelloCiboDispenser =
                                    dispensers.values.firstOrNull { it["dispenserId"] == dispenserId }
                                        ?.get("livelloCiboDispenser").toString()

                            }
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
            }

            LaunchedEffect(currentGatto) {
                currentGatto?.let {
                    lastMealQuantity = calcolaUltimoPastoQuantità(it)
                }
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Ricarica Dispenser", style = MaterialTheme.typography.titleMedium,
                        fontSize = 20.sp, fontFamily = FontFamily(Font(R.font.autouroneregular))) },
                    text = {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            )

                            {
                                Icon(
                                    painter = painterResource(id = R.drawable.quantita), // Replace with your drawable resource
                                    contentDescription = "food Icon",
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .size(20.dp),

                                    )


                                OutlinedTextField(
                                    value = livelloCiboDispenser,
                                    onValueChange = { livelloCiboDispenser = it },
                                    modifier = Modifier
                                        .padding(bottom = 2.dp)
                                        .width(100.dp)
                                        .height(50.dp)
                                        .border(1.dp, Color(0xFF7F5855), RoundedCornerShape(20.dp)),
                                    shape = RoundedCornerShape(20.dp),
                                    textStyle = TextStyle(fontSize = 14.sp, fontFamily = FontFamily(Font(R.font.autouroneregular))),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    trailingIcon = {
                                        Box(
                                            modifier = Modifier
                                                .padding(end = 8.dp),
                                            contentAlignment = Alignment.BottomEnd
                                        ) {

                                            Text("gr", style = TextStyle(fontSize = 14.sp, fontFamily = FontFamily(Font(R.font.autouroneregular))))
                                        }
                                    }
                                )
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val dispenserKey = dispensers.entries.firstOrNull { it.value["dispenserId"] == dispenserId }?.key
                                if (dispenserKey != null) {
                                    database.child(user).child("dispensers").child(dispenserKey).child("livelloCiboDispenser").setValue(livelloCiboDispenser.toLong())
                                    showDialog = false
                                }
                            },
                            modifier = Modifier
                                //.border(1.dp, Color(0xFF000000), RoundedCornerShape(20.dp))
                                .padding(8.dp)
                                .background(Color(0xFF7F5855), RoundedCornerShape(25.dp))
                        ) {
                            Text("Salva", style = (MaterialTheme.typography.titleSmall),
                                fontFamily = FontFamily(Font(R.font.autouroneregular)),
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .padding(4.dp),
                                color = Color(0xFFFFF5E3))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {showDialog = false}) {
                            Text("Annulla", style = (MaterialTheme.typography.titleSmall),
                                fontFamily = FontFamily(Font(R.font.autouroneregular)),
                                fontSize = 12.sp, color = Color(0xFF7F5855), modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(top = 15.dp))
                        }
                    },
                    containerColor = Color(0XFFFFF5E3),
                    modifier = Modifier.width(300.dp)
                )

            }




            if (gatti.isEmpty()) {
                Column(){

                        Text(
                            text = "Nessun dispenser presente",
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            fontFamily = FontFamily(Font(R.font.autouroneregular)),
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,

                            )

                    Spacer(modifier = Modifier.height(20.dp))

                    Card(
                        onClick = { },
                        modifier = Modifier
                            .width(170.dp) // Set the desired width
                            .align(Alignment.CenterHorizontally)
                            //.border(2.dp, Color(0xFF000000), RoundedCornerShape(40.dp))
                            .background(Color(0XFF7F5855), RoundedCornerShape(40.dp)),
                        shape = RoundedCornerShape(40.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0XFF7F5855))
                    ) {
                        Text(
                            text = "Aggiungi\n\ndispenser",
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            fontFamily = FontFamily(Font(R.font.autouroneregular)),
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 18.sp,
                            color = Color(0xFFFFFFFF),
                            textAlign = TextAlign.Center
                        )
                    }



                }


            } else {
                Column(
                    modifier = Modifier.fillMaxSize().background(Color(0xFFF3D6A9)).padding(16.dp)
                ) {


                    Row(
                        modifier = Modifier.fillMaxWidth().padding(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ricarica Dispenser:",
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Start,
                            fontFamily = customFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        Card(
                            onClick = {showDialog = true },
                            modifier = Modifier
                                .width(170.dp) // Set the desired width
                                //.border(2.dp, Color(0xFF000000), RoundedCornerShape(40.dp))
                                .background(Color(0XFF7F5855), RoundedCornerShape(40.dp)),
                            shape = RoundedCornerShape(40.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0XFF7F5855))
                        ) {
                            Text(
                                text = "Ricarica",
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                fontFamily = FontFamily(Font(R.font.autouroneregular)),
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 18.sp,
                                color = Color(0xFFFFFFFF),
                                textAlign = TextAlign.Center
                            )
                        }


                    }

                    Spacer(modifier = Modifier.height(20.dp))


                    Row(
                        modifier = Modifier.fillMaxWidth().padding(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Nome dispenser:",
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Start,
                            fontFamily = customFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        // Dropdown menu per selezionare il dispenser
                        var expanded by remember { mutableStateOf(false) }
                        val availableDispensers = dispensers.filter { dispenser ->
                            !gatti.values.any { it["dispenserId"] == dispenser.value["dispenserId"] }
                        }

                        Card(
                            modifier = Modifier
                                .width(130.dp) // Set the desired width
                                .height(50.dp) // Set the desired height
                                .padding(8.dp)
                                .border(1.dp, Color.Black, shape = RoundedCornerShape(14.dp)),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF3D6A9)) // Set the background color to match the background
                        ) {
                            TextButton(
                                onClick = { expanded = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    fontFamily = customFontFamily,
                                    color = Color.Black,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    text = dispenserName.ifEmpty { "Dispenser" })
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.arrow_down),
                                        contentDescription = "Arrow Down",
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.background(Color(0xFFF3D6A9))
                            ) {
                                availableDispensers.forEach { (key, dispenser) ->
                                    DropdownMenuItem(
                                        modifier = Modifier.height(30.dp),
                                        onClick = {
                                            selectedDispenserId = dispenser["dispenserId"] as Long
                                            dispenserName = dispenser["nome"] as String
                                            aggiornaDispenserIdNelDatabase(
                                                user,
                                                currentGatto?.get("nome") as? String ?: "",
                                                selectedDispenserId
                                            )
                                            expanded = false
                                            navController.navigate("home/dispenserDetail/$selectedDispenserId") {
                                                popUpTo("home/dispenserDetail/$selectedDispenserId") {
                                                    inclusive = true
                                                }
                                            }
                                        },
                                        text = {
                                            Text(
                                                text = dispenser["nome"] as String,
                                                fontFamily = customFontFamily,
                                                color = Color.Black,
                                                fontSize = 10.sp
                                            )
                                        })
                                }
                            }
                        }

                    }


                    Spacer(modifier = Modifier.height(40.dp))


                    currentGatto?.let { gatto ->
                        currentGatto?.let { gatto ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Gatto associato:",
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Start,
                                    fontFamily = customFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )

                                Column(
                                    modifier = Modifier.height(120.dp), // Imposta un'altezza specifica
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                )
                                {
                                    val iconResource = when (gatto["icona"]) {
                                        "foto-profilo1" -> R.drawable.icone_gatti_1
                                        "foto-profilo2" -> R.drawable.icone_gatti_2
                                        "foto-profilo3" -> R.drawable.icone_gatti_3
                                        "foto-profilo4" -> R.drawable.icone_gatti_4
                                        // Aggiungi altri casi per le altre icone
                                        else -> R.drawable.icone_gatti_1 // Icona di default se non corrisponde nessuna stringa
                                    }
                                    Image(
                                        painter = painterResource(id = iconResource), // Replace with your drawable resource
                                        contentDescription = "Cat Image",
                                        modifier = Modifier.size(80.dp).padding(end = 8.dp)
                                    )
                                    Text(
                                        text = "${gatto["nome"]}",
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.Start,
                                        fontFamily = customFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }


                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val filteredDispensers =
                            dispensers.values.filter { it["dispenserId"] == dispenserId }

                        if (filteredDispensers.isNotEmpty()) {
                            val currentDispenser = filteredDispensers.firstOrNull() ?: emptyMap()
                            val lastMealQuantityFloat = lastMealQuantity.toFloatOrNull() ?: 100f

                            val livelloCiboCiotola =
                                ((currentDispenser["livelloCiboCiotola"] as? Long
                                    ?: 0).toFloat() / lastMealQuantityFloat) * 100


                            val routine = gatti["routine"] as? Map<String, Map<String, Any>> ?: emptyMap()
                            val routineEntry = routine.values.find { it["ora"] == currentTime }
                            Log.d("Routine", "Routine: $routine")
                            Log.d("Routine", "Routine entry: $routineEntry")
                            Log.d("Routine", "Current time: $currentTime")
                            val routineQuantity = routineEntry?.get("quantita")?.toString()?.toFloatOrNull() ?: 0f
                            val sottrazioneCibo = (currentDispenser["livelloCiboDispenser"] as? Long ?: 0).toFloat() - routineQuantity
                            val livelloCiboDispenser = sottrazioneCibo / capacitàDispenser * 100
                            val dispenserRef = database.child("Utenti").child(user).child("dispensers")
                            dispenserRef.orderByChild("dispenserId").equalTo(currentDispenser["dispenserId"].toString())
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        for (dispenserSnapshot in snapshot.children) {
                                            val dispenserKey = dispenserSnapshot.key
                                            Log.d("Firebase", "Dispenser key: $dispenserKey")
                                            if(dispenserKey != null) {
                                                dispenserRef.child(dispenserKey).child("livelloCiboDispenser").setValue(sottrazioneCibo)
                                            }
                                        }
                                    }
                                    override fun onCancelled(error: DatabaseError) {
                                        Log.e("Firebase", "Errore nel leggere il dispenser: ${error.message}")
                                    }

                                })


                                //.child("livelloCiboDispenser").setValue((currentDispenser["livelloCiboDispenser"] as? Long ?: 0) - routineQuantity.toLong())

//                            val livelloCiboDispenser =
//                                ((currentDispenser["livelloCiboDispenser"] as? Long
//                                    ?: 0).toFloat() / capacitàDispenser * 100)
                            val labelCiboCiotola =
                                ((currentDispenser["livelloCiboCiotola"] as? Long ?: 0).toString())
                            val labelCiboDispenser =
                                ((currentDispenser["livelloCiboDispenser"] as? Long
                                    ?: 0).toString())
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                CircularProgressIndicator(
                                    livelloCiboDispenser,
                                    "Cibo Dispenser: $labelCiboDispenser g"
                                )
                                CircularProgressIndicator(
                                    livelloCiboCiotola,
                                    "  Cibo Ciotola: $labelCiboCiotola g "
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))


                    Card(
                        onClick = { },
                        modifier = Modifier
                            .width(170.dp) // Set the desired width
                            .align(Alignment.CenterHorizontally)
                            //.border(2.dp, Color(0xFF000000), RoundedCornerShape(40.dp))
                            .background(Color(0XFF7F5855), RoundedCornerShape(40.dp)),
                        shape = RoundedCornerShape(40.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0XFF7F5855))
                    ) {
                        Text(
                            text = "Aggiungi\n\ndispenser",
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            fontFamily = FontFamily(Font(R.font.autouroneregular)),
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 18.sp,
                            color = Color(0xFFFFFFFF),
                            textAlign = TextAlign.Center
                        )
                    }

                }
            }
        }
    }
}


fun aggiornaDispenserIdNelDatabase(user: String, gattoNome: String, nuovoDispenserId: Long) {
    val database = FirebaseDatabase.getInstance().reference.child("Utenti").child(user).child("gatti")
    database.orderByChild("nome").equalTo(gattoNome).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            for (gattoSnapshot in snapshot.children) {
                gattoSnapshot.ref.child("dispenserId").setValue(nuovoDispenserId)
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("DatabaseError", "Error updating dispenser ID: ${error.message}")
        }
    })
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Notification(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "NOTIFICHE",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily(Font(R.font.autouroneregular)),
                            color = Color(0xFF7F5855),
                            fontSize = 26.sp
                        ),
                        modifier = Modifier.padding(top = 25.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF3D6A9)
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
        var user = GlobalState.username
        var notifications by remember { mutableStateOf<List<Map<String, String>>>(emptyList()) }
        val database = FirebaseDatabase.getInstance().reference.child("Utenti")

        var nomeUtente by remember { mutableStateOf("") }

        LaunchedEffect(user) {
            database.orderByChild("nomeUtente").equalTo(user).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userSnapshot = snapshot.children.firstOrNull()
                    userSnapshot?.let {
                        nomeUtente = it.child("nomeUtente").getValue(String::class.java) ?: ""

                        val notificationsList = mutableListOf<Map<String, String>>()
                        for (notifica in it.child("notifiche").children) {
                            val data = notifica.child("data").getValue(String::class.java) ?: ""
                            val ora = notifica.child("ora").getValue(String::class.java) ?: ""
                            val testo = notifica.child("testo").getValue(String::class.java) ?: ""
                            notificationsList.add(mapOf("data" to data, "ora" to ora, "testo" to testo))
                        }
                        notifications = notificationsList.reversed()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle possible errors
                }
            })
        }

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

            if (notifications.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 8.dp)
                            .border(1.dp, Color(0xFF7F5855), RoundedCornerShape(8.dp)),
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0XFFF7E2C3))
                    ){
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Text(
                                text = "Nessuna notifica presente",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                fontFamily = customFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                    }


                }
            } else {
                Box(
                    modifier = Modifier.height(580.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .border(1.dp, Color(0xFF7F5855), RoundedCornerShape(8.dp)),
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0XFFF7E2C3))
                    ){
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(12.dp),
//                            horizontalArrangement = Arrangement.SpaceBetween,
//                            verticalAlignment = Alignment.CenterVertically
//                        ){
//                            Text(text = "Ultime notifiche", style = MaterialTheme.typography.titleMedium,
//                                fontSize = 20.sp, fontFamily = FontFamily(Font(R.font.autouroneregular)))
//                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                            ) {
                                itemsIndexed(notifications) { index, notification ->
                                    //val notification = notifications[index]
                                    val data = notification["data"]
                                    val ora = notification["ora"]
                                    val testo = notification["testo"]

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ){

                                    }
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .border(1.dp, Color(0xFF7F5855), RoundedCornerShape(8.dp)),
                                        shape = RoundedCornerShape(8.dp),
                                        elevation = CardDefaults.cardElevation(4.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0XFFFFF5E3))
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp),
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = "$data, h $ora",
                                                textAlign = TextAlign.Start,
                                                fontFamily = customFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp
                                            )
                                            Divider(
                                                color = Color.Black,
                                                thickness = 1.dp,
                                                modifier = Modifier.padding(vertical = 4.dp)
                                            )
                                            Text(
                                                text = "$testo",
                                                textAlign = TextAlign.Start,
                                                fontFamily = customFontFamily,
                                                fontWeight = FontWeight.Normal,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                    }


                }
                Button(
                    onClick = {
                        database.removeValue()
                    },
                    modifier = Modifier
                        .width(180.dp)
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7F5855))
                ) {
                    Text("Cancella\nnotifiche",
                        color = Color.White,
                        textAlign = TextAlign.Start,
                        fontFamily = customFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp)
                }


            }
        }
    }
}


