package com.github.ArthurSilvagbs.SoldiAPI.categoria.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record CategoriaResponseDTO(
   UUID idCategoria,
   UUID idUser,
   String nome,
   LocalDateTime createdAt
) {
}
