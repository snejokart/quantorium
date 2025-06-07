package com.example.quantorium.data

import android.app.AlertDialog
import android.content.Context
import io.github.jan.supabase.exceptions.RestException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun showError(context: Context, error: String){
    AlertDialog.Builder(context)
        .setTitle("Ошибка")
        .setMessage(error)
        .setNegativeButton("OK", null)
        .show()
}


fun request(
    request: suspend () -> Unit,
    onError: (String) -> Unit
){
    CoroutineScope(Dispatchers.Main).launch {
        try {
            request()
        }catch (e: RestException){
            onError(e.error)
        }catch (e: Exception){
            onError(e.message ?: e.toString())
        }
    }
}

fun request(
    context: Context,
    request: suspend () -> Unit
){
    CoroutineScope(Dispatchers.Main).launch {
        try {
            request()
        }catch (e: RestException){
            showError(context, e.error)
        }catch (e: Exception){
            showError(context, e.message ?: e.toString())
        }
    }
}