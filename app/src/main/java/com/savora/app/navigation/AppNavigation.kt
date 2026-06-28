package com.savora.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import com.savora.app.ui.screen.auth.LoginScreen
import com.savora.app.ui.screen.auth.RegisterScreen
import com.savora.app.ui.screen.cart.CartScreen
import com.savora.app.ui.screen.checkout.PaymentMethodScreen
import com.savora.app.ui.screen.checkout.PaymentSuccessScreen
import com.savora.app.ui.screen.home.HomeScreen
import com.savora.app.ui.screen.profile.AddressScreen
import com.savora.app.ui.screen.profile.PaymentDetailScreen
import com.savora.app.ui.screen.profile.PaymentHistoryScreen
import com.savora.app.ui.screen.profile.ProfileScreen
import com.savora.app.ui.screen.profile.SavedRecipesScreen
import com.savora.app.ui.screen.profile.SettingsScreen
import com.savora.app.ui.screen.recipe.RecipesScreen
import com.savora.app.ui.screen.recipe.RecipeDetailScreen
import com.savora.app.ui.screen.splash.SplashScreen
import com.savora.app.ui.screen.store.StoreScreen

object Routes {
    const val SPLASH = "splash"
    const val AUTH = "auth"
    const val REGISTER = "auth/register"
    const val HOME = "home"
    const val RECIPE_DETAIL = "recipe/{recipeId}"
    const val STORE = "store"
    const val CART = "cart"
    const val PAYMENT_METHOD = "checkout/payment"
    const val PAYMENT_SUCCESS = "checkout/success/{orderId}/{paymentMethod}/{total}"

    fun paymentSuccess(orderId: String, paymentMethod: String, total: Long): String {
        val encodedMethod = URLEncoder.encode(paymentMethod, StandardCharsets.UTF_8.toString())
        return "checkout/success/$orderId/$encodedMethod/$total"
    }
    const val RECIPES = "recipes"
    const val PROFILE = "profile"
    const val PAYMENT_HISTORY = "profile/payment-history"
    const val PAYMENT_DETAIL = "profile/payment/{paymentId}"
    const val SAVED_RECIPES = "profile/saved-recipes"
    const val ADDRESSES = "profile/addresses"
    const val SETTINGS = "profile/settings"

    fun paymentDetail(paymentId: String) = "profile/payment/$paymentId"

    fun recipeDetail(recipeId: String) = "recipe/$recipeId"
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        // Splash Screen
        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Routes.AUTH) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // Login / Register
        composable(Routes.AUTH) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.AUTH) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        // Daftar Akun
        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.AUTH) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Home
        composable(Routes.HOME) {
            HomeScreen(
                onRecipeClick = { recipeId ->
                    navController.navigate(Routes.recipeDetail(recipeId))
                },
                onCartClick = { navController.navigate(Routes.CART) },
                onNavigateToStore = { navController.navigate(Routes.STORE) },
                onNavigateToRecipes = { navController.navigate(Routes.RECIPES) },
                navController = navController
            )
        }

        // Recipe Detail
        composable(
            route = Routes.RECIPE_DETAIL,
            arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId") ?: ""
            RecipeDetailScreen(
                recipeId = recipeId,
                onNavigateBack = { navController.popBackStack() },
                onBuyIngredients = { navController.navigate(Routes.CART) }
            )
        }

        // Recipes list
        composable(Routes.RECIPES) {
            RecipesScreen(
                onRecipeClick = { recipeId ->
                    navController.navigate(Routes.recipeDetail(recipeId))
                },
                navController = navController
            )
        }

        // Toko / Store
        composable(Routes.STORE) {
            StoreScreen(
                onCartClick = { navController.navigate(Routes.CART) },
                navController = navController
            )
        }

        // Cart
        composable(Routes.CART) {
            CartScreen(
                onNavigateBack = { navController.popBackStack() },
                onCheckout = { navController.navigate(Routes.PAYMENT_METHOD) }
            )
        }

        // Payment Method
        composable(Routes.PAYMENT_METHOD) {
            PaymentMethodScreen(
                onNavigateBack = { navController.popBackStack() },
                onPaymentSuccess = { orderId, paymentMethod, total ->
                    navController.navigate(Routes.paymentSuccess(orderId, paymentMethod, total)) {
                        popUpTo(Routes.HOME)
                    }
                }
            )
        }

        // Payment Success
        composable(
            route = Routes.PAYMENT_SUCCESS,
            arguments = listOf(
                navArgument("orderId") { type = NavType.StringType },
                navArgument("paymentMethod") { type = NavType.StringType },
                navArgument("total") { type = NavType.LongType },
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            val paymentMethod = URLDecoder.decode(
                backStackEntry.arguments?.getString("paymentMethod") ?: "",
                StandardCharsets.UTF_8.toString()
            )
            val total = backStackEntry.arguments?.getLong("total") ?: 0L
            PaymentSuccessScreen(
                orderId = orderId,
                paymentMethod = paymentMethod,
                total = total,
                onNavigateHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }

        // Profile
        composable(Routes.PROFILE) {
            ProfileScreen(
                onNavigateToPaymentHistory = {
                    navController.navigate(Routes.PAYMENT_HISTORY)
                },
                onNavigateToSavedRecipes = {
                    navController.navigate(Routes.SAVED_RECIPES)
                },
                onNavigateToAddresses = {
                    navController.navigate(Routes.ADDRESSES)
                },
                onNavigateToSettings = {
                    navController.navigate(Routes.SETTINGS)
                },
                onLogout = {
                    navController.navigate(Routes.AUTH) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                navController = navController
            )
        }

        // Addresses
        composable(Routes.ADDRESSES) {
            AddressScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Payment History
        composable(Routes.PAYMENT_HISTORY) {
            PaymentHistoryScreen(
                onNavigateBack = { navController.popBackStack() },
                onPaymentClick = { paymentId ->
                    navController.navigate(Routes.paymentDetail(paymentId))
                },
            )
        }

        // Payment Detail (kwitansi)
        composable(
            route = Routes.PAYMENT_DETAIL,
            arguments = listOf(navArgument("paymentId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val paymentId = backStackEntry.arguments?.getString("paymentId") ?: ""
            PaymentDetailScreen(
                paymentId = paymentId,
                onNavigateBack = { navController.popBackStack() },
            )
        }

        // Saved Recipes
        composable(Routes.SAVED_RECIPES) {
            SavedRecipesScreen(
                onNavigateBack = { navController.popBackStack() },
                onRecipeClick = { recipeId ->
                    navController.navigate(Routes.recipeDetail(recipeId))
                }
            )
        }

        // Settings
        composable(Routes.SETTINGS) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onAccountDeleted = {
                    navController.navigate(Routes.AUTH) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
