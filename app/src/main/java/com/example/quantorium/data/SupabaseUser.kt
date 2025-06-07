package com.example.quantorium.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.serializer.KotlinXSerializer
import io.github.jan.supabase.storage.Storage
import kotlinx.serialization.json.Json


object SupabaseUser {
    val supabase = createSupabaseClient(
        "https://suuedpqhscdblsyltaph.supabase.co",
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InN1dWVkcHFoc2NkYmxzeWx0YXBoIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDcwMjYyNjIsImV4cCI6MjA2MjYwMjI2Mn0.wFkcjE7vbowTr5zp9JTSiBI7v_dHW-2IFbxzZHLMEPg"
    ) {
        defaultSerializer = KotlinXSerializer(Json{ignoreUnknownKeys=true})
        install(Auth)
        install(Postgrest)
        install(Storage)
        install(Realtime)
    }
}