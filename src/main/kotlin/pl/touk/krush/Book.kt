package pl.touk.krush

import io.requery.*
import java.time.LocalDate

@Entity
@Table(name = "BOOK")
interface Book : Persistable {
    @get:Key
    @get:io.requery.Generated
    val id: Long

    val isbn: String
    val author: String
    val title: String

    @get:Column(name = "PUBLISH_DATE")
    val publishDate: LocalDate
}
