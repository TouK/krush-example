package pl.touk.krush

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class TestEntityTest : BaseDatabaseTest() {

    @AfterEach
    internal fun tearDown() {
        transaction {
            TestEntityTable.deleteAll()
        }
    }

    @Test
    fun shouldUpdateTestEntity() {
        transaction {
            SchemaUtils.create(TestEntityTable)

            val entity = TestEntity(value = 2)

            val persistedEntity = TestEntityTable.insert(entity)
            assertThat(persistedEntity.id).isNotNull()
            val id = persistedEntity.id ?: throw IllegalArgumentException()

            val fetchedEntity =
                TestEntityTable.select { TestEntityTable.id eq id }.singleOrNull()?.toTestEntity() ?: throw IllegalArgumentException()

            val updatedEntity = fetchedEntity.copy(value = 2, optInt = 3)
            TestEntityTable.update({ TestEntityTable.id eq id }) { it.from(updatedEntity) }

            val allEntities = TestEntityTable.selectAll().toTestEntityList()
            assertThat(allEntities).containsExactly(updatedEntity)
        }
    }
}
