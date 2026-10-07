package com.paparazziapps.pretamistapp.libraries.database.di

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest


interface LoanDatabase {
    fun initDatabaseClient(
        supabaseUrl:String,
        supabaseKey:String,
    ): SupabaseClient {
        val supabase = createSupabaseClient(
            supabaseUrl = supabaseUrl,
            supabaseKey = supabaseKey
        ){
            install(Postgrest)
        }
        return supabase
    }
}

internal class LoanDatabaseImpl(
    private val supabaseUrl: String,
    private val supabaseKey: String) : LoanDatabase {

    init {
        this.initDatabaseClient(supabaseUrl, supabaseKey)
    }
}