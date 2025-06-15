package com.example.quantorium.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quantorium.data.SupabaseUser
import com.example.quantorium.databinding.ActivityNewPasswordBinding
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewPassword : AppCompatActivity() {

    private lateinit var binding : ActivityNewPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.newPassBtn.setOnClickListener {

            if (binding.password1.text.toString().isEmpty() || binding.password2.text.toString().isEmpty()){
                return@setOnClickListener
            }

            val password1 = binding.password1.text.toString()

            // Регулярное выражение для проверки:
            // - Минимум 6 символов
            // - Минимум 1 строчная буква
            // - Минимум 1 прописная буква
            // - Минимум 1 цифра
            val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$".toRegex()

            if (!passwordRegex.matches(password1)) {
                Toast.makeText(this@NewPassword, "Пароль должен содержать минимум 6 символов, включая строчные, прописные буквы и цифры.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (password1 == binding.password2.text.toString()) {
                val newPassword = password1

                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        SupabaseUser.supabase.auth.updateUser {
                            password = newPassword
                        }
                        SupabaseUser.supabase.auth.signOut()
                        Toast.makeText(this@NewPassword,"Пароль успешно сменён", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this@NewPassword, Auth::class.java))
                        finishAffinity()
                    } catch (e: RestException) {
                        Toast.makeText(this@NewPassword,"Ошибка", Toast.LENGTH_LONG).show()

                    } catch (e: Exception) {
                        Toast.makeText(this@NewPassword,"Ошибка", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}