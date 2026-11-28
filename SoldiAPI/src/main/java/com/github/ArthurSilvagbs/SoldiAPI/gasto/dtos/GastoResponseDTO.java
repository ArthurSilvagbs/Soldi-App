package com.github.ArthurSilvagbs.SoldiAPI.gasto.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record GastoResponseDTO(
   UUID idGasto,
   UUID idUser,
   UUID idCategoria,
   String descricao,
   BigDecimal valor,
   LocalDate data,
   LocalDateTime createdAt
) {
}
