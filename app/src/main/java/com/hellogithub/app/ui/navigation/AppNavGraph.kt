package com.hellogithub.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.hellogithub.app.ui.feed.FeedScreen
import com.hellogithub.app.ui.detail.DetailScreen
import com.hellogithub.app.ui.search.SearchScreen
import com.hellogithub.app.ui.periodical.PeriodicalScreen
import com.hellogithub.app.ui.rank.RankScreen
import com.hellogithub.app.ui.settings.SettingsScreen

object Routes {
    const val FEED = "feed"
    const val PERIODICAL = "periodical"
    const val SEARCH = "search"
    const val RANK = "rank"
    const val SETTINGS = "settings"
    const val DETAIL = "detail/{rid}"

    fun detail(rid: String) = "detail/$rid"
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Routes.FEED,
        modifier = modifier,
    ) {
        composable(Routes.FEED) {
            FeedScreen(
                onNavigateToDetail = { rid -> navController.navigate(Routes.detail(rid)) }
            )
        }
        composable(Routes.PERIODICAL) {
            PeriodicalScreen(
                onNavigateToDetail = { rid -> navController.navigate(Routes.detail(rid)) }
            )
        }
        composable(Routes.SEARCH) {
            SearchScreen(
                onNavigateToDetail = { rid -> navController.navigate(Routes.detail(rid)) }
            )
        }
        composable(Routes.RANK) {
            RankScreen(
                onNavigateToDetail = { rid -> navController.navigate(Routes.detail(rid)) }
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen()
        }
        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("rid") { type = NavType.StringType })
        ) { backStackEntry ->
            val rid = backStackEntry.arguments?.getString("rid") ?: return@composable
            DetailScreen(rid = rid, onBack = { navController.popBackStack() })
        }
    }
}
