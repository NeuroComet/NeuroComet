@file:Suppress("unused")

package com.kyilmaz.neurocomet

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

/**
 * Safe Supabase REST helpers that bypass kotlin-reflect's typeOf<T>() crash on Android.
 *
 * Error: "KotlinReflectionInternalError: Unresolved class: interface java.util.List"
 *
 * The Supabase SDK v3's reified functions (insert, select/decodeList, update) call
 * serializer<T>() which invokes typeOf<T>() at runtime. On Android/ART, kotlin-reflect
 * cannot resolve Java platform interfaces (java.util.List, java.util.Map) in the type
 * hierarchy, causing a crash.
 *
 * These helpers use a standalone Ktor HttpClient to call the PostgREST REST API directly,
 * encoding/decoding JSON manually and completely bypassing all reified typeOf() paths.
 */

private const val TAG = "SupabaseREST"

/** Shared JSON parser (lenient to handle Supabase responses). */
private val supabaseJson = Json { ignoreUnknownKeys = true; isLenient = true }

/** Standalone HTTP client without the Supabase SDK's URL rewriting. */
private val rawHttpClient by lazy { HttpClient(OkHttp) }

/** Cached raw Supabase URL (decrypted from BuildConfig). */
private val rawSupabaseUrl: String by lazy {
    SecurityUtils.decrypt(BuildConfig.SUPABASE_URL)
        .removeSuffix("/")
        .removeSuffix("/rest/v1")   // guard against double-path
        .removeSuffix("/")
}

/** Cached raw Supabase anon key (decrypted from BuildConfig). */
private val rawSupabaseKey: String by lazy {
    SecurityUtils.decrypt(BuildConfig.SUPABASE_KEY)
}

/**
 * Returns the current authenticated user's JWT access token if available,
 * otherwise falls back to the anon key.
 *
 * RLS policies that check `auth.uid()` require a real user JWT — sending
 * only the anon key causes those policies to evaluate uid as NULL and
 * reject (or silently skip) the operation, leading to 500 / empty-result
 * errors.
 */
private fun currentBearerToken(): String {
    return try {
        AppSupabaseClient.client?.auth?.currentSessionOrNull()?.accessToken
            ?.takeIf { it.isNotBlank() }
            ?: rawSupabaseKey
    } catch (_: Exception) {
        rawSupabaseKey
    }
}

// =============================================================================
// INSERT
// =============================================================================

/**
 * Insert a single [JsonObject] row into a Supabase table.
 */
suspend fun SupabaseClient.safeInsert(table: String, value: JsonObject) {
    val url = "$rawSupabaseUrl/rest/v1/$table"
    val token = currentBearerToken()
    Log.d(TAG, "INSERT → $table")
    val response = rawHttpClient.post(url) {
        contentType(ContentType.Application.Json)
        header("apikey", rawSupabaseKey)
        header("Authorization", "Bearer $token")
        header("Prefer", "return=minimal")
        setBody(value.toString())
    }
    if (response.status.value !in 200..299) {
        val body = response.bodyAsText()
        Log.e(TAG, "INSERT $table failed (${response.status}): $body")
        throw Exception("Insert failed (${response.status}): $body")
    }
}

/**
 * Insert multiple [JsonObject] rows into a Supabase table.
 */
suspend fun SupabaseClient.safeInsertList(table: String, values: List<JsonObject>) {
    val body = JsonArray(values)
    val url = "$rawSupabaseUrl/rest/v1/$table"
    val token = currentBearerToken()
    Log.d(TAG, "BULK INSERT → $table (${values.size} rows)")
    val response = rawHttpClient.post(url) {
        contentType(ContentType.Application.Json)
        header("apikey", rawSupabaseKey)
        header("Authorization", "Bearer $token")
        header("Prefer", "return=minimal")
        setBody(body.toString())
    }
    if (response.status.value !in 200..299) {
        val respBody = response.bodyAsText()
        Log.e(TAG, "BULK INSERT $table failed (${response.status}): $respBody")
        throw Exception("Bulk insert failed (${response.status}): $respBody")
    }
}

// =============================================================================
// SELECT
// =============================================================================

/**
 * Query rows from a Supabase table, returning raw [JsonArray].
 *
 * @param table   Table name
 * @param columns Comma-separated column list (e.g. "id,likes") or "*"
 * @param filters PostgREST filter query params (e.g. "post_id=eq.5&user_id=eq.abc")
 */
suspend fun safeSelect(
    table: String,
    columns: String = "*",
    filters: String = ""
): JsonArray {
    val url = buildString {
        append("$rawSupabaseUrl/rest/v1/$table?select=$columns")
        if (filters.isNotEmpty()) append("&$filters")
    }
    val token = currentBearerToken()
    Log.d(TAG, "SELECT → $table (columns=$columns)")
    val response = rawHttpClient.get(url) {
        header("apikey", rawSupabaseKey)
        header("Authorization", "Bearer $token")
        header("Accept", "application/json")
    }
    if (response.status.value !in 200..299) {
        val body = response.bodyAsText()
        Log.e(TAG, "SELECT $table failed (${response.status}): $body")
        throw Exception("Select failed (${response.status}): $body")
    }
    val text = response.bodyAsText()
    return supabaseJson.decodeFromString<JsonArray>(text)
}

// =============================================================================
// UPDATE (PATCH)
// =============================================================================

/**
 * Update rows in a Supabase table.
 *
 * @param table   Table name
 * @param body    JSON object with the fields to set
 * @param filters PostgREST filter query params (e.g. "id=eq.5")
 */
suspend fun safeUpdate(
    table: String,
    body: JsonObject,
    filters: String
) {
    val url = "$rawSupabaseUrl/rest/v1/$table?$filters"
    val token = currentBearerToken()
    Log.d(TAG, "UPDATE → $table ($filters)")
    val response = rawHttpClient.patch(url) {
        contentType(ContentType.Application.Json)
        header("apikey", rawSupabaseKey)
        header("Authorization", "Bearer $token")
        header("Prefer", "return=minimal")
        setBody(body.toString())
    }
    if (response.status.value !in 200..299) {
        val respBody = response.bodyAsText()
        Log.e(TAG, "UPDATE $table failed (${response.status}): $respBody")
        throw Exception("Update failed (${response.status}): $respBody")
    }
}

// =============================================================================
// DELETE
// =============================================================================

/**
 * Delete rows from a Supabase table.
 *
 * @param table   Table name
 * @param filters PostgREST filter query params (e.g. "post_id=eq.5&user_id=eq.abc")
 */
suspend fun safeDelete(
    table: String,
    filters: String
) {
    val url = "$rawSupabaseUrl/rest/v1/$table?$filters"
    val token = currentBearerToken()
    Log.d(TAG, "DELETE → $table ($filters)")
    val response = rawHttpClient.delete(url) {
        header("apikey", rawSupabaseKey)
        header("Authorization", "Bearer $token")
    }
    if (response.status.value !in 200..299) {
        val body = response.bodyAsText()
        Log.e(TAG, "DELETE $table failed (${response.status}): $body")
        throw Exception("Delete failed (${response.status}): $body")
    }
}
