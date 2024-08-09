package org.tames.ecommercecrud.modules.shippingaddress.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

@Entity
@Table(name = "shipping_address")
public class EnderecoEntrega {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotEmpty private String endereco;

  @NotEmpty private String complemento;

  @NotEmpty private String bairro;

  @NotEmpty private String cidade;

  @NotEmpty private String cep;

  public EnderecoEntrega() {}

  public EnderecoEntrega(
      String endereco, String complemento, String bairro, String cidade, String cep) {
    this.endereco = endereco;
    this.complemento = complemento;
    this.bairro = bairro;
    this.cidade = cidade;
    this.cep = cep;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getEndereco() {
    return endereco;
  }

  public void setEndereco(String endereco) {
    this.endereco = endereco;
  }

  public String getComplemento() {
    return complemento;
  }

  public void setComplemento(String complemento) {
    this.complemento = complemento;
  }

  public String getBairro() {
    return bairro;
  }

  public void setBairro(String bairro) {
    this.bairro = bairro;
  }

  public String getCidade() {
    return cidade;
  }

  public void setCidade(String cidade) {
    this.cidade = cidade;
  }

  public String getCep() {
    return cep;
  }

  public void setCep(String cep) {
    this.cep = cep;
  }
}
