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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import java.util.Calendar

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
                    items(gattiList.size) { index ->
                        val gatto = gattiList[index]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable { navController.navigate("catDetail/${gatto.nome}") }
                        ) {
                            Image(
                                //anche l'immagine dovrà cambiare con il gatto
                                painter = painterResource(id = R.drawable.foto_profilo), // Replace with your drawable resource
                                contentDescription = "Cat Image",
                                modifier = Modifier.size(50.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = gattiList[index].nome as? String ?: "",
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
            if (showDatePicker) {
                val context = LocalContext.current
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)

                android.app.DatePickerDialog(
                    context,
                    { _, selectedYear, selectedMonth, selectedDay ->
                        dataNascita = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                        showDatePicker = false
                    },
                    year, month, day
                ).show()
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
                enabled = isFormValid
            ) {
                Text("Conferma")
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
                title = { Text(gatto.nome) },
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
                        painter = painterResource(gatto.icona.toInt()), // Replace with your drawable resource
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

