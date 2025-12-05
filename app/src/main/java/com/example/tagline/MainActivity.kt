package com.example.tagline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.tagline.ui.navigation.NavRoutes
import com.example.tagline.ui.navigation.TaglineNavHost
import com.example.tagline.ui.theme.TaglineTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaglineTheme {
                TaglineApp(isLoggedIn = firebaseAuth.currentUser != null)
            }
        }
    }
}

@Composable
fun TaglineApp(isLoggedIn: Boolean) {
    val navController = rememberNavController()
    val startDestination = remember {
        if (isLoggedIn) NavRoutes.Search.route else NavRoutes.Login.route
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        TaglineNavHost(
            navController = navController,
            startDestination = startDestination
        )
    }
}
