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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.meoapp.ui.theme.MEOAppTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

val customFontFamily = FontFamily(
    Font(R.font.autouroneregular, FontWeight.Normal)
)

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
                val isLoggedIn = remember { mutableStateOf(false) }
                checkIfUserIsLoggedIn { isLoggedIn.value = it }

                Scaffold(
                    bottomBar = {
                        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                        if (currentRoute != "login" && currentRoute != "registrazione") {
                            BottomBar(navController)
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = if (isLoggedIn.value) {
                            database.child("Utenti").addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    GlobalState.username = snapshot.child("utenteLoggato").getValue(String::class.java) ?: ""
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Log.e("MainActivity", "Database error: ${error.message}")
                                }
                            })
                            "home"
                        } else "login",
                        Modifier.padding(innerPadding)
                    ) {
                        composable("login") { Login(navController) }
                        composable("registrazione") { Registrazione(navController) }
                        composable("home") { Homepage(navController) }
                        composable("settings") { Settings(navController) }
                        composable("cats") { Cats(navController) }
                        composable("cats/addcats") { AddCats(navController) }
                        composable("cats/catDetail/{catName}") { backStackEntry ->
                            val catName = backStackEntry.arguments?.getString("catName")
                            val gatto = gattiList.find { it.nome == catName }
                            if (gatto != null) {
                                CatDetail(navController, gatto)
                            } else {
                                Log.e("MainActivity", "Cat not found: $catName")
                            }
                        }
                        composable("home/dispenserDetail/{dispenserId}") { backStackEntry ->
                            val dispenserId = backStackEntry.arguments?.getString("dispenserId")?.toLongOrNull()
                            if (dispenserId != null) {
                                DispenserDetail(navController, dispenserId)
                            } else {
                                Log.e("MainActivity", "Dispenser ID not found or invalid")
                            }
                        }
                        composable("home/notification") { Notification(navController) }

                    }
                }
            }
        }
    }

    private fun checkIfUserIsLoggedIn(callback: (Boolean) -> Unit) {
        database.child("Utenti").child("loggato").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isLoggedIn = snapshot.getValue(Boolean::class.java) ?: false
                callback(isLoggedIn)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MainActivity", "Database error: ${error.message}")
                callback(false)
            }
        })
    }

    private fun startRoutineChecker() {
        routineCheckerRunnable = object : Runnable {
            override fun run() {
                fetchAndCheckData()  // Controlla i dati ogni secondo
                handler.postDelayed(this, 1000)  // Ripeti ogni secondo
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
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private val sentNotifications = mutableMapOf<String, Boolean>()
    private fun fetchAndCheckData() {
        var userId = GlobalState.username
        database.child("Utenti").child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val notifichePush = snapshot.child("notifichePush").getValue(Boolean::class.java) ?: false
                if (notifichePush) {
                    val dispensers = snapshot.child("dispensers").children
                    for (dispenser in dispensers) {
                        val nomeDispenser = dispenser.child("nome").getValue(String::class.java) ?: "Dispenser"
                        val livelloCiboCiotola = dispenser.child("livelloCiboCiotola").getValue(Int::class.java) ?: 0
                        val livelloCiboDispenser = dispenser.child("livelloCiboDispenser").getValue(Int::class.java) ?: 0
                        if (livelloCiboCiotola == 0) {
                            if (!sentNotifications.containsKey("ciotola_$nomeDispenser")) {
                                sendNotification(userId, "La ciotola del dispenser \"$nomeDispenser\" è vuota!")
                                sentNotifications["ciotola_$nomeDispenser"] = true
                            }
                        } else {
                            sentNotifications.remove("ciotola_$nomeDispenser")
                        }
                        if (livelloCiboDispenser == 0) {
                            if (!sentNotifications.containsKey("dispenser_$nomeDispenser")) {
                                sendNotification(userId, "Il dispenser \"$nomeDispenser\" è vuoto!")
                                sentNotifications["dispenser_$nomeDispenser"] = true
                            }
                        } else {
                            sentNotifications.remove("dispenser_$nomeDispenser")
                        }
                    }
                    val gatti = snapshot.child("gatti").children
                    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                    val currentTime = dateFormat.format(Date())
                    for (gatto in gatti) {
                        val nomeGatto = gatto.child("nome").getValue(String::class.java) ?: "Gatto"
                        val routine = gatto.child("routine").children
                        for (orario in routine) {
                            val oraRoutine = orario.child("ora").getValue(String::class.java)
                            val quantitaRoutine = orario.child("quantita").getValue(String::class.java)

                            if (oraRoutine != null && oraRoutine == currentTime) {
                                if (!sentNotifications.containsKey("routine_$nomeGatto")) {
                                    sendNotification(userId, "È ora del pasto per $nomeGatto!")
                                    sentNotifications["routine_$nomeGatto"] = true
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        sentNotifications.remove("routine_$nomeGatto")
                                    }, 60000)
                                    // Aggiorna ultimoPasto nel database
                                    val ultimoPasto = mapOf(
                                        "ora" to currentTime,
                                        "quantita" to quantitaRoutine
                                    )
                                    database.child("Utenti").child(userId).child("gatti").child(gatto.key!!).child("ultimoPasto").setValue(ultimoPasto)

                                }
                            }
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("MainActivity", "Database error: ${error.message}")
            }
        })
    }




    private fun saveNotificationToDatabase(userId: String, message: String) {
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val notification = mapOf(
            "ora" to currentTime,
            "data" to currentDate,
            "testo" to message
        )

        val notificationsRef = database.child("Utenti").child(userId).child("notifiche")
        notificationsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val notificationsList = snapshot.children.map { it.value as Map<String, String> }.toMutableList()
                notificationsList.add(notification)
                notificationsRef.setValue(notificationsList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MainActivity", "Database error: ${error.message}")
            }
        })
    }

    private fun sendNotification(userId: String, message: String) {
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
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            notify(System.currentTimeMillis().toInt(), builder.build())
        }

        // Save the notification to the database
        saveNotificationToDatabase(userId, message)
    }

    @Composable
    fun BottomBar(navController: NavController) {
        val items = listOf(
            BottomNavItem("settings", R.drawable.menu_account),
            BottomNavItem("home", R.drawable.menu_home),
            BottomNavItem("cats", R.drawable.menu_gatto)
        )

        NavigationBar(
            containerColor = Color(0xFFA37F6F),
            modifier = Modifier.height(80.dp)
        ) {
            val navBackStackEntry = navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry.value?.destination?.route

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                items.forEach { item ->
                    val isSelected = currentRoute?.startsWith(item.route) == true || currentRoute?.contains(item.route) == true
                    NavigationBarItem(
                        label = {},
                        icon = {
                            Image(
                                painter = painterResource(id = item.iconRes),
                                contentDescription = null,
                                modifier = Modifier.size(70.dp)
                            )
                        },
                        selected = isSelected,

                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF7F5855), // icona selezionata
                            selectedTextColor = Color(0xFF7F5855), // testo selezionato
                            indicatorColor = Color(0xFF7F5855), // indicatore di selezione

                        ),
                        )
                }
            }
        }
    }

    data class BottomNavItem(val route: String, val iconRes: Int)
    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        MEOAppTheme {
        }
    }
}