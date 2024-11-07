import com.google.firebase.Timestamp

data class Post(
    val username: String? = null,
    val date: Timestamp? = null,
    val post: String? = null,
    val mediaURL: String? = null
)
