package pl.touk.krush

import java.time.LocalDateTime
import java.util.*
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id

@Entity
data class Reservation(
    @Id
    val uid: UUID = UUID.randomUUID(),

    @Enumerated(EnumType.STRING)
    val status: Status = Status.FREE,

    val reservedAt: LocalDateTime? = null,
    val freedAt: LocalDateTime? = null
) {

    fun reserve() = copy(status = Status.RESERVED, reservedAt = LocalDateTime.now())

    fun free() = copy(status = Status.FREE, freedAt = LocalDateTime.now())
}

enum class Status { FREE, RESERVED }
