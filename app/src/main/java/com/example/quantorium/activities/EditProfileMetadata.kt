package com.example.quantorium.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.quantorium.R
import com.example.quantorium.data.AuthManager
import com.example.quantorium.data.SupabaseUser
import com.example.quantorium.databinding.FragmentEditProfileMetadataBinding
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class EditProfileMetadata : Fragment() {

    private var _binding: FragmentEditProfileMetadataBinding? = null
    private val binding get() = _binding!!

    private lateinit var authManager: AuthManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileMetadataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authManager = AuthManager(requireContext())

        if (binding.name.toString().isEmpty() || binding.surname.toString().isEmpty() || binding.patr.toString().isEmpty() || binding.age.toString().isEmpty() ||
            binding.classNumber.toString().isEmpty() || binding.phoneNumber.toString().isEmpty() || binding.email.toString().isEmpty() || binding.school.toString().isEmpty()) {

            binding.warning.text = "Пожалуйста, заполните все поля."
            binding.warning.isVisible = true // Показываем TextView с предупреждением
            return // Прекращаем выполнение функции
        } else {
            binding.warning.isVisible = false // Скрываем TextView, если все поля заполнены
        }

        CoroutineScope(Dispatchers.Main).launch {
            val user = SupabaseUser.supabase.auth.currentUserOrNull()
            if (user?.userMetadata != null) {
                binding.name.setText(user.userMetadata!!["name"]?.toString()?.trim('"') ?: "")
                binding.surname.setText(user.userMetadata!!["surname"]?.toString()?.trim('"') ?: "")
                binding.patr.setText(user.userMetadata!!["patr"]?.toString()?.trim('"') ?: "")
                binding.age.setText(user.userMetadata!!["age"]?.toString()?.trim('"') ?: "")
                binding.email.setText(user.email ?: "")
//                binding.school.setText(user.userMetadata!!["school"]?.toString()?.trim('"') ?: "")
                val encodedSchool = user.userMetadata!!["school"]?.toString()?.trim('"') ?: ""
                val decodedSchool = URLDecoder.decode(encodedSchool, StandardCharsets.UTF_8.toString())
                binding.school.setText(decodedSchool)
                binding.phoneNumber.setText(user.userMetadata!!["phone_number"]?.toString()?.trim('"') ?: "")
                binding.classNumber.setText(user.userMetadata!!["class_number"]?.toString()?.trim('"') ?: "")
                // binding.course.setText(user.userMetadata!!["course"]?.toString()?.trim('"') ?: "")
            }
        }


    binding.btnSave.setOnClickListener {
        val name = binding.name.text.toString()
        val surname = binding.surname.text.toString()
        val patr = binding.patr.text.toString()
        val age = binding.age.text.toString()
        val numClass = binding.classNumber.text.toString()
        val school = binding.school.text.toString()
        val email = binding.email.text.toString()
        val phone = binding.phoneNumber.text.toString()


        if (surname.isEmpty() || age.isEmpty() || name.isEmpty() || patr.isEmpty() || numClass.isEmpty() || email.isEmpty()) {
            Toast.makeText(requireContext(), "Заполните все поля!", Toast.LENGTH_SHORT).show()
            return@setOnClickListener
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val metadata = mutableMapOf<String, String>() // Изменяем тип на mutableMapOf<String, String>
                metadata["name"] = name
                metadata["surname"] = surname
                metadata["patr"] = patr
                metadata["age"] = age
                metadata["class_number"] = numClass
                metadata["phone_number"] = phone
                metadata["school"] = URLEncoder.encode(school, StandardCharsets.UTF_8.toString()) // Экранируем пробелы

                // Преобразуем Map в JsonObject
                val jsonObject = buildJsonObject {
                    metadata.forEach { (key, value) ->
                        put(key, value)
                    }
                }
                Log.d("updaterrrrr", "Обновляем профиль с данными: name=$name, surname=$surname, patr=$patr, age=$age, numClass=$numClass, phone=$phone, school=$school, email=$email")

                SupabaseUser.supabase.auth.updateUser {
                    this.email = email
                    this.data = jsonObject
                }
                Toast.makeText(
                    requireContext(),
                    "Данные обновлены",
                    Toast.LENGTH_SHORT
                ).show()
                val destinationFragment = FragmentProfileStudent()

                // Замените текущий фрагмент на целевой
                val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.fragment, destinationFragment) // Замените R.id.fragment_container на ID контейнера фрагментов в вашем layout
                fragmentTransaction.addToBackStack(null) // Добавьте фрагмент в бэкстек, чтобы пользователь мог вернуться
                fragmentTransaction.commit()
            } catch (e: RestException) {
                Log.e("ErrorProfile", "Ошибка при обновлении профиля (RestException): ${e.message}", e)
                Toast.makeText(requireContext(), "Ошибка: ${e.message}", Toast.LENGTH_LONG).show()

            } catch (e: Exception) {
                Log.e("ErrorProfile1", "Неизвестная ошибка при обновлении профиля: ${e.message}", e)
                Toast.makeText(requireContext(), "Ошибка: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    binding.btnLogout.setOnClickListener {
        val int = Intent(requireContext(), Auth::class.java)

        CoroutineScope(Dispatchers.Main).launch {
            try {
                SupabaseUser.supabase.auth.signOut()
                authManager.clearToken()
                Toast.makeText(
                    requireContext(),
                    "Вы вышли из своего аккаунта",
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(int)
            } catch (e: RestException) {
                Toast.makeText(requireContext(), "Данные обновлены", Toast.LENGTH_SHORT).show()
            } catch (e: RestException) {
                Toast.makeText(requireContext(), "Ошибка", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Ошибка: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}


//    suspend fun uploadAvatar(
//        client: SupabaseClient, // Use client here
//        userId: String,
//        imageBytes: ByteArray,
//        bucketName: String = "avatars"
//    ): Boolean { // Return success status
//        return withContext(Dispatchers.IO) {
//            try {
//                val bucket = client.storage.from(bucketName) // Access storage through client
//                val filePath = "avatars/${userId}.png"
//
//                bucket.upload(
//                    path = filePath,
//                    data = imageBytes,
//                    upsert = true
//                ) {
//                    customMetadata = mapOf("owner" to userId)
//                }
//
//                println("Avatar uploaded successfully to: $filePath")
//                true // Indicate success
//
//            } catch (e: Exception) {
//                println("Error uploading avatar: ${e.message}")
//                e.printStackTrace()
//                false // Indicate failure
//            }
//        }
//    }

override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
}
}