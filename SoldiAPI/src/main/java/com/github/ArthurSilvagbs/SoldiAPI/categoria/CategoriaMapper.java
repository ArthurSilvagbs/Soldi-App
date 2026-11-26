package com.github.ArthurSilvagbs.SoldiAPI.categoria;

import com.github.ArthurSilvagbs.SoldiAPI.categoria.dtos.CategoriaCreateDTO;
import com.github.ArthurSilvagbs.SoldiAPI.categoria.dtos.CategoriaResponseDTO;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CategoriaMapper {

   public Categoria mapearParaCategoria(UUID idUser, CategoriaCreateDTO dto) {
      return new Categoria(idUser, dto.nome());
   }

   public CategoriaResponseDTO mapearParaResponse(Categoria categoria) {
      return new CategoriaResponseDTO(
         categoria.getIdCategoria(),
         categoria.getIdUser(),
         categoria.getNome(),
         categoria.getCreatedAt()
      );
   }

}
