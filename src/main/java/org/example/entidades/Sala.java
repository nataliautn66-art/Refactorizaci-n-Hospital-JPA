package org.example.entidades;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "salas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"departamento", "citas"})
public class Sala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String numero;

    @Column(nullable = false, length = 100)
    private String tipo;

    @ManyToOne
    @JoinColumn(name = "departamento_id", nullable = false)
    private Departamento departamento;

    @OneToMany(mappedBy = "sala", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
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
}