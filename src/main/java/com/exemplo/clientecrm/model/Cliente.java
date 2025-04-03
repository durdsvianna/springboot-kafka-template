package com.exemplo.clientecrm.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String documento;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
} 