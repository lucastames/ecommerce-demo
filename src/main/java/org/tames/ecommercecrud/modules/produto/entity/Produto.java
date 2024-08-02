package org.tames.ecommercecrud.modules.produto.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.tames.ecommercecrud.modules.avaliacao.entity.Avaliacao;
import org.tames.ecommercecrud.modules.categoria.entity.Categoria;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "produto")
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    private String nome;

    @NotNull
    private BigDecimal preco;

    @NotEmpty
    private String descricao;

    @NotNull
    private Integer quantidadeEstoque;

    @ManyToMany()
    @JoinTable(name = "produto_categoria", joinColumns = @JoinColumn(name = "id_produto"), inverseJoinColumns = @JoinColumn(name = "id_categoria"))
    private Set<Categoria> categorias = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_produto")
    private List<Avaliacao> avaliacoes = new ArrayList<>();

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

    public Set<Categoria> getCategorias() {
        return categorias;
    }

    public List<Avaliacao> getAvaliacoes() {
        return avaliacoes;
    }
}
