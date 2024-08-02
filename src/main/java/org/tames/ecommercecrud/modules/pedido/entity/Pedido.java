package org.tames.ecommercecrud.modules.pedido.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.tames.ecommercecrud.modules.pedido.enums.StatusPedido;

import java.time.LocalDate;

@Entity
@Table(name = "pedido")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private LocalDate data;

    @Enumerated(EnumType.STRING)
    @NotNull
    private StatusPedido status;
}
