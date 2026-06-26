package com.savora.app.data.repository

import com.savora.app.data.model.User
import com.savora.app.remote.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from

class AuthRepository {

    suspend fun signIn(email: String, password: String): Result<String> {
        return try {
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            val authUser = supabase.auth.currentUserOrNull()
                ?: return Result.failure(Exception("Gagal mendapatkan session"))
            val existing = runCatching {
                supabase.from("users").select { filter { eq("id", authUser.id) } }.decodeList<User>()
            }.getOrNull()
            if (existing.isNullOrEmpty()) {
                runCatching {
                    supabase.from("users").insert(mapOf("id" to authUser.id, "full_name" to email.substringBefore("@"), "email" to email))
                }
            }
            Result.success(authUser.id)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun signUp(email: String, password: String, fullName: String, phone: String): Result<String> {
        return try {
            supabase.auth.signUpWith(Email) { this.email = email; this.password = password }
            supabase.auth.signInWith(Email) { this.email = email; this.password = password }
            val userId = supabase.auth.currentUserOrNull()?.id
                ?: return Result.failure(Exception("Gagal mendapatkan user ID setelah registrasi"))
            try {
                supabase.from("users").upsert(mapOf("id" to userId, "full_name" to fullName, "email" to email, "phone" to phone))
            } catch (e: Exception) {
                supabase.auth.signOut()
                return Result.failure(Exception("Gagal menyimpan profil: ${e.message}"))
            }
            Result.success(userId)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun ensureProfile(userId: String, email: String?) {
        runCatching {
            val existing = supabase.from("users").select { filter { eq("id", userId) } }.decodeList<User>()
            if (existing.isEmpty()) {
                supabase.from("users").insert(mapOf("id" to userId, "full_name" to (email?.substringBefore("@") ?: "Pengguna"), "email" to (email ?: "")))
            }
        }
    }

    suspend fun signInWithGoogle(): Result<Unit> {
        return try { supabase.auth.signInWith(Google); Result.success(Unit) }
        catch (e: Exception) { Result.failure(e) }
    }

    suspend fun signOut(): Result<Unit> {
        return try { supabase.auth.signOut(); Result.success(Unit) }
        catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getCurrentSession(): String? {
        return try { supabase.auth.currentUserOrNull()?.id } catch (e: Exception) { null }
    }

    suspend fun isLoggedIn(): Boolean {
        return try { supabase.auth.currentSessionOrNull() != null } catch (e: Exception) { false }
    }
}