package org.example.entidades;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "medicos")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true, exclude = {"departamento", "citas"})
public class Medico extends Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Matricula matricula;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EspecialidadMedica especialidad;

    @ManyToOne
    @JoinColumn(name = "departamento_id")
    private Departamento departamento;

    @OneToMany(mappedBy = "medico", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Cita> citas = new ArrayList<>();

    public void addCita(Cita cita) {
        if (this.citas == null) {
            this.citas = new ArrayList<>();
        }
        this.citas.add(cita);
    }

    public List<Cita> getCitas() {
        if (citas == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(citas);
    }

    public String getNumeroMatricula() {
        return matricula != null ? matricula.getNumero() : null;
    }
}