package com.github.ArthurSilvagbs.SoldiAPI.gasto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Consultas de gasto usadas por outras features. Depende apenas do repository,
 * para não criar dependência circular entre services.
 */
@Service
@RequiredArgsConstructor
public class GastoConsultaService {

   private final GastoRepository repository;

   @Transactional(readOnly = true)
   public boolean categoriaPossuiGastos(UUID idCategoria, UUID idUser) {
      return repository.existsByIdCategoriaAndIdUser(idCategoria, idUser);
   }

}
