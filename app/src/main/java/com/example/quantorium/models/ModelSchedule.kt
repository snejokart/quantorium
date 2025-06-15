package com.example.quantorium.models
import kotlinx.serialization.Serializable

@Serializable
data class ScheduleItem(
    val id: Int,
    val time: String,
    val courseName: String,  // Название предмета из таблицы courses
    val teacherFullName: String, // Полное имя учителя из таблицы teachers
    val dayOfWeek: String, // День недели
    val cabinet: String
)