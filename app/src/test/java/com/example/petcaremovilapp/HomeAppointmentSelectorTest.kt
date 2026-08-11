package com.example.petcaremovilapp

import com.example.petcaremovilapp.models.dto.AppointmentDto
import com.example.petcaremovilapp.ui.viewmodel.selectHomeAppointments
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeAppointmentSelectorTest {
    private val now = Instant.parse("2026-08-11T12:00:00Z")

    @Test fun clientGetsTwoNearestActiveFutureAppointments() {
        val values = listOf(
            appointment("late", "2026-08-14T12:00:00Z", "confirmada"),
            appointment("past", "2026-08-10T12:00:00Z", "confirmada"),
            appointment("cancelled", "2026-08-12T12:00:00Z", "cancelada"),
            appointment("first", "2026-08-12T10:00:00Z", "pendiente"),
            appointment("second", "2026-08-13T10:00:00Z", "confirmada")
        )
        assertEquals(listOf("first", "second"), selectHomeAppointments(values, 3, now).map { it.id_appointment })
    }

    @Test fun veterinarianGetsOnlyPendingAppointments() {
        val values = listOf(appointment("confirmed", "2026-08-12T10:00:00Z", "confirmada"), appointment("pending", "2026-08-13T10:00:00Z", "pendiente"))
        assertEquals(listOf("pending"), selectHomeAppointments(values, 2, now).map { it.id_appointment })
    }

    @Test fun adminGetsNoHomeAppointments() {
        assertTrue(selectHomeAppointments(listOf(appointment("one", "2026-08-12T10:00:00Z", "pendiente")), 1, now).isEmpty())
    }

    private fun appointment(id: String, date: String, status: String) = AppointmentDto(
        id, "Cliente", "Luna", "Mestiza", 10.0, 3, "Vet", "vet@example.com", null,
        "Clinica", "Centro", date, "Consulta", 350.0, status
    )
}
