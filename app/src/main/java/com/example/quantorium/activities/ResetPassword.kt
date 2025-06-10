package com.example.quantorium.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quantorium.data.SupabaseUser
import com.example.quantorium.databinding.ActivityResetPasswordBinding
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ResetPassword : AppCompatActivity() {

    private lateinit var binding: ActivityResetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.resetBtn.setOnClickListener {
            val email = binding.emailEditText.text.toString()

            if (email.isEmpty()) {
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    SupabaseUser.supabase.auth.resetPasswordForEmail(email = email)
                    val intent = Intent(this@ResetPassword, OtpVerification::class.java)
                    intent.putExtra("email", email)
                    finish()
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                } catch (e: RestException) {
                    // Получаем сообщение об ошибке из RestException
                    val errorMessage = "Ошибка Supabase: ${e.message}"
                    Toast.makeText(this@ResetPassword, errorMessage, Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    // Получаем сообщение об ошибке из Exception
                    val errorMessage = "Неизвестная ошибка: ${e.message}"
                    Toast.makeText(this@ResetPassword, errorMessage, Toast.LENGTH_LONG).show()
                }

            }
        }
        }
    }
