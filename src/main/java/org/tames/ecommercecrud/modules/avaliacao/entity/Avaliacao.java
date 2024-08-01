package org.tames.ecommercecrud.modules.avaliacao.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import org.tames.ecommercecrud.modules.avaliacao.enums.Nota;

import java.time.LocalDate;

@Entity
@Table(name = "avaliacao")
public class Avaliacao {
    @Id
    @GeneratedValue
    private Long id;

    @NotEmpty
    private String descricao;

    @NotEmpty
    private LocalDate data;

    @Enumerated(EnumType.STRING)
    @NotEmpty
    private Nota nota;

    public Avaliacao() {}

    public Avaliacao(String descricao, LocalDate data, Nota nota) {
        this.descricao = descricao;
        this.data = data;
        this.nota = nota;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public Nota getNota() {
        return nota;
    }

    public void setNota(Nota nota) {
        this.nota = nota;
    }
}
