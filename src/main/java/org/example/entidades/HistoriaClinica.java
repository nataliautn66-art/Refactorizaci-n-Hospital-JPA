package org.example.entidades;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "historias_clinicas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "paciente")
public class HistoriaClinica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_historia", nullable = false, unique = true, length = 50)
    private String numeroHistoria;

    @OneToOne
    @JoinColumn(name = "paciente_id", nullable = false, unique = true)
    private Paciente paciente;

    @Column(name = "fecha_creacion", nullable = false)
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @ElementCollection
    @CollectionTable(name = "diagnosticos", joinColumns = @JoinColumn(name = "historia_id"))
    @Column(name = "diagnostico", length = 500)
    private List<String> diagnostics = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "tratamientos", joinColumns = @JoinColumn(name = "historia_id"))
    @Column(name = "tratamiento", length = 500)
    private List<String> tratamientos = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "alergias", joinColumns = @JoinColumn(name = "historia_id"))
    @Column(name = "alergia", length = 200)
    private List<String> alergias = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (numeroHistoria == null && paciente != null) {
            numeroHistoria = generarNumeroHistoria(paciente.getDni());
        }
        if (diagnostics == null) {
            diagnostics = new ArrayList<>();
        }
        if (tratamientos == null) {
            tratamientos = new ArrayList<>();
        }
        if (alergias == null) {
            alergias = new ArrayList<>();
        }
    }

    private String generarNumeroHistoria(String dni) {
        long timestamp = System.currentTimeMillis();
        return String.format("HC-%s-%d", dni, timestamp);
    }

    public void agregarDiagnostico(String diagnostico) {
        if (diagnostico == null || diagnostico.trim().isEmpty()) {
            throw new IllegalArgumentException("El diagnóstico no puede estar vacío");
        }
        if (this.diagnostics == null) {
            this.diagnostics = new ArrayList<>();
        }
        this.diagnostics.add(diagnostico.trim());
    }

    public void agregarTratamiento(String tratamiento) {
        if (tratamiento == null || tratamiento.trim().isEmpty()) {
            throw new IllegalArgumentException("El tratamiento no puede estar vacío");
        }
        if (this.tratamientos == null) {
            this.tratamientos = new ArrayList<>();
        }
        this.tratamientos.add(tratamiento.trim());
    }

    public void agregarAlergia(String alergia) {
        if (alergia == null || alergia.trim().isEmpty()) {
            throw new IllegalArgumentException("La alergia no puede estar vacía");
        }
        if (this.alergias == null) {
            this.alergias = new ArrayList<>();
        }
        this.alergias.add(alergia.trim());
    }

    public List<String> getDiagnostics() {
        if (diagnostics == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(diagnostics);
    }

    public List<String> getTratamientos() {
        if (tratamientos == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(tratamientos);
    }

    public List<String> getAlergias() {
        if (alergias == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(alergias);
    }
}