package pl.touk.krush

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class ArticleTest : BaseDatabaseTest() {

    @AfterEach
    internal fun tearDown() {
        transaction {
            ArticleTagsTable.deleteAll()
            ArticleTable.deleteAll()
            TagTable.deleteAll()
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
