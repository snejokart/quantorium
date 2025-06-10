package com.example.quantorium.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import kotlinx.serialization.json.Json.Default.parseToJsonElement
import kotlinx.serialization.json.JsonObject

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

    CoroutineScope(Dispatchers.Main).launch {
        val user = SupabaseUser.supabase.auth.currentUserOrNull()
        if (user?.userMetadata != null){
            binding.name.setText(user.userMetadata!!["name"].toString())
            binding.surname.setText(user.userMetadata!!["surname"].toString())
            binding.patr.setText(user.userMetadata!!["patr"].toString())

            binding.age.setText(user.userMetadata!!["age"].toString())
            binding.email.setText(user.email ?: "")
            binding.surname.setText(user.userMetadata!!["surname"].toString())
            binding.school.setText(user.userMetadata!!["school"].toString())
            binding.phoneNumber.setText(user.userMetadata!!["phone_number"].toString())
            binding.classNumber.setText(user.userMetadata!!["class_number"].toString())
            binding.course.setText(user.userMetadata!!["course"].toString())
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
                val metadata =
                    SupabaseUser.supabase.auth.currentUserOrNull()?.userMetadata!!.toMap()
                        .toMutableMap()
                metadata["name"] = parseToJsonElement(name)
                metadata["surname"] = parseToJsonElement(surname)
                metadata["patr"] = parseToJsonElement(patr)
                metadata["age"] = parseToJsonElement(age)
                metadata["class_number"] = parseToJsonElement(numClass)
                metadata["phone_number"] = parseToJsonElement(phone)
                metadata["school"] = parseToJsonElement(school)

                SupabaseUser.supabase.auth.updateUser {
                    this.email = email
                    this.data = JsonObject(metadata)
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
                Toast.makeText(requireContext(), "Ошибка", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Ошибка", Toast.LENGTH_LONG).show()
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