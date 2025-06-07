package com.example.quantorium.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.quantorium.databinding.ActivityAuthBinding
import com.example.quantorium.data.SupabaseUser
import com.example.quantorium.data.request
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email

class Auth : AppCompatActivity() {

    private lateinit var binding : ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
//                Toast.makeText(
//                    this@Auth,
//                    Supabase.supabase.auth.currentUserOrNull()?.id ?: "",
//                    Toast.LENGTH_LONG
//                ).show()
                val int = Intent(this@Auth, ActivityFragment::class.java)
                startActivity(int)
                overridePendingTransition(0,0)
            }
        }
    }
}