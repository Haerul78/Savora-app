package com.savora.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.lifecycle.lifecycleScope
import com.savora.app.remote.supabase
import com.savora.app.ui.theme.SavoraTheme
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Test koneksi Supabase
        lifecycleScope.launch {
            try {
                val user = supabase.auth.currentUserOrNull()
                Log.d("Supabase", "Connected! Current user: $user")
            } catch (e: Exception) {
                Log.e("Supabase", "Connection failed: ${e.message}")
            }
        }

        setContent {
            SavoraTheme {
                Text("Savora — Supabase OK")
            }
        }
    }
}