package com.example.quantorium.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.quantorium.R
import com.example.quantorium.data.AuthManager
import com.example.quantorium.data.SupabaseUser
import com.example.quantorium.databinding.FragmentProfileStudentBinding
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

class FragmentProfileStudent : Fragment() {

    private lateinit var binding: FragmentProfileStudentBinding

    private lateinit var authManager: AuthManager

    private val client: SupabaseClient by lazy { SupabaseUser.supabase }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileStudentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authManager = AuthManager(requireContext())

        lifecycleScope.launch {
            if (binding.name.toString().isEmpty() || binding.surname.toString().isEmpty() || binding.patronymic.toString().isEmpty() || binding.dateBirth.toString().isEmpty() ||
                binding.classNumber.toString().isEmpty() || binding.telNumber.toString().isEmpty() || binding.userEmail.toString().isEmpty() || binding.school.toString().isEmpty()) {

                binding.warning.text = "Пожалуйста, заполните все поля."
                binding.warning.isVisible = true
                return@launch
            } else {
                binding.warning.isVisible = false // Скрываем TextView, если все поля заполнены
            }

            val user = client.auth.currentUserOrNull()
            if (user?.userMetadata != null) {
                binding.name.text = user.userMetadata!!["name"]?.toString()?.trim('"') ?: ""
                binding.surname.text = user.userMetadata!!["surname"]?.toString()?.trim('"') ?: ""
                binding.patronymic.text = user.userMetadata!!["patr"]?.toString()?.trim('"') ?: ""

                binding.dateBirth.text = user.userMetadata!!["age"]?.toString()?.trim('"') ?: ""
                binding.userEmail.text = user.email ?: ""
                val encodedSchool = user.userMetadata!!["school"]?.toString()?.trim('"') ?: ""
                val decodedSchool = URLDecoder.decode(encodedSchool, StandardCharsets.UTF_8.toString())
                binding.school.setText(decodedSchool)

                binding.telNumber.text = user.userMetadata!!["phone_number"]?.toString()?.trim('"')
                    ?: ""
                binding.classNumber.text = user.userMetadata!!["class_number"]?.toString()?.trim('"')
                    ?: ""
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

        binding.btnEdit.setOnClickListener {
            val destinationFragment = EditProfileMetadata()

            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(
                R.id.fragment,
                destinationFragment
            )
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }
}