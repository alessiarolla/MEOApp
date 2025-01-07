package com.example.meoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.meoapp.ui.theme.MEOAppTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this) // Initialize Firebase
        enableEdgeToEdge()
        setContent {
            MEOAppTheme {
                val navController = rememberNavController()

                Scaffold(
                    bottomBar = {
                        BottomBar(navController)
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        Modifier.padding(innerPadding)
                    ) {
                    composable("login") { Login(navController) }
                    composable("home") { Homepage(navController) }
                    composable("settings") { Settings(navController) }
                    composable("cats") { Cats(navController) }
                    composable("addcats") { AddCats(navController) }
                }
            }
        }
    }
}

    @Composable
    fun BottomBar(navController: NavController) {
        val items = listOf(
            BottomNavItem("settings", "Settings"),
            BottomNavItem("home", "Homepage"),
            BottomNavItem("cats", "Cats")
        )

        NavigationBar {
            items.forEach { item ->
                NavigationBarItem(
                    label = { Text(item.label) },
                    icon = {},
                    selected = navController.currentDestination?.route == item.route,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }

    data class BottomNavItem(val route: String, val label: String)



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MEOAppTheme {

    }
}
}
