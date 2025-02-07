package com.example.meoapp

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.meoapp.ui.theme.MEOAppTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {

    private lateinit var database: DatabaseReference
    private val CHANNEL_ID = "dispenser_notifications"
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var routineCheckerRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        database = FirebaseDatabase.getInstance().reference
        createNotificationChannel()
        requestNotificationPermission()
        fetchAndCheckData()
        fetchGattiFromFirebase()
        startRoutineChecker()  // Avvio del controllo periodico
        setContent {
            MEOAppTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = { BottomBar(navController) }
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
                        composable("catDetail/{catName}") { backStackEntry ->
                            val catName = backStackEntry.arguments?.getString("catName")
                            val gatto = gattiList.find { it.nome == catName }
                            if (gatto != null) {
                                CatDetail(navController, gatto)
                            } else {
                                Log.e("MainActivity", "Cat not found: $catName")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun startRoutineChecker() {
        routineCheckerRunnable = object : Runnable {
            override fun run() {
                fetchAndCheckData()  // Controlla i dati ogni secondo
                handler.postDelayed(this, 60000)  // Ripeti ogni minuto
            }
        }
        handler.post(routineCheckerRunnable)
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                val requestPermissionLauncher = registerForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted: Boolean ->
                    if (isGranted) {
                        Log.d("MainActivity", "Notification permission granted")
                    } else {
                        Log.d("MainActivity", "Notification permission denied")
                    }
                }
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Dispenser Notifications"
            val descriptionText = "Notifications for food dispenser alerts"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun fetchAndCheckData() {
        val userId = "annalisa"
        database.child("Utenti").child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dispensers = snapshot.child("dispensers").children
                for (dispenser in dispensers) {
                    val nomeDispenser = dispenser.child("nome").getValue(String::class.java) ?: "Dispenser"
                    val livelloCiboCiotola = dispenser.child("livelloCiboCiotola").getValue(Int::class.java) ?: 0
                    val livelloCiboDispenser = dispenser.child("livelloCiboDispenser").getValue(Int::class.java) ?: 0

                    if (livelloCiboCiotola == 0) {
                        sendNotification("La ciotola del dispenser \"$nomeDispenser\" è vuota!")
                    }
                    if (livelloCiboDispenser == 0) {
                        sendNotification("Il dispenser \"$nomeDispenser\" è vuoto!")
                    }
                }

                val gatti = snapshot.child("gatti").children
                val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val currentTime = dateFormat.format(Date())

                for (gatto in gatti) {
                    val nomeGatto = gatto.child("nome").getValue(String::class.java) ?: "Gatto"
                    val routine = gatto.child("routine").children
                    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                    val currentTime = dateFormat.format(Date())

                    for (orario in routine) {
                        val oraRoutine = orario.child("ora").getValue(String::class.java)
                        if (oraRoutine != null && oraRoutine == currentTime) {
                            sendNotification("È ora del pasto per $nomeGatto!")
                        }
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MainActivity", "Database error: ${error.message}")
            }
        })
    }

    private fun sendNotification(message: String) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.e("MainActivity", "Permission for notifications not granted")
            return
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("MEOApp")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            notify(System.currentTimeMillis().toInt(), builder.build())
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