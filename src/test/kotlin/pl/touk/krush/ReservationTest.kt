package pl.touk.krush

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

typealias WhereEx = SqlExpressionBuilder.() -> Op<Boolean>

class ReservationTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun connect() {
            Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")
        }
    }

    @Test
    fun shouldUpdateReservation() {
        transaction {
            SchemaUtils.create(ReservationTable)

            // given
            val reservation = Reservation().reserve().let(ReservationTable::insert)
            val byUid: WhereEx = { ReservationTable.uid eq reservation.uid }
            val persistedReservation = ReservationTable.select(byUid).singleOrNull()?.toReservation()
            assertThat(persistedReservation?.status).isEqualTo(Status.RESERVED)
            assertThat(persistedReservation?.reservedAt).isEqualTo(reservation.reservedAt)
            assertThat(persistedReservation?.freedAt).isNull()

            // when
            val freedReservation = reservation.free()
            ReservationTable.update(byUid) { it.from(freedReservation) }

            // then
            val updatedReservation = ReservationTable.select(byUid).singleOrNull()?.toReservation()
            assertThat(updatedReservation?.status).isEqualTo(Status.FREE)
            assertThat(updatedReservation?.reservedAt).isEqualTo(reservation.reservedAt)
            assertThat(updatedReservation?.freedAt).isEqualTo(freedReservation.freedAt)
        }
    }
}
