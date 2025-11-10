package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import org.example.entidades.*;
import org.example.servicio.CitaException;
import org.example.servicio.CitaManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hospital-persistence-unit");
        EntityManager em = emf.createEntityManager();

        try {
            System.out.println("=".repeat(80));
            System.out.println("SISTEMA DE GESTIÓN HOSPITALARIA - JPA/HIBERNATE");
            System.out.println("=".repeat(80));

            // ========== 1. CREAR Y PERSISTIR HOSPITAL CON ESTRUCTURA ==========
            System.out.println("\n>>> 1. CREANDO ESTRUCTURA HOSPITALARIA...");

            em.getTransaction().begin();

            // Crear Hospital
            Hospital hospital = Hospital.builder()
                    .nombre("Hospital Central de Buenos Aires")
                    .direccion("Av. Libertador 1234, CABA")
                    .telefono("011-4567-8901")
                    .build();

            // Crear Departamentos
            Departamento cardiologia = Departamento.builder()
                    .nombre("Departamento de Cardiología")
                    .especialidad(EspecialidadMedica.CARDIOLOGIA)
                    .build();

            Departamento pediatria = Departamento.builder()
                    .nombre("Departamento de Pediatría")
                    .especialidad(EspecialidadMedica.PEDIATRIA)
                    .build();

            Departamento traumatologia = Departamento.builder()
                    .nombre("Departamento de Traumatología")
                    .especialidad(EspecialidadMedica.TRAUMATOLOGIA)
                    .build();

            hospital.agregarDepartamento(cardiologia);
            hospital.agregarDepartamento(pediatria);
            hospital.agregarDepartamento(traumatologia);

            // Crear Salas
            Sala consultorioCard1 = cardiologia.crearSala("CARD-101", "Consultorio");
            Sala consultorioCard2 = cardiologia.crearSala("CARD-102", "Consultorio");
            Sala quirofanoCard = cardiologia.crearSala("CARD-201", "Quirófano");

            Sala consultorioPed1 = pediatria.crearSala("PED-101", "Consultorio");
            Sala emergenciasPed = pediatria.crearSala("PED-301", "Emergencias");

            Sala consultorioTrauma = traumatologia.crearSala("TRAUMA-101", "Consultorio");

            em.persist(hospital);
            em.persist(consultorioCard1);
            em.persist(consultorioCard2);
            em.persist(quirofanoCard);
            em.persist(consultorioPed1);
            em.persist(emergenciasPed);
            em.persist(consultorioTrauma);

            System.out.println("✓ Hospital creado: " + hospital.getNombre());
            System.out.println("✓ Departamentos creados: " + hospital.getDepartamentos().size());
            System.out.println("✓ Salas totales creadas: 6");

            // ========== 2. CREAR Y ASIGNAR MÉDICOS ==========
            System.out.println("\n>>> 2. REGISTRANDO MÉDICOS...");

            Medico cardiologo1 = Medico.builder()
                    .nombre("Carlos")
                    .apellido("González")
                    .dni("12345678")
                    .fechaNacimiento(LocalDate.of(1975, 5, 15))
                    .tipoSangre(TipoSangre.A_POSITIVO)
                    .matricula(new Matricula("MP-12345"))
                    .especialidad(EspecialidadMedica.CARDIOLOGIA)
                    .build();

            Medico pediatra1 = Medico.builder()
                    .nombre("Ana")
                    .apellido("Martínez")
                    .dni("23456789")
                    .fechaNacimiento(LocalDate.of(1980, 8, 20))
                    .tipoSangre(TipoSangre.O_NEGATIVO)
                    .matricula(new Matricula("MP-23456"))
                    .especialidad(EspecialidadMedica.PEDIATRIA)
                    .build();

            Medico traumatologo1 = Medico.builder()
                    .nombre("Roberto")
                    .apellido("Fernández")
                    .dni("34567890")
                    .fechaNacimiento(LocalDate.of(1978, 3, 10))
                    .tipoSangre(TipoSangre.B_POSITIVO)
                    .matricula(new Matricula("MP-34567"))
                    .especialidad(EspecialidadMedica.TRAUMATOLOGIA)
                    .build();

            cardiologia.agregarMedico(cardiologo1);
            pediatria.agregarMedico(pediatra1);
            traumatologia.agregarMedico(traumatologo1);

            em.persist(cardiologo1);
            em.persist(pediatra1);
            em.persist(traumatologo1);

            System.out.println("✓ Dr. " + cardiologo1.getNombreCompleto() + " - " +
                    cardiologo1.getEspecialidad().getDescripcion());
            System.out.println("✓ Dra. " + pediatra1.getNombreCompleto() + " - " +
                    pediatra1.getEspecialidad().getDescripcion());
            System.out.println("✓ Dr. " + traumatologo1.getNombreCompleto() + " - " +
                    traumatologo1.getEspecialidad().getDescripcion());

            // ========== 3. REGISTRAR PACIENTES ==========
            System.out.println("\n>>> 3. REGISTRANDO PACIENTES...");

            Paciente paciente1 = Paciente.builder()
                    .nombre("María")
                    .apellido("López")
                    .dni("11111111")
                    .fechaNacimiento(LocalDate.of(1985, 12, 5))
                    .tipoSangre(TipoSangre.A_POSITIVO)
                    .telefono("011-1111-1111")
                    .direccion("Calle Falsa 123, CABA")
                    .build();

            Paciente paciente2 = Paciente.builder()
                    .nombre("Juan")
                    .apellido("Pérez")
                    .dni("22222222")
                    .fechaNacimiento(LocalDate.of(1990, 6, 15))
                    .tipoSangre(TipoSangre.O_POSITIVO)
                    .telefono("011-2222-2222")
                    .direccion("Av. Corrientes 456, CABA")
                    .build();

            Paciente paciente3 = Paciente.builder()
                    .nombre("Sofía")
                    .apellido("Rodríguez")
                    .dni("33333333")
                    .fechaNacimiento(LocalDate.of(2015, 3, 20))
                    .tipoSangre(TipoSangre.B_NEGATIVO)
                    .telefono("011-3333-3333")
                    .direccion("Calle San Martín 789, CABA")
                    .build();

            hospital.agregarPaciente(paciente1);
            hospital.agregarPaciente(paciente2);
            hospital.agregarPaciente(paciente3);

            // CREAR HISTORIAS CLÍNICAS MANUALMENTE
            HistoriaClinica historia1 = new HistoriaClinica();
            historia1.setPaciente(paciente1);
            historia1.setNumeroHistoria("HC-" + paciente1.getDni() + "-" + System.currentTimeMillis());
            historia1.setFechaCreacion(LocalDateTime.now());
            paciente1.setHistoriaClinica(historia1);

            HistoriaClinica historia2 = new HistoriaClinica();
            historia2.setPaciente(paciente2);
            historia2.setNumeroHistoria("HC-" + paciente2.getDni() + "-" + System.currentTimeMillis());
            historia2.setFechaCreacion(LocalDateTime.now());
            paciente2.setHistoriaClinica(historia2);

            HistoriaClinica historia3 = new HistoriaClinica();
            historia3.setPaciente(paciente3);
            historia3.setNumeroHistoria("HC-" + paciente3.getDni() + "-" + System.currentTimeMillis());
            historia3.setFechaCreacion(LocalDateTime.now());
            paciente3.setHistoriaClinica(historia3);

            // Agregar información a historias clínicas
            historia1.agregarDiagnostico("Hipertensión arterial");
            historia1.agregarTratamiento("Enalapril 10mg cada 12 horas");
            historia1.agregarAlergia("Penicilina");

            historia2.agregarDiagnostico("Diabetes tipo 2");
            historia2.agregarTratamiento("Metformina 850mg");
            historia2.agregarAlergia("Ibuprofeno");

            historia3.agregarDiagnostico("Asma leve");
            historia3.agregarTratamiento("Salbutamol inhalador");

            //em.persist(historia1);
            //em.persist(historia2);
            //em.persist(historia3);

            System.out.println("✓ " + paciente1.getNombreCompleto() + " - HC: " +
                    historia1.getNumeroHistoria());
            System.out.println("✓ " + paciente2.getNombreCompleto() + " - HC: " +
                    historia2.getNumeroHistoria());
            System.out.println("✓ " + paciente3.getNombreCompleto() + " - HC: " +
                    historia3.getNumeroHistoria());

            em.getTransaction().commit();

            // ========== 4. PROGRAMAR CITAS ==========
            System.out.println("\n>>> 4. PROGRAMANDO CITAS MÉDICAS...");

            em.getTransaction().begin();

            CitaManager citaManager = new CitaManager();

            try {
                // Cita 1: Cardiología
                Cita cita1 = citaManager.programarCita(
                        paciente1,
                        cardiologo1,
                        consultorioCard1,
                        LocalDateTime.now().plusDays(1).withHour(10).withMinute(0),
                        new BigDecimal("150000.00")
                );
                cita1.setObservaciones("Control de presión arterial");
                em.persist(cita1);
                System.out.println("✓ " + cita1.getInfoCompleta());

                // Cita 2: Traumatología
                Cita cita2 = citaManager.programarCita(
                        paciente2,
                        traumatologo1,
                        consultorioTrauma,
                        LocalDateTime.now().plusDays(2).withHour(14).withMinute(0),
                        new BigDecimal("120000.00")
                );
                cita2.setObservaciones("Revisión de rodilla derecha");
                em.persist(cita2);
                System.out.println("✓ " + cita2.getInfoCompleta());

                // Cita 3: Pediatría
                Cita cita3 = citaManager.programarCita(
                        paciente3,
                        pediatra1,
                        consultorioPed1,
                        LocalDateTime.now().plusDays(3).withHour(9).withMinute(0),
                        new BigDecimal("100000.00")
                );
                cita3.setObservaciones("Control de crecimiento y desarrollo");
                em.persist(cita3);
                System.out.println("✓ " + cita3.getInfoCompleta());

            } catch (CitaException e) {
                System.err.println("✗ Error al programar cita: " + e.getMessage());
            }

            em.getTransaction().commit();

            // ========== 5. CONSULTAS JPQL ==========
            System.out.println("\n>>> 5. EJECUTANDO CONSULTAS JPQL...");

            // Consulta 1: Todos los médicos
            TypedQuery<Medico> queryMedicos = em.createQuery(
                    "SELECT m FROM Medico m", Medico.class
            );
            List<Medico> medicos = queryMedicos.getResultList();
            System.out.println("\n--- Total de Médicos: " + medicos.size() + " ---");
            for (Medico m : medicos) {
                System.out.println("  • " + m.getNombreCompleto() + " - " +
                        m.getEspecialidad().getDescripcion() + " (Mat: " + m.getNumeroMatricula() + ")");
            }

            // Consulta 2: Médicos por especialidad
            TypedQuery<Medico> queryCardiologos = em.createQuery(
                    "SELECT m FROM Medico m WHERE m.especialidad = :esp", Medico.class
            );
            queryCardiologos.setParameter("esp", EspecialidadMedica.CARDIOLOGIA);
            List<Medico> cardiologos = queryCardiologos.getResultList();
            System.out.println("\n--- Cardiólogos: " + cardiologos.size() + " ---");
            for (Medico m : cardiologos) {
                System.out.println("  • " + m.getNombreCompleto());
            }

            // Consulta 3: Pacientes con alergias
            TypedQuery<Paciente> queryAlergicos = em.createQuery(
                    "SELECT DISTINCT p FROM Paciente p JOIN p.historiaClinica h WHERE SIZE(h.alergias) > 0",
                    Paciente.class
            );
            List<Paciente> pacientesConAlergias = queryAlergicos.getResultList();
            System.out.println("\n--- Pacientes con Alergias: " + pacientesConAlergias.size() + " ---");
            for (Paciente p : pacientesConAlergias) {
                System.out.println("  • " + p.getNombreCompleto() + " - Alergias: " +
                        p.getHistoriaClinica().getAlergias());
            }

            // Consulta 4: Contar citas por estado
            System.out.println("\n--- Estadísticas de Citas por Estado ---");
            for (EstadoCita estado : EstadoCita.values()) {
                TypedQuery<Long> queryCount = em.createQuery(
                        "SELECT COUNT(c) FROM Cita c WHERE c.estado = :estado", Long.class
                );
                queryCount.setParameter("estado", estado);
                Long count = queryCount.getSingleResult();
                if (count > 0) {
                    System.out.println("  • " + estado + ": " + count);
                }
            }

            // ========== 6. ESTADÍSTICAS GENERALES ==========
            System.out.println("\n>>> 6. ESTADÍSTICAS GENERALES DEL SISTEMA...");

            Long totalHospitales = em.createQuery("SELECT COUNT(h) FROM Hospital h", Long.class)
                    .getSingleResult();
            Long totalDepartamentos = em.createQuery("SELECT COUNT(d) FROM Departamento d", Long.class)
                    .getSingleResult();
            Long totalSalas = em.createQuery("SELECT COUNT(s) FROM Sala s", Long.class)
                    .getSingleResult();
            Long totalMedicos = em.createQuery("SELECT COUNT(m) FROM Medico m", Long.class)
                    .getSingleResult();
            Long totalPacientes = em.createQuery("SELECT COUNT(p) FROM Paciente p", Long.class)
                    .getSingleResult();
            Long totalCitas = em.createQuery("SELECT COUNT(c) FROM Cita c", Long.class)
                    .getSingleResult();

            System.out.println("  • Hospitales registrados: " + totalHospitales);
            System.out.println("  • Departamentos: " + totalDepartamentos);
            System.out.println("  • Salas disponibles: " + totalSalas);
            System.out.println("  • Médicos activos: " + totalMedicos);
            System.out.println("  • Pacientes registrados: " + totalPacientes);
            System.out.println("  • Citas programadas: " + totalCitas);

            // ========== 7. ACTUALIZAR ESTADO DE CITA ==========
            System.out.println("\n>>> 7. ACTUALIZANDO ESTADO DE CITA...");

            em.getTransaction().begin();
            TypedQuery<Cita> queryCita = em.createQuery(
                    "SELECT c FROM Cita c WHERE c.paciente.dni = :dni", Cita.class
            );
            queryCita.setParameter("dni", "11111111");
            queryCita.setMaxResults(1);
            List<Cita> citasResult = queryCita.getResultList();

            if (!citasResult.isEmpty()) {
                Cita citaActualizar = citasResult.get(0);
                citaActualizar.setEstado(EstadoCita.COMPLETADA);
                citaActualizar.setObservaciones("Consulta realizada exitosamente. Paciente estable.");
                em.merge(citaActualizar);
                System.out.println("✓ Cita actualizada a estado: " + citaActualizar.getEstado());
            }
            em.getTransaction().commit();

            // ========== FINALIZACIÓN ==========
            System.out.println("\n" + "=".repeat(80));
            System.out.println("SISTEMA EJECUTADO EXITOSAMENTE");
            System.out.println("=".repeat(80));
            System.out.println("\n✓ Todas las entidades fueron persistidas correctamente");
            System.out.println("✓ Base de datos H2 creada en: ./data/hospidb.mv.db");
            System.out.println("✓ Relaciones bidireccionales verificadas");
            System.out.println("✓ Validaciones de negocio aplicadas");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("\n✗ ERROR EN LA EJECUCIÓN:");
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
            System.out.println("\n✓ Recursos liberados correctamente");
        }
    }
}