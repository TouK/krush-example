package pl.touk.krush

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class TestEntity (
    @Id @GeneratedValue
    val id: Int? = null,
    val value: Int,
    val optInt: Int? = null
)
