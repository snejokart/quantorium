package com.example.quantorium.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

class FragmentProfileStudent : Fragment() {

    private lateinit var binding: FragmentProfileStudentBinding

    private lateinit var authManager: AuthManager

    //  Use this ONLY if SupabaseUser properly initializes and holds a client
    private val client: SupabaseClient by lazy { SupabaseUser.supabase } //  Initialization

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileStudentBinding.inflate(inflater, container, false)
        // Do NOT initialize client here.  It's handled with `by lazy` above
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authManager = AuthManager(requireContext())

        lifecycleScope.launch {
            val user = client.auth.currentUserOrNull()
            if (user?.userMetadata != null) {
                binding.name.text = user.userMetadata!!["name"]?.toString()
                binding.surname.text = user.userMetadata!!["surname"]?.toString()
                binding.patronymic.text = user.userMetadata!!["patr"]?.toString()

                binding.dateBirth.text = user.userMetadata!!["age"]?.toString() ?: "Возраст: пусто"
                binding.userEmail.text = user.email ?: ""
                binding.school.text = user.userMetadata!!["school"]?.toString() ?: "Школа: пусто"
                binding.telNumber.text =
                    user.userMetadata!!["phone_number"]?.toString() ?: "Номер телефона: пусто"
                binding.classNumber.text =
                    user.userMetadata!!["class_number"]?.toString() ?: "Класс: пусто"
                binding.course.text = user.userMetadata!!["course"]?.toString() ?: "Курс: пусто"

//                val avatarUrl = getAvatarUrl(user.id, "avatars")  //  Call without client parameter
//
//                avatarUrl?.let {
//                    withContext(Dispatchers.Main) {
//                        Glide.with(requireContext())
//                            .load(it)
//                            .circleCrop()
//                            .into(binding.icon)
//                    }
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

            // Замените текущий фрагмент на целевой
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(
                R.id.fragment,
                destinationFragment
            ) // Замените R.id.fragment_container на ID контейнера фрагментов в вашем layout
            fragmentTransaction.addToBackStack(null) // Добавьте фрагмент в бэкстек, чтобы пользователь мог вернуться
            fragmentTransaction.commit()
        }
    }
}