package pl.touk.krush

import io.requery.kotlin.eq
import io.requery.sql.KotlinConfiguration
import io.requery.sql.KotlinEntityDataStore
import io.requery.sql.SchemaModifier
import io.requery.sql.TableCreationMode
import org.assertj.core.api.Assertions.assertThat
import org.h2.jdbcx.JdbcDataSource
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.Month

class BookTest {

    companion object {
        lateinit var dataStore : KotlinEntityDataStore<Any>

        @BeforeAll
        @JvmStatic
        fun connect() {
            val datasource = JdbcDataSource().apply {
                setUrl("jdbc:h2:/tmp/test")
            }
            val models = Models.DEFAULT
            val configuration = KotlinConfiguration(
                dataSource = datasource,
                model = models,
                statementCacheSize = 0,
                useDefaultLogging = true
            )
            dataStore = KotlinEntityDataStore(configuration)

            val tables = SchemaModifier(configuration)
            val mode = TableCreationMode.DROP_CREATE
            tables.createTables(mode)
        }
    }

    @Test
    fun shouldPersistBook() {
        //given
        val book = BookEntity().apply {
            setIsbn("1449373321")
            setPublishDate(LocalDate.of(2017, Month.APRIL, 11))
            setTitle("Designing Data-Intensive Applications")
            setAuthor("Martin Kleppmann")
        }

        // when
        val persistedBook = dataStore.insert(book)

        // then
        val books = dataStore.select(Book::class).where(Book::id eq book.id).get().toList()

        assertThat(books).containsExactly(persistedBook)
    }

}
