package com.savora.app.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val RECIPES = "recipes"
    const val RECIPE_DETAIL = "recipe_detail/{recipeId}"
    const val STORE = "store"
    const val CART = "cart"
    const val PAYMENT_METHOD = "payment_method/{orderId}"
    const val PAYMENT_SUCCESS = "payment_success/{paymentId}"
    const val PROFILE = "profile"
    const val ADDRESS = "address"
    const val SAVED_RECIPES = "saved_recipes"
    const val PAYMENT_HISTORY = "payment_history"
    const val PAYMENT_DETAIL = "payment_detail/{paymentId}"

    fun recipeDetail(recipeId: String) = "recipe_detail/$recipeId"
    fun paymentMethod(orderId: String) = "payment_method/$orderId"
    fun paymentSuccess(paymentId: String) = "payment_success/$paymentId"
    fun paymentDetail(paymentId: String) = "payment_detail/$paymentId"
}

@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = Routes.SPLASH, modifier = modifier) {

        composable(Routes.SPLASH) {
            Text("TODO: SplashScreen") // diganti Commit 14
        }
        composable(Routes.LOGIN) {
            Text("TODO: LoginScreen") // diganti Commit 15
        }
        composable(Routes.REGISTER) {
            Text("TODO: RegisterScreen") // diganti Commit 16
        }
        composable(Routes.HOME) {
            Text("TODO: HomeScreen") // diganti Commit 21
        }
        composable(Routes.RECIPES) {
            Text("TODO: RecipesScreen") // diganti Commit 22
        }
        composable(
            route = Routes.RECIPE_DETAIL,
            arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
        ) { backStack ->
            val recipeId = backStack.arguments?.getString("recipeId") ?: return@composable
            Text("TODO: RecipeDetailScreen recipeId=$recipeId") // diganti Commit 23
        }
        composable(Routes.STORE) {
            Text("TODO: StoreScreen") // diganti Commit 29
        }
        composable(Routes.CART) {
            Text("TODO: CartScreen") // diganti Commit 30
        }
        composable(
            route = Routes.PAYMENT_METHOD,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStack ->
            val orderId = backStack.arguments?.getString("orderId") ?: return@composable
            Text("TODO: PaymentMethodScreen orderId=$orderId") // diganti Commit 36
        }
        composable(
            route = Routes.PAYMENT_SUCCESS,
            arguments = listOf(navArgument("paymentId") { type = NavType.StringType })
        ) { backStack ->
            val paymentId = backStack.arguments?.getString("paymentId") ?: return@composable
            Text("TODO: PaymentSuccessScreen paymentId=$paymentId") // diganti Commit 37
        }
        composable(Routes.PROFILE) {
            Text("TODO: ProfileScreen") // diganti Commit 41
        }
        composable(Routes.ADDRESS) {
            Text("TODO: AddressScreen") // diganti Commit 42
        }
        composable(Routes.SAVED_RECIPES) {
            Text("TODO: SavedRecipesScreen") // diganti Commit 24
        }
        composable(Routes.PAYMENT_HISTORY) {
            Text("TODO: PaymentHistoryScreen") // diganti Commit 43
        }
        composable(
            route = Routes.PAYMENT_DETAIL,
            arguments = listOf(navArgument("paymentId") { type = NavType.StringType })
        ) { backStack ->
            val paymentId = backStack.arguments?.getString("paymentId") ?: return@composable
            Text("TODO: PaymentDetailScreen paymentId=$paymentId") // diganti Commit 43
        }
    }
}