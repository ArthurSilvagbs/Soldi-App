package com.github.ArthurSilvagbs.SoldiAPI.gasto.dtos;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record GastoUpdateDTO(

   @NotNull(message = "A categoria é obrigatória.")
   UUID idCategoria,

   @NotBlank(message = "A descrição é obrigatória.")
   @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres.")
   String descricao,

   @NotNull(message = "O valor é obrigatório.")
   @Positive(message = "O valor deve ser maior que zero.")
   @Digits(integer = 17, fraction = 2, message = "O valor deve ter no máximo 17 dígitos inteiros e 2 casas decimais.")
   BigDecimal valor,

   @NotNull(message = "A data é obrigatória.")
   LocalDate data
) {
}
