package pl.touk.krush

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import pl.touk.krush.ReservationTable.freedAt
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

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
    fun shouldUpdateUsingExposed() {
        transaction {
            SchemaUtils.create(ReservationTable)

            // given
            val reservation = Reservation().reserve().let(ReservationTable::insert)
            val byUid: WhereEx = { ReservationTable.uid eq reservation.uid }
            val persistedReservation = ReservationTable.select(byUid).singleOrNull()?.toReservation()
            assertThat(persistedReservation?.status).isEqualTo(Status.RESERVED)
            assertThat(persistedReservation?.reservedAt).isCloseTo(reservation.reservedAt, within(10, ChronoUnit.MILLIS))
            assertThat(persistedReservation?.freedAt).isNull()

            // when
            val freedAt = LocalDateTime.now()
            val updated = ReservationTable.update({
                (ReservationTable.uid eq reservation.uid) and (ReservationTable.status eq Status.RESERVED) }) {
                    it[ReservationTable.status] = Status.FREE
                    it[ReservationTable.freedAt] = freedAt
                }
            if (updated < 1) {
                throw IllegalStateException("Wrong status!")
            }

            // then
            val updatedReservation = ReservationTable.select(byUid).singleOrNull()?.toReservation()
            assertThat(updatedReservation?.status).isEqualTo(Status.FREE)
            assertThat(updatedReservation?.reservedAt).isCloseTo(reservation.reservedAt, within(10, ChronoUnit.MILLIS))
            assertThat(updatedReservation?.freedAt).isCloseTo(freedAt, within(10, ChronoUnit.MILLIS))
        }

    }

    @Test
    fun shouldUpdateWithFrom() {
        transaction {
            SchemaUtils.create(ReservationTable)

            // given
            val reservation = Reservation().reserve().let(ReservationTable::insert)
            val byUid: WhereEx = { ReservationTable.uid eq reservation.uid }
            val persistedReservation = ReservationTable.select(byUid).singleOrNull()?.toReservation()
            assertThat(persistedReservation?.status).isEqualTo(Status.RESERVED)
            assertThat(persistedReservation?.reservedAt).isCloseTo(reservation.reservedAt, within(10, ChronoUnit.MILLIS))
            assertThat(persistedReservation?.freedAt).isNull()

            // when
            val freedReservation = reservation.free()
            ReservationTable.update(byUid) { it.from(freedReservation) }

            // then
            val updatedReservation = ReservationTable.select(byUid).singleOrNull()?.toReservation()
            assertThat(updatedReservation?.status).isEqualTo(Status.FREE)
            assertThat(updatedReservation?.reservedAt).isCloseTo(reservation.reservedAt, within(10, ChronoUnit.MILLIS))
            assertThat(updatedReservation?.freedAt).isCloseTo(freedReservation.freedAt, within(10, ChronoUnit.MILLIS))
        }
    }
}
