package com.openreel.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.openreel.app.ui.screens.feed.HomeFeedScreen

@Composable
fun OpenReelNavHost(navController: androidx.navigation.NavHostController) {
    NavHost(navController = navController, startDestination = "feed") {
        composable("feed") { HomeFeedScreen() }
    }
}
