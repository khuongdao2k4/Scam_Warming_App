package com.example.scam_warming_app

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import com.example.scam_warming_app.data.local.SessionManager
import com.example.scam_warming_app.data.remote.ApiService
import com.example.scam_warming_app.data.remote.RegisterRequest
import com.example.scam_warming_app.presentation.blacklist.BlacklistScreen
import com.example.scam_warming_app.presentation.blacklist.BlacklistViewModel
import com.example.scam_warming_app.presentation.detail.DetailScreen
import com.example.scam_warming_app.presentation.detail.DetailViewModel
import com.example.scam_warming_app.presentation.history.HistoryScreen
import com.example.scam_warming_app.presentation.history.HistoryViewModel
import com.example.scam_warming_app.presentation.home.HomeScreen
import com.example.scam_warming_app.presentation.home.HomeViewModel
import com.example.scam_warming_app.presentation.onboarding.OnboardingScreen
import com.example.scam_warming_app.presentation.onboarding.OnboardingViewModel
import com.example.scam_warming_app.presentation.permission.PermissionScreen
import com.example.scam_warming_app.presentation.report.ReportScreen
import com.example.scam_warming_app.presentation.report.ReportViewModel
import com.example.scam_warming_app.presentation.settings.SettingsScreen
import com.example.scam_warming_app.presentation.settings.SettingsViewModel
import com.example.scam_warming_app.presentation.splash.SplashScreen
import com.example.scam_warming_app.presentation.splash.SplashViewModel
import com.example.scam_warming_app.presentation.trusted.TrustedNumbersScreen
import com.example.scam_warming_app.presentation.trusted.TrustedNumbersViewModel

import com.example.scam_warming_app.service.CallDetectionService
import com.example.scam_warming_app.ui.theme.ScamwarmingappTheme
import com.example.scam_warming_app.utils.PermissionManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    data object Home : Screen("home", "Trang chủ", Icons.Rounded.Shield)
    data object Blacklist : Screen("blacklist", "Dữ liệu", Icons.Rounded.Storage)
    data object History : Screen("history", "Nhật ký", Icons.Rounded.History)
    data object Settings : Screen("settings", "Cài đặt", Icons.Rounded.Settings)
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var apiService: ApiService

    private lateinit var permissionManager: PermissionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        permissionManager = PermissionManager(this)

        enableEdgeToEdge()
        setContent {
            ScamwarmingappTheme {
                // Ban đầu luôn vào Splash
                var currentViewState by remember { mutableStateOf("splash") }

                when (currentViewState) {
                    "splash" -> SplashScreen(
                        onNext = { destination -> 
                            // Nhận điểm đến tiếp theo từ Splash (onboarding/permission/main)
                            currentViewState = destination 
                        },
                        viewModel = hiltViewModel<SplashViewModel>()
                    )
                    "onboarding" -> OnboardingScreen(
                        onFinished = { currentViewState = "permission" },
                        viewModel = hiltViewModel<OnboardingViewModel>()
                    )
                    "permission" -> PermissionScreen(
                        permissionManager = permissionManager,
                        onAllGranted = { 
                            handleRegistrationAndStart {
                                currentViewState = "main"
                            }
                        }
                    )
                    "main" -> MainAppContainer()
                }
            }
        }
    }

    private fun handleRegistrationAndStart(onComplete: () -> Unit) {
        val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID) ?: "unknown"
        
        lifecycleScope.launch {
            try {
                val response = apiService.register(
                    RegisterRequest(
                        phone_number = "USER_PHONE", 
                        device_id = deviceId,
                        device_model = Build.MODEL,
                        os_version = "Android ${Build.VERSION.RELEASE}"
                    )
                )
                
                if (response.success && response.access_token != null) {
                    sessionManager.saveAuthToken(response.access_token)
                }
            } catch (e: Exception) {
                Log.e("JWT", "Error: ${e.message}")
            } finally {
                startServices(this@MainActivity)
                onComplete()
            }
        }
    }
}

@Composable
fun MainAppContainer() {
    val navController = rememberNavController()
    val items = listOf(Screen.Home, Screen.Blacklist, Screen.History, Screen.Settings)
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomBar = items.any { it.route == currentDestination?.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(tonalElevation = 8.dp) {
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = null) },
                            label = { Text(screen.label, fontSize = 10.sp) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, Screen.Home.route, Modifier.padding(innerPadding)) {
            composable(Screen.Home.route) { 
                HomeScreen(
                    onNavigateToReport = { navController.navigate("report") },
                    onNavigateToDetail = { id, type -> navController.navigate("detail/$type/$id") }
                ) 
            }
            composable(Screen.Blacklist.route) { 
                BlacklistScreen(viewModel = hiltViewModel<BlacklistViewModel>()) 
            }
            composable(Screen.History.route) { 
                HistoryScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToDetail = { id, type -> navController.navigate("detail/$type/$id") },
                    viewModel = hiltViewModel<HistoryViewModel>()
                )
            }
            composable(Screen.Settings.route) { 
                SettingsScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToTrusted = { navController.navigate("trusted") },
                    viewModel = hiltViewModel<SettingsViewModel>()
                ) 
            }
            composable("report") { 
                ReportScreen(
                    onBack = { navController.popBackStack() },
                    viewModel = hiltViewModel<ReportViewModel>()
                ) 
            }
            composable("trusted") { 
                TrustedNumbersScreen(
                    onBack = { navController.popBackStack() },
                    viewModel = hiltViewModel<TrustedNumbersViewModel>()
                ) 
            }
            composable(
                route = "detail/{type}/{id}",
                arguments = listOf(
                    navArgument("type") { type = NavType.StringType },
                    navArgument("id") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val type = backStackEntry.arguments?.getString("type") ?: "sms"
                val id = backStackEntry.arguments?.getLong("id") ?: 0L
                DetailScreen(
                    id = id, 
                    type = type, 
                    onBack = { navController.popBackStack() },
                    viewModel = hiltViewModel<DetailViewModel>()
                )
            }
        }
    }
}

private fun startServices(context: Context) {
    val intent = Intent(context, CallDetectionService::class.java)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.startForegroundService(intent)
    } else {
        context.startService(intent)
    }
}
