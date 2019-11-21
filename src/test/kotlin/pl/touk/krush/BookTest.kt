package pl.touk.krush

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.Month

class BookTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun connect() {
            Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")
        }
    }

    @Test
    fun shouldPersistBook() {
        transaction {
            SchemaUtils.create(BookTable)

            //given
            val book = Book(
                isbn = "1449373321", publishDate = LocalDate.of(2017, Month.APRIL, 11),
                title = "Designing Data-Intensive Applications", author = "Martin Kleppmann"
            )

            val persistedBook = BookTable.insert(book)
            assertThat(persistedBook.id).isNotNull()

            // when
            val selectedBooks = (BookTable)
                .select { BookTable.author like "Martin K%" }
                .toBookList()

            // then
            assertThat(selectedBooks).containsOnly(persistedBook)
        }
    }

}
