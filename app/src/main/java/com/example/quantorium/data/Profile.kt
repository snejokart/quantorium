import kotlinx.serialization.Serializable

@Serializable
data class Profile_user(
    val user_id: String,
    val name: String?,
    val surname: String?,
    val patronymic: String?,
    val age: String,
    val phoneNumber: String?,
    val classNumber: String?,  // Changed 'class' to 'classValue'
    val school: String?,
    val avatar_url: String?,
//    val course1Name: String? // Added the course name
)