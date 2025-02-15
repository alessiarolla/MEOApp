package com.example.meoapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.database.FirebaseDatabase
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import java.util.Calendar
import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.github.mikephil.charting.charts.BarChart
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.time.Duration.Companion.seconds
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter


//import android.content.Context

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Cats(navController: NavController) {
    val pagerState = rememberPagerState(pageCount = { gattiList.size })

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "I TUOI GATTI", style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily(Font(R.font.autouroneregular)),
                            color = Color(0xFF7F5855),
                            fontSize = 26.sp
                        ), modifier = Modifier.padding(top = 25.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF3D6A9) // Sostituisci con il colore desiderato
                )
            )

        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .background(Color(0xFFF3D6A9))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                //.background(Color(0xFFF3D6A9))
            ) {
                Divider(
                    modifier = Modifier.padding(bottom = 10.dp),
                    color = Color(0xFF7F5855),
                    thickness = 2.dp
                )

                Spacer(modifier = Modifier.height(30.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(500.dp)
                        .padding(bottom = 16.dp)
                        .background(Color(0XFFFBF1E3), RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0xFF7F5855), RoundedCornerShape(16.dp)),
                ) {
                    HorizontalPager(
                        state = pagerState,
                        //pageCount = gattiList.size,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        val cat = gattiList[page]
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            val iconResource = when (cat.icona) {
                                "foto-profilo1" -> R.drawable.foto_profilo
                                "foto-profilo2" -> R.drawable.foto_profilo
                                "foto-profilo3" -> R.drawable.foto_profilo
                                "foto-profilo4" -> R.drawable.foto_profilo
                                // Aggiungi altri casi per le altre icone
                                else -> R.drawable.foto_profilo // Icona di default se non corrisponde nessuna stringa
                            }
                            Image(
                                painter = painterResource(id = iconResource), // Replace with your drawable resource
                                contentDescription = "Cat Image",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .size(200.dp),
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = cat.nome,
                                style = MaterialTheme.typography.titleMedium,
                                fontFamily = FontFamily(Font(R.font.autouroneregular)),
                                fontSize = 20.sp
                            )
                            Text(
                                text = cat.dataNascita,
                                style = MaterialTheme.typography.bodyLarge,
                                fontFamily = FontFamily(Font(R.font.autouroneregular)),
                                fontSize = 20.sp
                            )
                            Card(
                                onClick = { GlobalState.gatto = cat.nome
                                    navController.navigate("catDetail/${cat.nome}") },
                                modifier = Modifier
                                    //.border(1.dp, Color(0xFF000000), RoundedCornerShape(25.dp))
                                    //.background(Color(0XFF7F5855), RoundedCornerShape(25.dp))
                                    .align(Alignment.End)
                                    .padding(end = 15.dp, top = 80.dp),
                                //shape = RoundedCornerShape(25.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = "Forward",
                                    tint = Color(0xFF7F5855),
                                    modifier = Modifier
                                        .size(50.dp)
                                        .background(Color.Transparent, shape = CircleShape)
                                        .border(2.dp, Color(0xFF7F5855), shape = CircleShape)
                                        .padding(8.dp)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    repeat(gattiList.size) { index ->
                        val color =
                            if (pagerState.currentPage == index) Color.Black else Color.Gray
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(color, shape = CircleShape)
                                .padding(4.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Card(
                    onClick = { navController.navigate("addcats") },
                    modifier = Modifier
                        .border(1.dp, Color(0xFF000000), RoundedCornerShape(25.dp))
                        .background(Color(0XFF7F5855), RoundedCornerShape(25.dp))
                        .align(Alignment.CenterHorizontally),
                    shape = RoundedCornerShape(25.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0XFF7F5855))
                ) {
                    Text(
                        text = "Aggiungi gatto",
                        modifier = Modifier.padding(16.dp),
                        fontFamily = FontFamily(Font(R.font.autouroneregular)),
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 13.sp,
                        color = Color(0xFFFFFFFF)
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCats (navController: NavController){
    var nome by remember { mutableStateOf("") }
    var peso by remember { mutableStateOf("") }
    var dataNascita by remember { mutableStateOf("") }
    var dispenser by remember { mutableStateOf("") }
    var sesso by remember { mutableStateOf("") }
    val sessoOptions = listOf("Maschio", "Femmina")
    var expanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedImage by remember { mutableStateOf("foto-profilo1") }
    var showDatePicker by remember { mutableStateOf(false) }
    var listaicone = listOf(
        Pair(R.drawable.foto_profilo, "foto_profilo1"),
        Pair(R.drawable.foto_profilo2, "foto_profilo2"),
        Pair(R.drawable.foto_profilo3, "foto_profilo3")
    )
    val imageResource = when (selectedImage) {
        "foto_profilo1" -> R.drawable.foto_profilo
        "foto_profilo2" -> R.drawable.foto_profilo2
        "foto_profilo3" -> R.drawable.foto_profilo3
        else -> R.drawable.foto_profilo // Default image
    }

    val isFormValid = nome.isNotBlank() && peso.isNotBlank() && dataNascita.isNotBlank() && dispenser.isNotBlank() && sesso.isNotBlank()


    val context = LocalContext.current
    val config = context.resources.configuration
    config.setLocale(Locale("it"))
    context.resources.updateConfiguration(config, context.resources.displayMetrics)


    if (showDatePicker) {
        val currentTimeMillis = remember { System.currentTimeMillis() } // Data attuale
        val datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Picker,
            initialSelectedDateMillis = currentTimeMillis, // Seleziona oggi come predefinito
            yearRange = 1900..Calendar.getInstance().get(Calendar.YEAR), // Limita gli anni
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis <= currentTimeMillis // Solo date fino a oggi
                }
            }
        )


        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val selectedDate = datePickerState.selectedDateMillis
                    if (selectedDate != null) {
                        val calendar = Calendar.getInstance().apply { timeInMillis = selectedDate }
                        val day = calendar.get(Calendar.DAY_OF_MONTH)
                        val month = calendar.get(Calendar.MONTH) + 1 // Mesi partono da 0
                        val year = calendar.get(Calendar.YEAR)
                        dataNascita = "$day/$month/$year"
                    }
                    showDatePicker = false
                }) {
                    Text("Conferma", color = Color(0xFF7F5855), fontFamily = FontFamily(Font(R.font.autouroneregular)))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Annulla", fontFamily = FontFamily(Font(R.font.autouroneregular)),
                        color = Color(0xFF7F5855)) // Modifica il colore del testo del pulsante
                }
            }
        ) {
            DatePicker(state = datePickerState,
                title = { Text("SELEZIONA UNA DATA", style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily(Font(R.font.autouroneregular)),
                    color = Color(0xFF7F5855),
                    fontSize = 20.sp), modifier = Modifier.padding(start = 15.dp, top = 15.dp))},
                colors = DatePickerDefaults.colors(
                containerColor = Color(0xFFF7E2C3), // Modifica il colore di sfondo del DatePicker
                titleContentColor = Color(0xFF7F5855), // Modifica il colore del titolo
                headlineContentColor = Color(0xFF7F5855), // Modifica il colore del testo principale
                weekdayContentColor = Color(0xFF7F5855), // Modifica il colore dei giorni della settimana
                subheadContentColor = Color(0xFF7F5855), // Modifica il colore del testo secondario
                selectedDayContentColor = Color.White, // Modifica il colore del testo del giorno selezionato
                selectedDayContainerColor = Color(0xFF7F5855) // Modifica il colore di sfondo del giorno selezionato
            ))
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AGGIUNGI GATTO", style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily(Font(R.font.autouroneregular)),
                    color = Color(0xFF7F5855),
                    fontSize = 26.sp
                ), modifier = Modifier.padding(top = 25.dp)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF3D6A9) // Sostituisci con il colore desiderato
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() },
                        modifier = Modifier.padding(top = 25.dp)) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF7F5855))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column (
            modifier = Modifier.fillMaxSize()
                .background(Color(0xFFF3D6A9))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(5.dp),
                //verticalArrangement = Arrangement.Center
            ) {
                Divider(
                    modifier = Modifier.padding(bottom = 10.dp),
                    color = Color(0xFF7F5855),
                    thickness = 2.dp
                )
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(Color.Transparent, shape = CircleShape)
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = imageResource),
                        contentDescription = "Cat Image",
                        modifier = Modifier.size(120.dp)
                    )
                    IconButton(
                        onClick = { showDialog = true },
                        modifier = Modifier.align(Alignment.BottomEnd)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Image")
                    }
                }

                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("Seleziona un'immagine") },
                        text = {
                            LazyColumn {
                                items(listaicone) { image ->
                                    Image(
                                        painter = painterResource(id = image.first),
                                        contentDescription = "Selectable Image",
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clickable {
                                                selectedImage = image.second
                                                showDialog = false
                                            }
                                    )
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showDialog = false }) {
                                Text("Conferma")
                            }
                        }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Nome gatto", modifier = Modifier.alignByBaseline(),
                        style = (MaterialTheme.typography.bodyMedium),
                        fontFamily = FontFamily(Font(R.font.autouroneregular)),
                        fontSize = 16.sp)
                    OutlinedTextField(
                        value = nome,
                        singleLine = true,
                        onValueChange = { nome = it },
                        modifier = Modifier.alignByBaseline().weight(1f)
                            .padding(start = 15.dp)
                            .border(1.dp, Color(0xFF7F5855), RoundedCornerShape(20.dp))
                            .height(50.dp)
                            .width(150.dp),
                        shape = RoundedCornerShape(20.dp),
                        textStyle = TextStyle(fontSize = 14.sp, fontFamily = FontFamily(Font(R.font.autouroneregular)))
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Peso", modifier = Modifier.alignByBaseline(),
                        style = (MaterialTheme.typography.bodyMedium),
                        fontFamily = FontFamily(Font(R.font.autouroneregular)), fontSize = 16.sp)
                    OutlinedTextField(
                        value = peso,
                        onValueChange = { peso = it },
                        singleLine = true,
                        modifier = Modifier.alignByBaseline().weight(1f)
                            .padding(start = 85.dp)
                            .border(1.dp, Color(0xFF7F5855), RoundedCornerShape(20.dp))
                            .height(50.dp)
                            .width(100.dp),
                        shape = RoundedCornerShape(20.dp),
                        textStyle = TextStyle(fontSize = 14.sp, fontFamily = FontFamily(Font(R.font.autouroneregular))),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        trailingIcon = {
                            Box(
                                modifier = Modifier
                                    //.fillMaxHeight()
                                    .padding(end = 8.dp),
                                contentAlignment = Alignment.BottomEnd
                            ) {
                                Text("kg", style = TextStyle(fontSize = 14.sp, fontFamily = FontFamily(Font(R.font.autouroneregular))))
                            }
                        }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Data di \nnascita", modifier = Modifier.alignByBaseline(),
                        style = (MaterialTheme.typography.bodyMedium),
                        fontFamily = FontFamily(Font(R.font.autouroneregular)), fontSize = 16.sp)
                    Card(
                        modifier = Modifier
                            .alignByBaseline()
                            .clickable { showDatePicker = true } // Clicca per aprire il DatePicker
                            .padding(start = 16.dp)
                            .border(1.dp, Color(0xFF7F5855), RoundedCornerShape(20.dp))
                            .height(50.dp)
                            .width(240.dp),
                        colors = CardDefaults.cardColors(Color.Transparent)
                    ) {
                        Text(
                            text = if (dataNascita.isNotBlank()) dataNascita else "Seleziona una data",
                            style = MaterialTheme.typography.bodyLarge,
                            fontFamily = FontFamily(Font(R.font.autouroneregular)),
                            fontSize = 12.sp,
                            color = if (dataNascita.isNotBlank()) Color.Black else Color(0XFF635A4E),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Dispenser", modifier = Modifier.alignByBaseline(),
                        style = (MaterialTheme.typography.bodyMedium),
                        fontFamily = FontFamily(Font(R.font.autouroneregular)), fontSize = 16.sp)
                    OutlinedTextField(
                        value = dispenser,
                        singleLine = true,
                        onValueChange = { dispenser = it },
                        modifier = Modifier.alignByBaseline().weight(1f)
                            .padding(start = 30.dp)
                            .border(1.dp, Color(0xFF7F5855), RoundedCornerShape(20.dp))
                            .height(50.dp),
                        shape = RoundedCornerShape(20.dp),
                        textStyle = TextStyle(fontSize = 14.sp, fontFamily = FontFamily(Font(R.font.autouroneregular)))
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Sesso", modifier = Modifier.alignByBaseline().align(Alignment.CenterVertically).padding(top = 10.dp),
                        style = (MaterialTheme.typography.bodyMedium),
                        fontFamily = FontFamily(Font(R.font.autouroneregular)), fontSize = 16.sp)
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = sesso,
                            onValueChange = { sesso = it },
                            readOnly = true,
                            modifier = Modifier
                                .alignByBaseline()
                                .weight(1f)
                                .menuAnchor()
                                .padding(start = 75.dp)
                                .border(1.dp, Color(0xFF7F5855), RoundedCornerShape(20.dp))
                                .height(50.dp),
                            shape = RoundedCornerShape(20.dp),
                            textStyle = TextStyle(fontSize = 14.sp, fontFamily = FontFamily(Font(R.font.autouroneregular)))
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier
                                .background(Color(0xFFA37F6F))
                                .fillMaxWidth()
                        ) {
                            sessoOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(
                                        option,
                                        style = (MaterialTheme.typography.bodySmall),
                                        fontFamily = FontFamily(Font(R.font.autouroneregular)),
                                        fontSize = 14.sp) },
                                    onClick = {
                                        sesso = option
                                        expanded = false
                                    },
                                    colors = MenuDefaults.itemColors(
                                        textColor = Color(0xFF000000),

                                    )
                                )
                            }
                        }
                    }
                }
                Button(
                    onClick = {
                        val gatto = mapOf(
                            "nome" to nome,
                            "peso" to peso,
                            "dataNascita" to dataNascita,
                            "dispenserId" to dispenser,
                            "sesso" to sesso,
                            "icona" to selectedImage,
                            "routine" to emptyList<orario>(),
                        )
                        val user = Utente(GlobalState.username)
                        if (user != null) {
                            val database = FirebaseDatabase.getInstance().reference
                            database.child("Utenti").child(user.nome).child("gatti").push()
                                .setValue(gatto)
                        }
                        navController.navigate("cats")

                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7F5855), contentColor = Color.White),
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .height(50.dp)
                        .width(200.dp)
                        .border(1.dp, Color(0xFF000000), RoundedCornerShape(30.dp))
                        .align(Alignment.CenterHorizontally)
                    //enabled = isFormValid
                ) {
                    Text("Conferma", style = (MaterialTheme.typography.titleSmall),
                        fontFamily = FontFamily(Font(R.font.autouroneregular)),
                        fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
fun AddRoutineDialog(onDismiss: () -> Unit) {
    var ore by remember { mutableStateOf("") }
    var min by remember { mutableStateOf("") }
    var orario = "$ore:$min:00"
    var quantita by remember { mutableStateOf("") }
    val isFormValid = orario.matches(Regex("^\\d{2}:\\d{2}:00$")) && quantita.isNotBlank()
    var messaggioErrore by remember { mutableStateOf("") }


    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuovo pasto", style = MaterialTheme.typography.titleMedium,
            fontSize = 20.sp, fontFamily = FontFamily(Font(R.font.autouroneregular))) },
        text = {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.orario), // Replace with your drawable resource
                        contentDescription = "Clock Icon",
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(20.dp),

                    )
                    OutlinedTextField(
                        value = ore,
                        onValueChange = { if (it.length <= 2) ore = it  },
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .width(60.dp)
                            .height(50.dp)
                            .border(1.dp, Color(0xFF7F5855), RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        textStyle = TextStyle(fontSize = 14.sp, fontFamily = FontFamily(Font(R.font.autouroneregular))),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Text(":", style = TextStyle(fontSize = 20.sp, fontFamily = FontFamily(Font(R.font.autouroneregular))), modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 8.dp, end = 8.dp))
                    OutlinedTextField(
                        value = min,
                        onValueChange = { if (it.length <= 2) min = it  },
                        singleLine = true,
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .width(60.dp)
                            .height(50.dp)
                            .border(1.dp, Color(0xFF7F5855), RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        textStyle = TextStyle(fontSize = 14.sp, fontFamily = FontFamily(Font(R.font.autouroneregular))),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.quantita), // Replace with your drawable resource
                        contentDescription = "food Icon",
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(20.dp),

                        )
                    OutlinedTextField(
                        value = quantita,
                        onValueChange = { quantita = it },
                        //label = { Text("Quantità") }
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .width(100.dp)
                            .height(50.dp)
                            .border(1.dp, Color(0xFF7F5855), RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        textStyle = TextStyle(fontSize = 14.sp, fontFamily = FontFamily(Font(R.font.autouroneregular))),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                }
                Text(
                    text = messaggioErrore,
                    color = Color.Red,
                    fontSize = 12.sp,
                    fontFamily = FontFamily(Font(R.font.autouroneregular)))
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (!isFormValid) {
                        messaggioErrore = "Riempi tutti i campi"
                    } else if (isFormValid) {
                        val user = Utente(GlobalState.username)
                        if (user != null) {
                            val database = FirebaseDatabase.getInstance().reference
                            val gattoName = GlobalState.gatto
                            val gattiRef = database.child("Utenti").child(user.nome).child("gatti").orderByChild("nome").equalTo(gattoName)
                            gattiRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    for (gattoSnapshot in snapshot.children) {
                                        val catKey = gattoSnapshot.key
                                        if (catKey != null) {
                                            val routineRef = database.child("Utenti").child(user.nome).child("gatti").child(catKey).child("routine")
                                            val existingRoutine = gattoSnapshot.child("routine").children.find { it.child("ora").value == orario }
                                            if (existingRoutine != null) {
                                                existingRoutine.ref.child("quantita").setValue(quantita)
                                            } else {
                                                val routineCount = gattoSnapshot.child("routine").childrenCount.toInt()
                                                val newRoutineKey = "orario" + (routineCount + 1)

                                                val newRoutine = orario(ora = orario, quantita = quantita)
                                                routineRef.child(newRoutineKey).setValue(newRoutine)
                                            }
                                        }
                                    }
                                }
                                override fun onCancelled(error: DatabaseError) {
                                    Log.e("Firebase", "Errore nel leggere la routine: ${error.message}")
                                }

                            })
                            onDismiss() // Chiude la finestra
                        }
                    }
                },
                enabled = isFormValid
            ) {
                Text("Aggiungi", style = (MaterialTheme.typography.titleSmall),
                    fontFamily = FontFamily(Font(R.font.autouroneregular)),
                    fontSize = 12.sp,
                    modifier = Modifier
                        .border(1.dp, Color(0xFF7F5855), RoundedCornerShape(20.dp))
                        .padding(8.dp),
                    color = Color(0xFF7F5855))
                //Log.d("Firebase", "Routine aggiunta: $orario")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annulla", style = (MaterialTheme.typography.titleSmall),
                    fontFamily = FontFamily(Font(R.font.autouroneregular)),
                    fontSize = 12.sp, color = Color(0xFF7F5855))
            }
        },
        containerColor = Color(0XFFFFF5E3)
    )
}

@Composable
fun DeleteRoutineDialog(onDismiss: () -> Unit, orario: orario) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Conferma eliminazione", style = MaterialTheme.typography.titleMedium,
            fontSize = 20.sp, fontFamily = FontFamily(Font(R.font.autouroneregular)))  },
        text = { Text("Sei sicuro di voler eliminare questa routine?", style = MaterialTheme.typography.bodySmall,
            fontSize = 15.sp, fontFamily = FontFamily(Font(R.font.autouroneregular))) },
        confirmButton = {
            TextButton(
                onClick = {
                    val user = Utente(GlobalState.username)
                    if (user != null) {
                        val database = FirebaseDatabase.getInstance().reference
                        val gattoName = GlobalState.gatto
                        val gattiRef = database.child("Utenti").child(user.nome).child("gatti").orderByChild("nome").equalTo(gattoName)
                        gattiRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (gattoSnapshot in snapshot.children) {
                                    val catKey = gattoSnapshot.key
                                    if (catKey != null) {
                                        val routineRef =
                                            database.child("Utenti").child(user.nome).child("gatti")
                                                .child(catKey).child("routine")
                                        routineRef.addListenerForSingleValueEvent(object :
                                            ValueEventListener {
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                Log.d("Firebase", "Routine data: ${snapshot.value}")
                                                Log.d("Firebase", "orario: $orario")
                                                val routineToDelete =
                                                    snapshot.children.find { it.child("ora").value == orario.ora }
                                                Log.d("Firebase", "Routine to delete: $routineToDelete")
                                                routineToDelete?.ref?.removeValue()
                                            }

                                            override fun onCancelled(error: DatabaseError) {
                                                Log.e(
                                                    "Firebase",
                                                    "Errore nel leggere la routine: ${error.message}"
                                                )
                                            }
                                        })
                                    }
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                                Log.e("Firebase", "Errore nel leggere la routine: ${error.message}")
                            }
                        })
                        onDismiss() // Chiude la finestra
                    }
                }
            ) {
                Text("Elimina")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annulla")
            }
        }
    )
}


@Composable
fun FirstPage() {
    var showAddRoutineDialog by remember { mutableStateOf(false) }
    var showDeleteRoutineDialog by remember { mutableStateOf(false) }
    var orariodelete by remember { mutableStateOf(orario("","")) }
    val sortedRoutine = gattiList.find { it.nome == GlobalState.gatto }?.routine?.sortedBy { it.ora } ?: emptyList()

    if (showAddRoutineDialog) {
        AddRoutineDialog(onDismiss = { showAddRoutineDialog = false })
    }
    if (showDeleteRoutineDialog) {
        DeleteRoutineDialog(onDismiss = { showDeleteRoutineDialog = false }, orariodelete)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
            .height(500.dp)
            .background(Color(0XFFF7E2C3), shape = RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFF7F5855), RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Routine", style = MaterialTheme.typography.titleMedium,
                    fontSize = 20.sp, fontFamily = FontFamily(Font(R.font.autouroneregular)))
            }
            LazyColumn {
                itemsIndexed(sortedRoutine) { index, routine ->
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 2.dp, end = 10.dp, start = 10.dp, bottom = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Pasto ${index + 1}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontFamily = FontFamily(Font(R.font.autouroneregular)),
                                    fontWeight = FontWeight.Bold,
                                )
                                Row {
                                    IconButton(onClick = { showAddRoutineDialog = true }) {
                                        Icon(
                                            painter = painterResource(R.drawable.modifica),
                                            contentDescription = "Edit Routine",
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    IconButton(onClick = {
                                        showDeleteRoutineDialog = true
                                        orariodelete = routine
                                    }) {
                                        Icon(
                                            painter = painterResource(R.drawable.elimina),
                                            contentDescription = "Delete Routine",
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                            Divider(
                                modifier = Modifier.padding(bottom = 10.dp),
                                color = Color(0xFF7F5855),
                                thickness = 1.dp
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .padding(start = 20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.orario), // Replace with your drawable resource
                                    contentDescription = "Icona",
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = if (routine.ora.length >= 5) routine.ora.substring(0, 5) else routine.ora,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = FontFamily(Font(R.font.autouroneregular)),
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .padding(start = 20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.quantita), // Replace with your drawable resource
                                    contentDescription = "Icona",
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "${routine.quantita} gr",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = FontFamily(Font(R.font.autouroneregular)),
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

        }
        Button(onClick = { showAddRoutineDialog = true },
            modifier = Modifier.align(Alignment.BottomCenter)
                .padding(top = 16.dp, bottom = 16.dp)
                .height(50.dp)
                .width(200.dp)
                .border(1.dp, Color(0xFF000000), RoundedCornerShape(30.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7F5855), contentColor = Color.White),
            ){
            Text("+ aggiungi routine", style = (MaterialTheme.typography.titleSmall),
                fontFamily = FontFamily(Font(R.font.autouroneregular)),
                fontSize = 12.sp)
        }
    }
}

@Composable
fun SecondPage() {
    val sortedRoutine = gattiList.find { it.nome == GlobalState.gatto }?.cronologia?.sortedWith(compareBy({ SimpleDateFormat("dd/MM/yyyy").parse(it.giorno) },{ it.ora })) ?: emptyList()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
            .height(500.dp)
            .background(Color(0XFFF7E2C3), shape = RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFF7F5855), RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Cronologia pasti", style = MaterialTheme.typography.titleMedium,
                    fontSize = 20.sp, fontFamily = FontFamily(Font(R.font.autouroneregular)))
            }
            LazyColumn {
                itemsIndexed (sortedRoutine) { index, routine ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .border(1.dp, Color(0xFF7F5855), RoundedCornerShape(8.dp)),
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0XFFFFF5E3))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 2.dp, end = 10.dp, start = 10.dp, bottom = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${routine.giorno ?: "N/A"}, h ${routine.ora}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontFamily = FontFamily(Font(R.font.autouroneregular)),
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                            Divider(
                                modifier = Modifier.padding(bottom = 10.dp),
                                color = Color(0xFF7F5855),
                                thickness = 1.dp
                            )
                            Text(
                                text = "Quantità totale: ${routine.quantita} gr",
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = FontFamily(Font(R.font.autouroneregular)),
                                fontSize = 14.sp,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Quantità mangiata: ${routine.mangiato ?: "N/A"} gr",
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = FontFamily(Font(R.font.autouroneregular)),
                                fontSize = 14.sp,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThirdPage() {
    var selectedPeriod by remember { mutableStateOf("Settimanali") }
    val periodOptions = listOf("Settimanali", "Mensili")
    var expanded by remember { mutableStateOf(false) }
    val cronologia = gattiList.find { it.nome == GlobalState.gatto }?.cronologia ?: emptyList()

    // Applica il filtro basato sul periodo selezionato
    val filteredCronologia = remember(selectedPeriod, cronologia) {
        filterCronologia(cronologia, selectedPeriod)
    }

    val (averageGrams, averagePercentage) = calculateStatistics(filteredCronologia, selectedPeriod)

    // Calcola le ultime 4 settimane per il grafico mensile
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val calendar = Calendar.getInstance()
    calendar.firstDayOfWeek = Calendar.MONDAY  // Imposta il lunedì come primo giorno della settimana
    val currentWeek = calendar.get(Calendar.WEEK_OF_YEAR)
    val currentYear = calendar.get(Calendar.YEAR)

    // Calcola le settimane precedenti
    val previousWeeks = getPreviousWeeks(currentWeek, currentYear)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
            .height(500.dp)
            .background(Color(0XFFF7E2C3), shape = RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFF7F5855), RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Text(text = "Statistiche", style = MaterialTheme.typography.titleMedium,
                fontSize = 20.sp, fontFamily = FontFamily(Font(R.font.autouroneregular)))
            Spacer(modifier = Modifier.height(5.dp))
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier
                    .width(150.dp)
            ) {
                OutlinedTextField(
                    value = selectedPeriod,
                    onValueChange = { selectedPeriod = it },
                    readOnly = true,
                    modifier = Modifier
                        .menuAnchor()
                        .border(1.dp, Color(0xFF7F5855), RoundedCornerShape(20.dp))
                        .height(50.dp),
                    shape = RoundedCornerShape(20.dp),
                    textStyle = TextStyle(fontSize = 14.sp, fontFamily = FontFamily(Font(R.font.autouroneregular)))
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    periodOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedPeriod = option
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Media grammi mangiati: $averageGrams gr", style = MaterialTheme.typography.bodyLarge, fontFamily = FontFamily(Font(R.font.autouroneregular)))
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = "Media percentuale cibo mangiato: ${"%.2f".format(averagePercentage)}%", style = MaterialTheme.typography.bodyLarge, fontFamily = FontFamily(Font(R.font.autouroneregular)))

            Spacer(modifier = Modifier.height(16.dp))

            // Grafico con dati filtrati
            FoodChart(filteredCronologia, selectedPeriod, previousWeeks)
        }
    }
}

// Funzione per filtrare e ottenere le ultime 4 settimane
fun getPreviousWeeks(week: Int, year: Int): List<Pair<Int, Int>> {
    val weeks = mutableListOf<Pair<Int, Int>>()
    val calendar = Calendar.getInstance()
    calendar.firstDayOfWeek = Calendar.MONDAY
    calendar.minimalDaysInFirstWeek = 4 // Imposta lo standard ISO 8601
//    var currentWeek = week
//    var currentYear = year

    calendar.set(Calendar.YEAR, year)
    calendar.set(Calendar.WEEK_OF_YEAR, week)
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY) // Assicura che partiamo dal lunedì


//    // Aggiungi la settimana corrente
//    weeks.add(Pair(currentYear, currentWeek))

    // Aggiungi le settimane precedenti
    for (i in 0..3) {
        val weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR)
        val yearOfWeek = calendar.get(Calendar.YEAR)

        weeks.add(Pair(yearOfWeek, weekOfYear))

        // Scala di una settimana indietro
        calendar.add(Calendar.WEEK_OF_YEAR, -1)
    }

    return weeks.reversed() // Le settimane vanno dalla più vecchia alla più recente
}

fun filterCronologia(cronologia: List<orario>, period: String): List<orario> {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val calendar = Calendar.getInstance()
    calendar.firstDayOfWeek = Calendar.MONDAY  // Imposta il lunedì come primo giorno della settimana

    fun parseDate(date: String): Calendar {
        return Calendar.getInstance().apply {
            time = formatter.parse(date)
            firstDayOfWeek = Calendar.MONDAY // Assicuriamoci che il lunedì sia il primo giorno della settimana
            minimalDaysInFirstWeek = 4
        }
    }

    fun isCurrentWeek(date: String): Boolean {
//        val dateCalendar = Calendar.getInstance().apply { time = formatter.parse(date) }
//
//        // Modifica qui: settimana che inizia da lunedì
//        val dayOfWeek = (dateCalendar.get(Calendar.DAY_OF_WEEK) + 5) % 7  // 1 -> Lunedì, 7 -> Domenica
//        dateCalendar.set(Calendar.DAY_OF_WEEK, dayOfWeek)
//
//        return dateCalendar.get(Calendar.WEEK_OF_YEAR) == calendar.get(Calendar.WEEK_OF_YEAR) &&
//                dateCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
        val parsedDate = parseDate(date)
        val currentWeek = calendar.get(Calendar.WEEK_OF_YEAR)
        val currentYear = calendar.get(Calendar.YEAR)
        val dateWeek = parsedDate.get(Calendar.WEEK_OF_YEAR)
        val dateYear = parsedDate.get(Calendar.YEAR)
        return currentWeek == dateWeek && currentYear == dateYear
    }

    fun isCurrentMonth(date: String): Boolean {
        val dateCalendar = Calendar.getInstance().apply { time = formatter.parse(date) }
        return dateCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                dateCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
    }

    fun getWeekOfYear(date: String): Int {
        val dateCalendar = parseDate(date)
        return dateCalendar.get(Calendar.WEEK_OF_YEAR)
    }

    fun getYearOfDate(date: String): Int {
        val dateCalendar = Calendar.getInstance().apply { time = formatter.parse(date) }
        return dateCalendar.get(Calendar.YEAR)
    }

    // Ottieni la settimana e l'anno corrente
    val currentWeek = calendar.get(Calendar.WEEK_OF_YEAR)
    val currentYear = calendar.get(Calendar.YEAR)



    return when (period) {
        "Settimanali" -> cronologia.filter { it.giorno?.let { isCurrentWeek(it) } ?: false }
        "Mensili" -> {
            val previousWeeks = getPreviousWeeks(currentWeek, currentYear)

            // Raggruppa i pasti per settimana dell'anno (indipendentemente dal mese)
            val weeklyMeals = cronologia.filter { it.giorno?.let { date ->
                val parsedDate = parseDate(date)
                parsedDate.firstDayOfWeek = Calendar.MONDAY
                parsedDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)  // Imposta sempre al lunedì
                val week = parsedDate.get(Calendar.WEEK_OF_YEAR)
                val year = parsedDate.get(Calendar.YEAR)
                previousWeeks.contains(Pair(year, week))
            } ?: false }
                .groupBy { record ->
                    // Calcola la settimana e l'anno del pasto
                    val date = formatter.parse(record.giorno ?: "")
                    val dateCalendar = Calendar.getInstance().apply { time = date }
                    val weekOfYear = dateCalendar.get(Calendar.WEEK_OF_YEAR)
                    val year = dateCalendar.get(Calendar.YEAR)

                    // Ritorna una coppia (anno, settimana)
                    Pair(year, weekOfYear)
                }
                .mapValues { (_, meals) ->
                    // Somma i pasti consumati nella stessa settimana
                    meals.reduce { acc, meal ->
                        acc.copy(
                            mangiato = ((acc.mangiato?.toFloatOrNull() ?: 0f) + (meal.mangiato?.toFloatOrNull() ?: 0f)).toString()
                        )
                    }
                }

            weeklyMeals.values.toList()
        }
        else -> cronologia
    }
}

@Composable
fun FoodChart(cronologia: List<orario>, period: String, previousWeeks: List<Pair<Int, Int>> = emptyList()) {
    val context = LocalContext.current
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val calendar = Calendar.getInstance()

    val xAxisLabels: List<String>
    val foodMap: Map<String, Float>

    if (period == "Settimanali") {
        xAxisLabels = listOf("Lun", "Mar", "Mer", "Gio", "Ven", "Sab", "Dom")
        foodMap = xAxisLabels.associateWith { 0f }.toMutableMap()

        cronologia.forEach { record ->
            record.giorno?.let { dateString ->
                val date = formatter.parse(dateString)
                if (date != null) {
                    calendar.time = date
                    val dayIndex = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7
                    val dayLabel = xAxisLabels[dayIndex]
                    foodMap[dayLabel] = (foodMap[dayLabel] ?: 0f) + (record.mangiato?.toFloatOrNull() ?: 0f)
                }
            }
        }
    } else {
        xAxisLabels = listOf("Set. 1", "Set. 2", "Set. 3", "Set. 4")
        foodMap = xAxisLabels.associateWith { 0f }.toMutableMap()

//        cronologia.forEach { record ->
//            record.giorno?.let { dateString ->
//                val date = formatter.parse(dateString)
//                if (date != null) {
//                    calendar.time = date
//                    val weekIndex = calendar.get(Calendar.WEEK_OF_MONTH) - 1
//                    val weekLabel = xAxisLabels.getOrElse(weekIndex) { "Set. 4" }
//                    foodMap[weekLabel] = (foodMap[weekLabel] ?: 0f) + (record.mangiato?.toFloatOrNull() ?: 0f)
//                }
//            }
//        }
        cronologia.forEach { record ->
            record.giorno?.let { dateString ->
                val date = formatter.parse(dateString)
                if (date != null) {
                    calendar.time = date
                    val weekIndex = calendar.get(Calendar.WEEK_OF_YEAR) // Prendi il numero della settimana dell'anno
                    // Controlla se il pasto è in una delle ultime 4 settimane
                    val weekLabel = when (weekIndex) {
                        previousWeeks[0].second -> "Set. 1" // La settimana più vecchia
                        previousWeeks[1].second -> "Set. 2"
                        previousWeeks[2].second -> "Set. 3"
                        previousWeeks[3].second -> "Set. 4" // La settimana più recente
                        else -> return@forEach  // Ignora le settimane fuori dalle ultime 4
                    }

                    // Aggiungi i dati per la settimana giusta
                    foodMap[weekLabel] = (foodMap[weekLabel] ?: 0f) + (record.mangiato?.toFloatOrNull() ?: 0f)
                }
            }
        }
    }

    val entries = foodMap.entries.mapIndexed { index, entry ->
        BarEntry(index.toFloat(), entry.value)
    }

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(10.dp),
        factory = { ctx ->
            BarChart(ctx).apply {
                description.isEnabled = false
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                axisRight.isEnabled = false
                axisLeft.axisMinimum = 0f
                xAxis.granularity = 1f
                xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabels)

                val dataSet = BarDataSet(entries, "Cibo consumato")
                dataSet.color = Color(0xFF7F5855).toArgb()
                dataSet.valueTextSize = 12f
                dataSet.valueTextColor = Color(0xFF7F5855).toArgb()

                val barData = BarData(dataSet)
                barData.barWidth = 0.5f

                this.data = barData
                invalidate()
            }
        },
        update = { chart ->
            val newEntries = foodMap.entries.mapIndexed { index, entry ->
                BarEntry(index.toFloat(), entry.value)
            }
            val dataSet = BarDataSet(newEntries, "Cibo consumato")
            dataSet.color = Color(0xFF7F5855).toArgb()
            dataSet.valueTextSize = 12f

            val barData = BarData(dataSet)
            barData.barWidth = 0.5f

            chart.data = barData
            chart.xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabels)
            chart.invalidate()
        }
    )
}

fun calculateStatistics(cronologia: List<orario>, period: String): Pair<Double, Double> {
    fun isCurrentWeek(date: String): Boolean {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val currentWeek = calendar.get(Calendar.WEEK_OF_YEAR)
        val currentYear = calendar.get(Calendar.YEAR)
        val dateCalendar = Calendar.getInstance().apply { time = formatter.parse(date) }
        return dateCalendar.get(Calendar.WEEK_OF_YEAR) == currentWeek && dateCalendar.get(Calendar.YEAR) == currentYear
    }

    fun isCurrentMonth(date: String): Boolean {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)
        val dateCalendar = Calendar.getInstance().apply { time = formatter.parse(date) }
        return dateCalendar.get(Calendar.MONTH) == currentMonth && dateCalendar.get(Calendar.YEAR) == currentYear
    }
    val filteredCronologia = when (period) {
        "Settimanali" -> cronologia.filter { it.giorno?.let { isCurrentWeek(it) } ?: false }
        "Mensili" -> cronologia.filter { it.giorno?.let { isCurrentMonth(it) } ?: false }
        else -> cronologia
    }

    val totalGrams = filteredCronologia.sumOf { it.mangiato?.toDoubleOrNull() ?: 0.0 }
    val totalExpectedGrams = filteredCronologia.sumOf { it.quantita.toDoubleOrNull() ?: 0.0 }
    val averageGrams = if (filteredCronologia.isNotEmpty()) totalGrams / filteredCronologia.size else 0.0
    val averagePercentage = if (totalExpectedGrams > 0) (totalGrams / totalExpectedGrams) * 100 else 0.0

    return Pair(averageGrams, averagePercentage)
}


@Composable
fun Carousel() {
    val pages = listOf<@Composable () -> Unit>({ FirstPage() }, { SecondPage() }, { ThirdPage() })
    val pagerState = rememberPagerState(pageCount = { pages.size })

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .height(450.dp)
            .fillMaxSize()
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth().height(500.dp)
        ) { page ->
            pages[page]()
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        pages.forEachIndexed { index, _ ->
            val color = if (pagerState.currentPage == index) Color.Black else Color.Gray
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(color, shape = CircleShape)
                    .padding(4.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatDetail(navController: NavController, gatto: gatto) {
    var showDialog by remember { mutableStateOf(false) }
    //val pagerState = rememberPagerState()
    val iconResource = when (gatto.icona) {
        "foto-profilo1" -> R.drawable.foto_profilo
        "foto-profilo2" -> R.drawable.foto_profilo
        "foto-profilo3" -> R.drawable.foto_profilo
        "foto-profilo4" -> R.drawable.foto_profilo
        // Aggiungi altri casi per le altre icone
        else -> R.drawable.foto_profilo // Icona di default se non corrisponde nessuna stringa
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DETTAGLI", style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily(Font(R.font.autouroneregular)),
                    color = Color(0xFF7F5855),
                    fontSize = 26.sp
                ), modifier = Modifier.padding(top = 25.dp)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF3D6A9) // Sostituisci con il colore desiderato
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() },
                        modifier = Modifier.padding(top = 25.dp)) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF7F5855))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column (
            modifier = Modifier.fillMaxSize()
                .background(Color(0xFFF3D6A9))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top
            ) {
                Divider(
                    modifier = Modifier.padding(bottom = 10.dp),
                    color = Color(0xFF7F5855),
                    thickness = 2.dp
                )
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp, start = 16.dp, end = 12.dp, top = 23.dp)
                        .border(1.dp, Color(0xFF7F5855), RoundedCornerShape(23.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0XFFFFD89D))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = iconResource), // Replace with your drawable resource
                            contentDescription = "Cat Image",
                            modifier = Modifier.size(120.dp)
                        )
                        Column(
                            modifier = Modifier.weight(1f).padding(start = 16.dp)
                        ) {
                            Text(text = gatto.nome, style = MaterialTheme.typography.titleMedium,
                                fontSize = 20.sp, fontFamily = FontFamily(Font(R.font.autouroneregular)),
                                modifier = Modifier.padding(bottom = 5.dp))
                            Text(
                                text = "Peso: ${gatto.peso} kg",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 14.sp,
                                fontFamily = FontFamily(Font(R.font.autouroneregular)),
                                modifier = Modifier.padding(bottom = 5.dp)
                            )
                            Text(
                                text = "Età: ${calculateAge(gatto.dataNascita)} anni",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 14.sp,
                                fontFamily = FontFamily(Font(R.font.autouroneregular)),
                                modifier = Modifier.padding(bottom = 5.dp)
                            )
                            Text(
                                text = "Sesso: ${gatto.sesso}",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 14.sp,
                                fontFamily = FontFamily(Font(R.font.autouroneregular))
                            )

                        }
//                        IconButton(onClick = { showDialog = true }) {
//                            Icon(Icons.Default.Edit, contentDescription = "Edit Cat")
//                        }
                    }
                }
                Carousel()

                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("Seleziona un'immagine") },
                        text = {
                            LazyColumn {
                                items(
                                    listOf(
                                        R.drawable.foto_profilo,
                                        R.drawable.foto_profilo,
                                        R.drawable.foto_profilo
                                    )
                                ) { image ->
                                    Image(
                                        painter = painterResource(id = image),
                                        contentDescription = "Selectable Image",
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clickable {
                                                // Handle image selection
                                                showDialog = false
                                            }
                                    )
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showDialog = false }) {
                                Text("Conferma")
                            }
                        }
                    )
                }
            }
        }
    }
}

fun calculateAge(birthDate: String): Int {
    val parts = birthDate.split("/")
    val day = parts[0].toInt()
    val month = parts[1].toInt()
    val year = parts[2].toInt()
    val dob = Calendar.getInstance()
    dob.set(year, month - 1, day)
    val today = Calendar.getInstance()
    var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
    if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
        age--
    }
    return age
}



