package org.tames.ecommercecrud.modules.metodopagamento.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "metodo_pagamento")
public class MetodoPagamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Double taxaTransacao;

    @NotEmpty
    private String nome;

    public MetodoPagamento() {}

    public MetodoPagamento(String nome, Double taxaTransacao) {
        this(nome);
        this.taxaTransacao = taxaTransacao;
    }

    public MetodoPagamento(String nome) {
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotNull Double getTaxaTransacao() {
        return taxaTransacao;
    }

    public void setTaxaTransacao(@NotNull Double taxaTransacao) {
        this.taxaTransacao = taxaTransacao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
