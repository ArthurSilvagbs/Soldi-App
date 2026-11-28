package com.github.ArthurSilvagbs.SoldiAPI.gasto;

import com.github.ArthurSilvagbs.SoldiAPI.gasto.dtos.GastoCreateDTO;
import com.github.ArthurSilvagbs.SoldiAPI.gasto.dtos.GastoResponseDTO;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GastoMapper {

   public Gasto mapearParaGasto(UUID idUser, GastoCreateDTO dto) {
      return new Gasto(
         idUser,
         dto.idCategoria(),
         dto.descricao(),
         dto.valor(),
         dto.data()
      );
   }

   public GastoResponseDTO mapearParaResponse(Gasto gasto) {
      return new GastoResponseDTO(
         gasto.getIdGasto(),
         gasto.getIdUser(),
         gasto.getIdCategoria(),
         gasto.getDescricao(),
         gasto.getValor(),
         gasto.getData(),
         gasto.getCreatedAt()
      );
   }

}
