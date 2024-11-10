import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

data class Post(
    val username: String? = null,
    val date: Timestamp? = null,
    val post: String? = null,
    val mediaURL: String? = null
) {
    // Función para convertir el Timestamp en un formato legible
    fun getFormattedDate(): String? {
        return date?.toDate()?.let {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            dateFormat.format(it)
        }
    }
}
