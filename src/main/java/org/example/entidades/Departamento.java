package org.example.entidades;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "departamentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"hospital", "medicos", "salas"})
public class Departamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EspecialidadMedica especialidad;

    @ManyToOne
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @OneToMany(mappedBy = "departamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Medico> medicos = new ArrayList<>();

    @OneToMany(mappedBy = "departamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sala> salas = new ArrayList<>();

    public void agregarMedico(Medico medico) {
        if (medico == null) {
            throw new IllegalArgumentException("El médico no puede ser nulo");
        }
        if (!medico.getEspecialidad().equals(this.especialidad)) {
            throw new IllegalArgumentException(
                    String.format("La especialidad del médico (%s) no coincide con la del departamento (%s)",
                            medico.getEspecialidad().getDescripcion(),
                            this.especialidad.getDescripcion())
            );
        }
        if (this.medicos == null) {
            this.medicos = new ArrayList<>();
        }
        if (!this.medicos.contains(medico)) {
            this.medicos.add(medico);
            medico.setDepartamento(this);
        }
    }

    public Sala crearSala(String numero, String tipo) {
        Sala sala = Sala.builder()
                .numero(numero)
                .tipo(tipo)
                .departamento(this)
                .citas(new ArrayList<>())
                .build();
        if (this.salas == null) {
            this.salas = new ArrayList<>();
        }
        this.salas.add(sala);
        return sala;
    }

    public List<Medico> getMedicos() {
        if (medicos == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(medicos);
    }

    public List<Sala> getSalas() {
        if (salas == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(salas);
    }
}