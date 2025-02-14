package com.example.meoapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.format.TextStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(navController: NavController) {
    var userEmail = GlobalState.username
    val database = FirebaseDatabase.getInstance().reference.child("Utenti")


    var nomeUtente by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var notifichePush by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var showDialogPass by remember { mutableStateOf(false) }




    LaunchedEffect(userEmail) {
        database.orderByChild("email").equalTo(userEmail).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userSnapshot = snapshot.children.firstOrNull()
                userSnapshot?.let {
                    nomeUtente = it.child("nomeUtente").getValue(String::class.java) ?: ""
                    email = it.child("email").getValue(String::class.java) ?: ""
                    password = it.child("password").getValue(String::class.java) ?: ""
                    notifichePush = it.child("notifichePush").getValue(Boolean::class.java) ?: true
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },

            title = {
                Text(
                    text = "Modifica nome Utente",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontFamily = customFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            },
            text = {
                Card(
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                    TextField(
                        value = nomeUtente,
                        onValueChange = { nomeUtente = it },
                        modifier = Modifier.weight(1f),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            textAlign = TextAlign.Start,
                            fontFamily = customFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),singleLine = true
                        )
                }}
            },
            confirmButton = {
                Button(
                    onClick = {},
                    /*onClick = {
                        database.child(userEmail).child("nomeUtente").setValue(nomeUtente)
                        database.child(userEmail).child("email").setValue(nomeUtente)
                        GlobalState.username = nomeUtente
                        showDialog = false
                        navController.navigate("settings") {
                            popUpTo("settings") { inclusive = true }
                        }
                    },*/
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0XFF7F5855))

                ) {
                    Text("Conferma")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0XFF7F5855))

                ) {
                    Text("  Annulla  ")
                }
            },
            containerColor = Color(0xFFF7E2C3) // Set the background color to match the rest

        )
    }




    if (showDialogPass) {
        var passwordInput by remember { mutableStateOf(password) }
        var isPasswordVisible by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showDialogPass = false },
            title = {
                Text(
                    text = "Modifica password",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontFamily = customFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            },
            text = {
                Card {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = passwordInput,
                            onValueChange = { passwordInput = it },
                            modifier = Modifier.weight(1f),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                textAlign = TextAlign.Start,
                                fontFamily = customFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            singleLine = true,
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Image(
                            painter = painterResource(id = if (isPasswordVisible) R.drawable.visible else R.drawable.not_visible),
                            contentDescription = if (isPasswordVisible) "Nascondi password" else "Mostra password",
                            modifier = Modifier
                                .size(30.dp)
                                .padding(4.dp)
                                .clickable { isPasswordVisible = !isPasswordVisible }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (passwordInput.isNotEmpty()) {
                            password = passwordInput
                            database.child(userEmail).child("password").setValue(password)
                            showDialogPass = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0XFF7F5855))
                ) {
                    Text("Conferma")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialogPass = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0XFF7F5855))
                ) {
                    Text("Annulla")
                }
            },
            containerColor = Color(0xFFF7E2C3)
        )
    }




    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ACCOUNT", style = MaterialTheme.typography.titleMedium.copy(
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

                Column(modifier = Modifier.
                fillMaxSize().background(Color(0xFFF3D6A9)).padding(16.dp)
                ){

                    Spacer(modifier = Modifier.height(40.dp))


                    //nome utente
                    Row(modifier = Modifier.fillMaxWidth().padding(6.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Il tuo nome utente:",
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Start,
                            fontFamily = customFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = email,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End,
                            fontFamily = customFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.width(8.dp)) // Aggiunto uno spazio qui


                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Icon",
                            modifier = Modifier
                                .size(24.dp) // Adjust the size as needed
                                .padding(2.dp) // Reduced padding
                                .clickable { showDialog = true }
                        )
                    }

                    Spacer(modifier = Modifier.height(35.dp))


                    //password
                    Row(modifier = Modifier.fillMaxWidth().padding(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "La tua password:",
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Start,
                            fontFamily = customFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "*".repeat(password.length),
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End,
                            fontFamily = customFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.width(8.dp)) // Aggiunto uno spazio qui


                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Icon",
                            modifier = Modifier
                                .size(24.dp) // Adjust the size as needed
                                .padding(2.dp) // Reduced padding
                                .clickable { showDialogPass = true }
                        )
                    }


                    Spacer(modifier = Modifier.height(35.dp))


                    //notifichePush
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ){      Text(
                        text = "Attiva/disattiva \n notifiche push:",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Start,
                        fontFamily = customFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                        Checkbox(
                            checked = notifichePush,
                            onCheckedChange = { isChecked ->
                                notifichePush = isChecked
                                database.child(userEmail).child("notifichePush").setValue(isChecked)
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFFA06558),
                                uncheckedColor = Color(0xFFA06558),
                                checkmarkColor = Color.White
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(80.dp))


                    Text(
                        text = "Contattaci per ricevere assistenza o per segnalare un problema:",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 26.dp, end = 26.dp),
                        textAlign = TextAlign.Center,
                        fontFamily = customFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(30.dp))


                    Row(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
                        Text(
                            text = "Mail:",
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Start,
                            fontFamily = customFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "_____________",
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End,
                            fontFamily = customFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(15.dp))


                    Row(modifier = Modifier.fillMaxWidth().padding(6.dp)) {
                        Text(
                            text = "Telefono:",
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Start,
                            fontFamily = customFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "_____________",
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End,
                            fontFamily = customFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }




                }


            }
        }
    }







}