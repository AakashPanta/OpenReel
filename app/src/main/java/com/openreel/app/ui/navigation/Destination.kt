package com.openreel.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Destination(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    data object Feed : Destination("feed", "Home", Icons.Rounded.Home)
    data object Explore : Destination("explore", "Explore", Icons.Rounded.Explore)
    data object Upload : Destination("upload", "Create", Icons.Rounded.AddCircle)
    data object Alerts : Destination("alerts", "Alerts", Icons.Rounded.Notifications)
    data object Profile : Destination("profile", "Profile", Icons.Rounded.Person)
}

val bottomDestinations = listOf(
    Destination.Feed,
    Destination.Explore,
    Destination.Upload,
    Destination.Alerts,
    Destination.Profile
)
