package br.com.alurafood.pedidos.amqp;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PagamentoDto {
    private Long id;
    private BigDecimal valor;
    private String nome;
    private String numero;
    private String expiracao;
    private String codigo;
    private PagamentoStatus status;
    private Long formaDePagamentoId;
    private Long pedidoId;


}
