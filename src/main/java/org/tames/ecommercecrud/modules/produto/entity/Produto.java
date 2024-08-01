package org.tames.ecommercecrud.modules.produto.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Entity
@Table(name = "produto")
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotEmpty
    String nome;

    @NotNull
    BigDecimal preco;

    @NotEmpty
    String descricao;

    @NotNull
    Integer quantidadeEstoque;

    public Produto() {
    }

    public Produto(String nome, BigDecimal preco, String descricao, Integer quantidadeEstoque) {
        this(nome, preco, descricao);
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public Produto(String nome, BigDecimal preco, String descricao) {
        this.preco = preco;
        this.nome = nome;
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public Integer getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(Integer quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
