package com.github.ArthurSilvagbs.SoldiAPI.categoria;

import com.github.ArthurSilvagbs.SoldiAPI.categoria.dtos.CategoriaCreateDTO;
import com.github.ArthurSilvagbs.SoldiAPI.categoria.dtos.CategoriaResponseDTO;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CategoriaMapperTest {

   private final CategoriaMapper mapper = new CategoriaMapper();

   @Test
   void mapearParaCategoria_deveCopiarUsuarioENome() {
      UUID idUser = UUID.randomUUID();

      Categoria categoria = mapper.mapearParaCategoria(idUser, new CategoriaCreateDTO("Mercado"));

      assertThat(categoria.getIdUser()).isEqualTo(idUser);
      assertThat(categoria.getNome()).isEqualTo("Mercado");
   }

   @Test
   void mapearParaResponse_deveCopiarCampos() {
      UUID idUser = UUID.randomUUID();
      Categoria categoria = new Categoria(idUser, "Lazer");

      CategoriaResponseDTO response = mapper.mapearParaResponse(categoria);

      assertThat(response.idUser()).isEqualTo(idUser);
      assertThat(response.nome()).isEqualTo("Lazer");
   }
}
