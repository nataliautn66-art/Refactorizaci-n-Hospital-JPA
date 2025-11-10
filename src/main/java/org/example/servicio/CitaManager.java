package org.example.servicio;

import org.example.entidades.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class CitaManager {

    private final List<Cita> citas = new ArrayList<>();
    private final Map<Paciente, List<Cita>> citasPorPaciente = new HashMap<>();
    private final Map<Medico, List<Cita>> citasPorMedico = new HashMap<>();
    private final Map<Sala, List<Cita>> citasPorSala = new HashMap<>();

    private static final long BUFFER_HORAS = 2;

    public Cita programarCita(Paciente paciente, Medico medico, Sala sala,
                              LocalDateTime fechaHora, BigDecimal costo) throws CitaException {

        validarCita(paciente, medico, sala, fechaHora, costo);

        Cita cita = Cita.builder()
                .paciente(paciente)
                .medico(medico)
                .sala(sala)
                .fechaHora(fechaHora)
                .costo(costo)
                .estado(EstadoCita.PROGRAMADA)
                .build();

        // Establecer relaciones bidireccionales
        paciente.addCita(cita);
        medico.addCita(cita);
        sala.addCita(cita);

        agregarCitaAIndices(cita);

        return cita;
    }

    private void validarCita(Paciente paciente, Medico medico, Sala sala,
                             LocalDateTime fechaHora, BigDecimal costo) throws CitaException {

        // 1. Validación temporal
        if (fechaHora.isBefore(LocalDateTime.now())) {
            throw new CitaException("No se puede programar una cita en el pasado");
        }

        // 2. Validación de costo
        if (costo == null || costo.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CitaException("El costo debe ser mayor que cero");
        }

        // 3. Validación de especialidad
        if (!medico.getEspecialidad().equals(sala.getDepartamento().getEspecialidad())) {
            throw new CitaException(
                    String.format("La especialidad del médico (%s) no coincide con el departamento de la sala (%s)",
                            medico.getEspecialidad().getDescripcion(),
                            sala.getDepartamento().getEspecialidad().getDescripcion())
            );
        }

        // 4. Validación de disponibilidad del médico
        if (!esMedicoDisponible(medico, fechaHora)) {
            throw new CitaException(
                    String.format("El médico %s no está disponible en el horario solicitado (buffer de %d horas)",
                            medico.getNombreCompleto(), BUFFER_HORAS)
            );
        }

        // 5. Validación de disponibilidad de la sala
        if (!esSalaDisponible(sala, fechaHora)) {
            throw new CitaException(
                    String.format("La sala %s no está disponible en el horario solicitado (buffer de %d horas)",
                            sala.getNumero(), BUFFER_HORAS)
            );
        }
    }

    private boolean esMedicoDisponible(Medico medico, LocalDateTime fechaHora) {
        List<Cita> citasMedico = citasPorMedico.getOrDefault(medico, Collections.emptyList());

        for (Cita citaExistente : citasMedico) {
            long horasDiferencia = Math.abs(
                    ChronoUnit.HOURS.between(citaExistente.getFechaHora(), fechaHora)
            );
            if (horasDiferencia < BUFFER_HORAS) {
                return false;
            }
        }
        return true;
    }

    private boolean esSalaDisponible(Sala sala, LocalDateTime fechaHora) {
        List<Cita> citasSala = citasPorSala.getOrDefault(sala, Collections.emptyList());

        for (Cita citaExistente : citasSala) {
            long horasDiferencia = Math.abs(
                    ChronoUnit.HOURS.between(citaExistente.getFechaHora(), fechaHora)
            );
            if (horasDiferencia < BUFFER_HORAS) {
                return false;
            }
        }
        return true;
    }

    private void agregarCitaAIndices(Cita cita) {
        citas.add(cita);

        citasPorPaciente.computeIfAbsent(cita.getPaciente(), k -> new ArrayList<>()).add(cita);
        citasPorMedico.computeIfAbsent(cita.getMedico(), k -> new ArrayList<>()).add(cita);
        citasPorSala.computeIfAbsent(cita.getSala(), k -> new ArrayList<>()).add(cita);
    }

    public List<Cita> getCitasPorPaciente(Paciente paciente) {
        return Collections.unmodifiableList(
                citasPorPaciente.getOrDefault(paciente, Collections.emptyList())
        );
    }

    public List<Cita> getCitasPorMedico(Medico medico) {
        return Collections.unmodifiableList(
                citasPorMedico.getOrDefault(medico, Collections.emptyList())
        );
    }

    public List<Cita> getCitasPorSala(Sala sala) {
        return Collections.unmodifiableList(
                citasPorSala.getOrDefault(sala, Collections.emptyList())
        );
    }

    public List<Cita> getTodasLasCitas() {
        return Collections.unmodifiableList(citas);
    }

    public void limpiarCitas() {
        citas.clear();
        citasPorPaciente.clear();
        citasPorMedico.clear();
        citasPorSala.clear();
    }
}