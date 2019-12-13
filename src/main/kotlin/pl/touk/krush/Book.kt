package pl.touk.krush

import io.requery.*
import java.time.LocalDate

@Entity
@Table(name = "books")
interface Book : Persistable {
    @get:Key @get:Generated
    val id: Long

    val isbn: String
    val author: String
    val title: String

    val publishDate: LocalDate
}
