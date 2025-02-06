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
import androidx.compose.material.icons.filled.Delete

//import android.content.Context

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Cats(navController: NavController) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("I TUOI GATTI") },
            )
            Divider()
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text("Seleziona un gatto")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 16.dp)
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
            ) {
                LazyColumn {
                    items(gattiList) { cat ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable {
                                    GlobalState.gatto = cat.nome
                                    navController.navigate("catDetail/${cat.nome}") }
                        ) {
                            Image(
                                //anche l'immagine dovrà cambiare con il gatto
                                painter = painterResource(id = R.drawable.foto_profilo), // Replace with your drawable resource
                                contentDescription = "Cat Image",
                                modifier = Modifier.size(50.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = cat.nome,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
            Card(
                onClick = { navController.navigate("addcats") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Aggiungi gatto",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
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
    var selectedImage by remember { mutableIntStateOf(R.drawable.foto_profilo) }
    var showDatePicker by remember { mutableStateOf(false) }


    val isFormValid = nome.isNotBlank() && peso.isNotBlank() && dataNascita.isNotBlank() && dispenser.isNotBlank() && sesso.isNotBlank()

    if (showDatePicker) {
        val context = LocalContext.current
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->
                dataNascita = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                showDatePicker = false
            },
            year, month, day
        ).show()
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AGGIUNGI GATTO") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(MaterialTheme.colorScheme.primary, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = selectedImage),
                    contentDescription = "Cat Image",
                    modifier = Modifier.size(80.dp)
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
                            items(listOf(R.drawable.foto_profilo, R.drawable.foto_profilo, R.drawable.foto_profilo)) { image ->
                                Image(
                                    painter = painterResource(id = image),
                                    contentDescription = "Selectable Image",
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clickable {
                                            selectedImage = image
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
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Nome gatto", modifier = Modifier.alignByBaseline())
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    modifier = Modifier.alignByBaseline().weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Peso", modifier = Modifier.alignByBaseline())
                OutlinedTextField(
                    value = peso,
                    onValueChange = { peso = it },
                    modifier = Modifier.alignByBaseline().weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Data di nascita", modifier = Modifier.alignByBaseline())
                OutlinedTextField(
                    value = dataNascita,
                    onValueChange = { dataNascita = it },
                    readOnly = true,
                    modifier = Modifier
                        .alignByBaseline()
                        .weight(1f)
                        .clickable { showDatePicker = true }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Dispenser", modifier = Modifier.alignByBaseline())
                OutlinedTextField(
                    value = dispenser,
                    onValueChange = { dispenser = it },
                    modifier = Modifier.alignByBaseline().weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Sesso", modifier = Modifier.alignByBaseline())
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
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        sessoOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    sesso = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
            Button(
                onClick = {
//                    val gatto = mapOf(
//                        "nome" to nome,
//                        "peso" to peso,
//                        "dataNascita" to dataNascita,
//                        "dispenser" to dispenser,
//                        "sesso" to sesso,
//                        "icona" to selectedImage
//                    )
//                    val user = GlobalState.utente
//                    if (user != null) {
//                        val database = FirebaseDatabase.getInstance().reference
//                        database.child("Utenti").child(user.email.replace(".", ",")).child("gatti").push().setValue(gatto)
//                    }
                    navController.navigate("cats")

                },
                modifier = Modifier.fillMaxWidth(),
                //enabled = isFormValid
            ) {
                Text("Conferma")
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
                        val user = GlobalState.utente
                        if (user != null) {
                            val database = FirebaseDatabase.getInstance().reference
                            val gattoName = GlobalState.gatto
                            val gattiRef = database.child("Utenti").child(user.nome).child("gatti").child(gattoName).child("routine")
                            val newRoutine = orario(orario, quantita)
                            gattiRef.push().setValue(newRoutine)
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
fun FirstPage() {
    var showAddRoutineDialog by remember { mutableStateOf(false) }

    if (showAddRoutineDialog) {
        AddRoutineDialog(onDismiss = { showAddRoutineDialog = false })
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
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
            gattiList.find { it.nome == GlobalState.gatto }?.routine?.forEachIndexed { index, routine ->
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
                            Text(text = "Routine ${index + 1}", style = MaterialTheme.typography.bodySmall)
                            Row {
                                IconButton(onClick = { /* Handle edit routine */ }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit Routine")
                                }
                                IconButton(onClick = { /* Handle delete routine */ }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Routine")
                                }
                            }
                        }
                        Text(text = "Orario: ${routine.ora}", style = MaterialTheme.typography.bodySmall)
                        Text(text = "Quantità: ${routine.quantita} grammi", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

        }
        Button(onClick = {
            showAddRoutineDialog = true },
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
            .height(400.dp)
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
                            Text(text = "${routine.giorno}, h ${routine.ora}", style = MaterialTheme.typography.bodySmall)
                        }
                        Divider()
                        Text(text = "Quantità totale: ${routine.quantita} gr", style = MaterialTheme.typography.bodySmall)
                        Text(text = "Quantità mangiata: ${routine.mangiato} gr", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
fun ThirdPage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .background(Color.Blue, shape = RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Third Page", style = TextStyle(fontSize = 20.sp, color = Color.White))
    }
}

@Composable
fun Carousel() {
    val pages = listOf<@Composable () -> Unit>({ FirstPage() }, { SecondPage() }, { ThirdPage() })
    val pagerState = rememberPagerState(pageCount = { pages.size })

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth().height(200.dp)
        ) { page ->
            pages[page]()
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatDetail(navController: NavController, gatto: gatto) {
    var showDialog by remember { mutableStateOf(false) }
    //val pagerState = rememberPagerState()

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
//                    Image(
//                        painter = painterResource(gatto.icona.toInt()), // Replace with your drawable resource
//                        contentDescription = "Cat Image",
//                        modifier = Modifier.size(80.dp)
//                    )
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



