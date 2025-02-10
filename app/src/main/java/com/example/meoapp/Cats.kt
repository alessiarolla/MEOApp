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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlin.time.Duration.Companion.seconds

//import android.content.Context

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Cats(navController: NavController) {
    val pagerState = rememberPagerState(pageCount = { gattiList.size })

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("I TUOI GATTI", style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily(Font(R.font.autouroneregular)),
                    color = Color(0xFF7F5855),
                    fontSize = 26.sp
                ), modifier = Modifier.padding(top = 25.dp)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF3D6A9) // Sostituisci con il colore desiderato
                )
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
                    .padding(16.dp)
                //.background(Color(0xFFF3D6A9))
            ) {
                Divider(
                    modifier = Modifier.padding(bottom = 10.dp),
                    color = Color(0xFF7F5855),
                    thickness = 2.dp
                )
                Card(
                    onClick = { navController.navigate("addcats") },
                    modifier = Modifier
                        .align(Alignment.End)
                        .border(1.dp, Color(0xFF000000), RoundedCornerShape(25.dp))
                        .background(Color(0XFF7F5855), RoundedCornerShape(25.dp)),
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
                Spacer(modifier = Modifier.height(20.dp))
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
                                .fillMaxSize()
                                .clickable {
                                    GlobalState.gatto = cat.nome
                                    navController.navigate("catDetail/${cat.nome}")
                                },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            val iconResource = when (cat.icona) {
                                "foto-profilo1" -> R.drawable.foto_profilo
                                "foto-profilo2" -> R.drawable.foto_profilo2
                                "foto-profilo3" -> R.drawable.foto_profilo3
                                // Aggiungi altri casi per le altre icone
                                else -> R.drawable.foto_profilo // Icona di default se non corrisponde nessuna stringa
                            }
                            Image(
                                painter = painterResource(id = iconResource), // Replace with your drawable resource
                                contentDescription = "Cat Image",
                                modifier = Modifier.size(100.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = cat.nome,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    repeat(gattiList.size) { index ->
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
    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
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
                        dataNascita = "$day-$month-$year"
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(state = datePickerState)
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
                            onDismissRequest = { expanded = false }
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
                                    modifier = Modifier
                                        .background(Color(0xFFA37F6F))
                                        .fillMaxHeight()
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
                        val user = Utente("annalisa", "ciao1")
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
    var orario by remember { mutableStateOf("") }
    var quantita by remember { mutableStateOf("") }
    val isFormValid = orario.isNotBlank() && quantita.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Aggiungi pasto") },
        text = {
            Column {
                OutlinedTextField(
                    value = orario,
                    onValueChange = { orario = it },
                    //label = { Text("Orario") }
                )
                OutlinedTextField(
                    value = quantita,
                    onValueChange = { quantita = it },
                    //label = { Text("Quantità") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (isFormValid) {
                        val user = Utente("annalisa", "ciao1")
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
                Text("Aggiungi")
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
fun DeleteRoutineDialog(onDismiss: () -> Unit, orario: orario) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Conferma eliminazione") },
        text = { Text("Sei sicuro di voler eliminare questa routine?") },
        confirmButton = {
            TextButton(
                onClick = {
                    val user = Utente("annalisa", "ciao1")
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

    if (showAddRoutineDialog) {
        AddRoutineDialog(onDismiss = { showAddRoutineDialog = false })
    }
    if (showDeleteRoutineDialog) {
        DeleteRoutineDialog(onDismiss = { showDeleteRoutineDialog = false }, orariodelete)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
            .background(Color(0XFFF7E2C3), shape = RoundedCornerShape(16.dp)),
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
                Text(text = "Routine", style = MaterialTheme.typography.titleMedium)
            }
            LazyColumn {
                itemsIndexed(gattiList.find { it.nome == GlobalState.gatto }?.routine?: emptyList()) { index, routine ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Routine ${index + 1}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Row {
                                    IconButton(onClick = { showAddRoutineDialog = true }) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = "Edit Routine"
                                        )
                                    }
                                    IconButton(onClick = {
                                        showDeleteRoutineDialog = true
                                        orariodelete = routine
                                    }) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Delete Routine"
                                        )
                                    }
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.orario), // Replace with your drawable resource
                                    contentDescription = "Icona",
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = routine.ora,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.quantita), // Replace with your drawable resource
                                    contentDescription = "Icona",
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "${routine.quantita} grammi",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

        }
        Button(onClick = { showAddRoutineDialog = true },
            modifier = Modifier.align(Alignment.BottomCenter)
            ){
            Text("+ aggiungi routine")
        }
    }
}

@Composable
fun SecondPage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
            .background(Color(0XFFF7E2C3), shape = RoundedCornerShape(16.dp)),
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
                Text(text = "Cronologia pasti", style = MaterialTheme.typography.titleMedium)
            }
            gattiList.find { it.nome == GlobalState.gatto }?.cronologia?.forEachIndexed { index, routine ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "${routine.giorno ?: "N/A"}, h ${routine.ora}", style = MaterialTheme.typography.bodySmall)
                        }
                        Divider()
                        Text(text = "Quantità totale: ${routine.quantita} gr", style = MaterialTheme.typography.bodySmall)
                        Text(text = "Quantità mangiata: ${routine.mangiato ?: "N/A"} gr", style = MaterialTheme.typography.bodySmall)
                        Log.d("Routine", "Giorno: ${routine.giorno}, Mangiato: ${routine.mangiato}")
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

    val (averageGrams, averagePercentage) = calculateStatistics(cronologia, selectedPeriod)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
            .background(Color(0XFFF7E2C3), shape = RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "Statistiche", style = MaterialTheme.typography.titleMedium)

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedPeriod,
                    onValueChange = { selectedPeriod = it },
                    readOnly = true,
                    modifier = Modifier
                        //.fillMaxWidth()
                        .menuAnchor()
                        .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                        .height(50.dp)
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

            Text(text = "Media grammi mangiati: $averageGrams gr", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Media percentuale cibo mangiato: $averagePercentage%", style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(16.dp))

            // Placeholder for the chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .border(1.dp, Color.Black)
                    //.background(Color.LightGray)
            ) {
                // Implement the chart here
            }
        }
    }
}

fun calculateStatistics(cronologia: List<orario>, period: String): Pair<Double, Double> {
    val filteredCronologia = when (period) {
        "Settimanali" -> cronologia.filter {
            (it.giorno?.toLongOrNull() ?: 0L) >= Calendar.getInstance()
                .apply { add(Calendar.DAY_OF_YEAR, -7) }.timeInMillis
        }
        "Mensili" -> cronologia.filter {
            (it.giorno?.toLongOrNull() ?: 0L) >= Calendar.getInstance()
                .apply { add(Calendar.MONTH, -1) }.timeInMillis
        }
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
            .height(400.dp)
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
        "foto-profilo2" -> R.drawable.foto_profilo2
        "foto-profilo3" -> R.drawable.foto_profilo3
        // Aggiungi altri casi per le altre icone
        else -> R.drawable.foto_profilo // Icona di default se non corrisponde nessuna stringa
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DETTAGLI") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("cats") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
            Divider()
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = iconResource), // Replace with your drawable resource
                        contentDescription = "Cat Image",
                        modifier = Modifier.size(80.dp)
                    )
                    Column(
                        modifier = Modifier.weight(1f).padding(start = 16.dp)
                    ) {
                        Text(text = gatto.nome, style = MaterialTheme.typography.bodyLarge)
                        Text(text = "Peso: ${gatto.peso} kg", style = MaterialTheme.typography.bodySmall)
                        Text(text = "Età: ${calculateAge(gatto.dataNascita)} anni", style = MaterialTheme.typography.bodySmall)
                    }
                    IconButton(onClick = { showDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Cat")
                    }
                }
            }
            Carousel()

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Seleziona un'immagine") },
                    text = {
                        LazyColumn {
                            items(listOf(R.drawable.foto_profilo, R.drawable.foto_profilo, R.drawable.foto_profilo)) { image ->
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

fun calculateAge(birthDate: String): Int {
    val parts = birthDate.split("-")
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



