package com.example.meoapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource

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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 16.dp)
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
            ) {
                LazyColumn {
                    items(gattiList.size) { index ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
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



@Composable
fun AddCats (navController: NavController){
    var nome by remember { mutableStateOf("") }
    var peso by remember { mutableStateOf("") }
    var dataNascita by remember { mutableStateOf("") }
    var dispenser by remember { mutableStateOf("") }
    var sesso by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome gatto") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )
        OutlinedTextField(
            value = peso,
            onValueChange = { peso = it },
            label = { Text("Peso") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )
        OutlinedTextField(
            value = dataNascita,
            onValueChange = { dataNascita = it },
            label = { Text("Data di nascita") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )
        OutlinedTextField(
            value = dispenser,
            onValueChange = { dispenser = it },
            label = { Text("Dispenser") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )
        OutlinedTextField(
            value = sesso,
            onValueChange = { sesso = it },
            label = { Text("Sesso") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )
        Button(
            onClick = {
                val gatto = mapOf(
                    "nome" to nome,
                    "peso" to peso,
                    "dataNascita" to dataNascita,
                    "dispenser" to dispenser,
                    "sesso" to sesso
                )
                val user = GlobalState.utente
                if (user != null) {
                    val database = FirebaseDatabase.getInstance().reference
                    database.child("Utenti").child(user.email.replace(".", ",")).child("gatti").push().setValue(gatto)
                }
                navController.navigate("cats")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Conferma")
        }
    }
}
