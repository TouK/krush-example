package pl.touk.krush

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.replace
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

typealias WhereEx = SqlExpressionBuilder.() -> Op<Boolean>

class ReservationTest : BaseDatabaseTest() {

    @AfterEach
    internal fun tearDown() {
        transaction {
            ReservationTable.deleteAll()
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

    @Test
    fun shouldReplace() {
        transaction {
            SchemaUtils.create(ReservationTable)

            // given
            val reservation = Reservation().reserve()

            // when
            ReservationTable.replace { it.from(reservation) }
            val freedReservation = reservation.free()
            ReservationTable.replace { it.from(freedReservation) }

            // then
            val allReservations = ReservationTable.selectAll().toReservationList()
            assertThat(allReservations)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("reservedAt", "freedAt")
                .containsExactly(freedReservation)
        }
    }

    @Test
    fun shouldBatchInsert() {
        transaction {
            SchemaUtils.create(ReservationTable)

            // given
            val reservation1 = Reservation().reserve()
            val reservation2 = Reservation().reserve()

            // when
            ReservationTable.batchInsert(
                listOf(reservation1, reservation2), body = { this.from(it) }
            )

            // then
            val allReservations = ReservationTable.selectAll().toReservationList()
            assertThat(allReservations)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("reservedAt")
                .containsExactly(reservation1, reservation2)
        }
    }

}
