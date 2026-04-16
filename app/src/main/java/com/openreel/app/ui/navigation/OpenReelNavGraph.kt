package com.openreel.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.openreel.app.ui.explore.ExploreScreen
import com.openreel.app.ui.explore.ExploreViewModel
import com.openreel.app.ui.feed.FeedScreen
import com.openreel.app.ui.feed.FeedViewModel
import com.openreel.app.ui.notifications.NotificationsScreen
import com.openreel.app.ui.notifications.NotificationsViewModel
import com.openreel.app.ui.profile.ProfileScreen
import com.openreel.app.ui.profile.ProfileViewModel
import com.openreel.app.ui.upload.UploadScreen
import com.openreel.app.ui.upload.UploadViewModel

@Composable
fun OpenReelNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Destination.Feed.route,
        modifier = modifier
    ) {
        composable(Destination.Feed.route) {
            FeedScreen(viewModel = viewModel<FeedViewModel>())
        }
        composable(Destination.Explore.route) {
            ExploreScreen(viewModel = viewModel<ExploreViewModel>())
        }
        composable(Destination.Upload.route) {
            UploadScreen(viewModel = viewModel<UploadViewModel>())
        }
        composable(Destination.Alerts.route) {
            NotificationsScreen(viewModel = viewModel<NotificationsViewModel>())
        }
        composable(Destination.Profile.route) {
            ProfileScreen(viewModel = viewModel<ProfileViewModel>())
        }
    }
}
