package com.example.meoapp

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

@SuppressLint("RememberReturnType")
@Composable
fun Login(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errore by remember { mutableStateOf("") }
    val database = FirebaseDatabase.getInstance()
    val utentiRef = database.getReference("Utenti")

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFF3D6A9))
        .padding(16.dp)
    ){
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {



            Image(
                painter = painterResource(id = R.drawable.logo_login),
                contentDescription = "App Logo",
                modifier = Modifier
                    .padding(4.dp)
                    .padding(top = 40.dp)
                    .size(140.dp)
            )
        }

        Spacer(modifier = Modifier.height(50.dp))


        Text(
            text = "Benvenuto",
            fontFamily = customFontFamily,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))


        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = {
                Text("Username",
                    fontFamily = customFontFamily,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                    modifier = Modifier.fillMaxWidth(),
                ) },
            modifier = Modifier
                .width(250.dp)
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        var passwordVisible by remember { mutableStateOf(false) }

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = {
                Text("Password",
                    fontFamily = customFontFamily,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                    modifier = Modifier.fillMaxWidth(),
            ) },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) R.drawable.visible else R.drawable.not_visible
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Image(
                        painter = painterResource(id = image),
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            modifier = Modifier
                .width(250.dp)
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 10.dp)
        )

        TextButton(
            onClick = { navController.navigate("registrazione") },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                buildAnnotatedString {
                    append("Non hai un account? ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Registrati ora")
                    }
                },
                fontFamily = customFontFamily,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 10.sp),
                color = Color(0xFF000000),
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        Button(
            onClick = {
                if (username.isNotEmpty() && password.isNotEmpty()) {
                    val utentiRef = database.getReference("Utenti")
                    utentiRef.orderByChild("nomeUtente").equalTo(username).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val userSnapshot = snapshot.children.firstOrNull()
                            if (userSnapshot != null) {
                                val dbPassword = userSnapshot.child("password").getValue(String::class.java)
                                if (dbPassword == password) {
                                    utentiRef.child("loggato").setValue(true)
                                    utentiRef.child("utenteLoggato").setValue(username)
                                    GlobalState.username = username
                                    navController.navigate("home")
                                } else {
                                    errore = "Password errata"
                                }
                            } else {
                                errore = "Username errato"
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            errore = "Errore di connessione"
                        }
                    })
                }
            },
            modifier = Modifier
                .width(120.dp)
                .align(Alignment.CenterHorizontally)
                .background(Color(0xFF7F5855), RoundedCornerShape(20.dp))
                .border(1.dp, Color(0xFF000000), RoundedCornerShape(20.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7F5855)),
        ) {
            Text(
                "Accedi",
                fontFamily = customFontFamily,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }


        if (errore.isNotEmpty()) {
            Text(
                text = errore,
                fontFamily = customFontFamily,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
                color = Color.Red,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(Alignment.CenterHorizontally)
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
    var errore by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3D6A9))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {


            Image(
                painter = painterResource(id = R.drawable.logo_login),
                contentDescription = "App Logo",
                modifier = Modifier
                    .padding(4.dp)
                    .padding(top = 40.dp)
                    .size(140.dp)
            )
        }

        Spacer(modifier = Modifier.height(50.dp))


        Text(
            text = "Registati",
            fontFamily = customFontFamily,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = {
                Text(
                    "Username",
                    fontFamily = customFontFamily,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            modifier = Modifier
                .width(250.dp)
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        var passwordVisible by remember { mutableStateOf(false) }

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = {
                Text(
                    "Password",
                    fontFamily = customFontFamily,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) R.drawable.visible else R.drawable.not_visible
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Image(
                        painter = painterResource(id = image),
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            modifier = Modifier
                .width(250.dp)
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 10.dp)
        )

        TextButton(
            onClick = { navController.navigate("login") },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                buildAnnotatedString {
                    append("Hai già un account? ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Accedi")
                    }
                },
                fontFamily = customFontFamily,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 10.sp),
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = {
                if (username.isNotEmpty() && password.isNotEmpty()) {
                    utentiRef.child(username).get().addOnSuccessListener { snapshot ->
                        if (snapshot.exists()) {
                            showDialog = true
                            errore = "Username già in uso"
                        } else {
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
                                errore = "Utente registrato con successo"
                            }
                        }
                    }
                }
            },
            modifier = Modifier
                .width(150.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                "Registrati",
                fontFamily = customFontFamily,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Registrazione",
                    fontFamily = customFontFamily,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center)
                },
                text = { Text(errore,
                    fontFamily = customFontFamily,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 12.sp),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center)
                },
                confirmButton = {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Button(onClick = { showDialog = false },
                            modifier = Modifier
                                .width(80.dp)
                                .align(Alignment.Center)) {
                            Text("OK",
                                fontFamily = customFontFamily,
                                style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center)
                        }}
                }
            )
        }
    }}








//se voglio mettere freccia per tornare indietro invece
/*
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
                title = {
                    Text(
                        "REGISTRAZIONE",
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
                        onClick = { navController.navigate("login") },
                        modifier = Modifier.padding(top = 25.dp)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF7F5855))
                    }
                }
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
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF3D6A9))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                )  {
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = {
                            Text("Username",
                            fontFamily = customFontFamily,
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                            modifier = Modifier.fillMaxWidth(),
                            ) },
                        modifier = Modifier
                            .width(250.dp)
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 16.dp)
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = {
                            Text("Password",
                                fontFamily = customFontFamily,
                                style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                                modifier = Modifier.fillMaxWidth(),)
                                },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .width(250.dp)
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 16.dp)
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
                        modifier = Modifier
                            .width(110.dp)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Text("Registrati",
                            fontFamily = customFontFamily,
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center

                        )
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


 */