package pl.touk.krush

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class ArticleTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun connect() {
            Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")
        }
    }

    @Test
    fun shouldPersistArticleWithTags() {
        transaction {
            SchemaUtils.create(TagTable, ArticleTable, ArticleTagsTable)
            // given
            val tag1 = Tag(name = "jvm")
            val tag2 = Tag(name = "spring")

            val tags = listOf(tag1, tag2).map(TagTable::insert)
            val article = Article(title = "Spring for dummies", tags = tags)

            // when
            val persistedArticle = ArticleTable.insert(article)

            // then
            val (selectedArticle) = (ArticleTable leftJoin ArticleTagsTable leftJoin TagTable)
                .select { TagTable.name inList listOf("jvm", "spring") }
                .toArticleList()

            assertThat(selectedArticle).isEqualTo(persistedArticle)
        }
    }
}
