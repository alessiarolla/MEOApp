package com.example.meoapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


@Composable
fun Settings(navController: NavController) {
    var userEmail = "annalisa"
    val database = FirebaseDatabase.getInstance().reference.child("Utenti")

    var nome by remember { mutableStateOf("") }

    LaunchedEffect(userEmail) {
        database.orderByChild("email").equalTo(userEmail).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userSnapshot = snapshot.children.firstOrNull()
                userSnapshot?.let {
                    nome = it.child("nome").getValue(String::class.java) ?: ""
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    Column(modifier = Modifier.
    fillMaxSize().background(Color(0xFFF3D6A9)).padding(16.dp)
    ){
        Text(
            text = "Account",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontFamily = customFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )

        Row(modifier = Modifier.fillMaxWidth().padding(6.dp)) {
            Text(
                text = "Il tuo nome:",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start,
                fontFamily = customFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = nome,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End,
                fontFamily = customFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }



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