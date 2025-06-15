package com.example.quantorium.activities

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quantorium.R
import com.example.quantorium.data.SupabaseUser
import com.example.quantorium.databinding.ActivityOtpVerificationBinding
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.gotrue.OtpType
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OtpVerification : AppCompatActivity() {

    private lateinit var binding: ActivityOtpVerificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val email = intent.getStringExtra("email") ?: ""

        val timer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = (millisUntilFinished / 1000).toInt()
                val minutes = seconds / 60
                val secondsRemaining = seconds % 60
                binding.resetAgain.text = "Отправить код повторно можно через ${
                    String.format(
                        "%d:%02d",
                        minutes,
                        secondsRemaining
                    )
                }"
            }

            override fun onFinish() {
                binding.resetAgain.text = "Отправить код повторно"

                binding.resetAgain.setOnClickListener {

                    CoroutineScope(Dispatchers.Main).launch {
                        try {
                            SupabaseUser.supabase.auth.resetPasswordForEmail(email = email)
                            val intent = Intent(this@OtpVerification, OtpVerification::class.java)
                            intent.putExtra("email", email)
                            finish()
                            startActivity(intent)
                            overridePendingTransition(0, 0)
                        } catch (e: RestException) {
                            // Получаем сообщение об ошибке из RestException
                            val errorMessage = "Ошибка Supabase: ${e.message}"
                            Toast.makeText(this@OtpVerification, errorMessage, Toast.LENGTH_LONG).show()
                        } catch (e: Exception) {
                            // Получаем сообщение об ошибке из Exception
                            val errorMessage = "Неизвестная ошибка: ${e.message}"
                            Toast.makeText(this@OtpVerification, errorMessage, Toast.LENGTH_LONG).show()
                        }

                    }
                }
            }
        }.start()


        val editTextList = listOf(
            findViewById(R.id.n1),
            findViewById(R.id.n2),
            findViewById(R.id.n3),
            findViewById(R.id.n4),
            findViewById(R.id.n5),
            findViewById<EditText>(R.id.n6)
        )

        var combinedNumber = "";

        for (i in 0 until editTextList.size) {
            val editText = editTextList[i]

            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val digit = s?.toString() ?: "" // Получаем введенную цифру
                    combinedNumber += digit // Добавляем цифру к общей строке

                    if (i < editTextList.size - 1 && s?.length == 1) {
                        editTextList[i + 1].requestFocus()
                    } else if (i == editTextList.size - 1 && s?.length == 1) {
                        // Скрываем клавиатуру на последнем поле
                        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(editText.windowToken, 0)
                    }
                }
            })
        }

        binding.otpBtn.setOnClickListener {

            if (combinedNumber.isEmpty()) {
                return@setOnClickListener
            }

            if (combinedNumber.length != 6) {
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    SupabaseUser.supabase.auth.verifyEmailOtp(
                        type = OtpType.Email.EMAIL,
                        email = email,
                        token = combinedNumber
                    )
                    startActivity(Intent(this@OtpVerification, NewPassword::class.java))
                } catch (e: RestException) {
                    Toast.makeText(this@OtpVerification, "Ошибка", Toast.LENGTH_LONG).show()

                } catch (e: java.lang.Exception) {
                    Toast.makeText(this@OtpVerification, "Ошибка", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}