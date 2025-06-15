package com.example.quantorium.activities

import NoAccountDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.quantorium.data.AuthManager
import com.example.quantorium.data.SupabaseUser
import com.example.quantorium.data.request
import com.example.quantorium.databinding.ActivityAuthBinding
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email

class Auth : AppCompatActivity() {

    private lateinit var binding : ActivityAuthBinding
    private lateinit var authManager: AuthManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authManager = AuthManager(this)

        binding.resetPass.setOnClickListener {
            val int = Intent(this, ResetPassword::class.java)
            startActivity(int)
            overridePendingTransition(0,0)
        }

        binding.noAccBtn.setOnClickListener {
            val dialog = NoAccountDialog()
            dialog.show(supportFragmentManager, "NoAccountDialog")
        }


        binding.auth.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isEmpty()){
                return@setOnClickListener;
            }

            if (password.isEmpty()){
                return@setOnClickListener
            }

            request(this){
                SupabaseUser.supabase.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }
                val currentUser = SupabaseUser.supabase.auth.currentUserOrNull()
                val token = currentUser?.id ?: "" // Use user ID as token
                authManager.saveToken(token)
                val int = Intent(this@Auth, ActivityFragment::class.java)
                startActivity(int)
                overridePendingTransition(0,0)
            }
        }
    }
}