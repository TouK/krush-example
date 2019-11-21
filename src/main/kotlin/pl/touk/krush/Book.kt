package pl.touk.krush

import java.time.LocalDate
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class Book(
    @Id @GeneratedValue
    val id: Long? = null,

    val isbn: String,
    val author: String,
    val title: String,
    val publishDate: LocalDate
)
