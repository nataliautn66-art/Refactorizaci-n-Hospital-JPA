package org.example.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Matricula implements Serializable {

    @Column(name = "numero_matricula", nullable = false, unique = true, length = 20)
    private String numero;

    public Matricula(String numero) {
        validarFormato(numero);
        this.numero = numero;
    }

    private void validarFormato(String numero) {
        if (numero == null || !numero.matches("MP-\\d{4,6}")) {
            throw new IllegalArgumentException(
                    "Formato de matrícula inválido. Debe ser MP- seguido de 4 a 6 dígitos. Ejemplo: MP-12345"
            );
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Matricula matricula = (Matricula) o;
        return Objects.equals(numero, matricula.numero);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero);
    }

    @Override
    public String toString() {
        return numero;
    }
}
