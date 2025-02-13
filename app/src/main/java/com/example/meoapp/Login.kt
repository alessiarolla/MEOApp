package com.example.meoapp

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

@SuppressLint("RememberReturnType")
@Composable
fun Login(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errore by remember { mutableStateOf("") }
    val database = FirebaseDatabase.getInstance()
    val utentiRef = database.getReference("Utenti")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Benvenuto",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
        Button(
            onClick = {
                utentiRef.child(username).get().addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        val dbPassword = snapshot.child("password").value as? String
                        if (dbPassword == password) {
                            GlobalState.username = username
                            navController.navigate("home")
                        } else {
                            errore = "Password errata"
                        }
                    } else {
                        errore = "Username errato"
                    }
                }.addOnFailureListener {
                    errore = "Errore di connessione"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }
        TextButton(onClick = { navController.navigate("registrazione") }) {
            Text("Non hai un account? Registrati ora")
        }
        if (errore.isNotEmpty()) {
            Text(
                text = errore,
                color = Color.Red,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Registrazione(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    val database = FirebaseDatabase.getInstance()
    val utentiRef = database.getReference("Utenti")
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("REGISTRAZIONE", style = MaterialTheme.typography.titleMedium.copy(
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

                Column(
                    modifier = Modifier.fillMaxSize().background(Color(0xFFF3D6A9)).padding(16.dp)
                ) {
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    )
                    Button(
                        onClick = {
                            val nuovoUtente = mapOf(
                                "email" to username,
                                "password" to password,
                                "nomeUtente" to username,
                                "gatti" to emptyMap<String, Any>(),
                                "dispensers" to emptyList<Any>(),
                                "notifichePush" to true
                            )
                            utentiRef.child(username).setValue(nuovoUtente).addOnSuccessListener {
                                showDialog = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Registrati")
                    }
                }
                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = {},
                        title = { Text("Registrazione completata") },
                        text = { Text("Utente registrato con successo") },
                        confirmButton = {
                            Button(onClick = { navController.navigate("login") }) {
                                Text("Continua")
                            }
                        }
                    )
                }
            }
        }
    }
}