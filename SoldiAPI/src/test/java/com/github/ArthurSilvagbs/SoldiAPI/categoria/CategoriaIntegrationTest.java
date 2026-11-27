package com.github.ArthurSilvagbs.SoldiAPI.categoria;

import com.github.ArthurSilvagbs.SoldiAPI.IntegrationTestBase;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CategoriaIntegrationTest extends IntegrationTestBase {

   private String criar(String auth, String nome) throws Exception {
      String body = mvc.perform(post("/categoria")
            .header("Authorization", auth)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
               {"nome":"%s"}
               """.formatted(nome)))
         .andExpect(status().isCreated())
         .andReturn().getResponse().getContentAsString();
      return JsonPath.read(body, "$.idCategoria");
   }

   @Test
   void fluxoFeliz_criarBuscarListarAtualizarEExcluir() throws Exception {
      String auth = autenticar("arthur@email.com");

      // criar
      mvc.perform(post("/categoria")
            .header("Authorization", auth)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
               {"nome":"Mercado"}
               """))
         .andExpect(status().isCreated())
         .andExpect(jsonPath("$.nome").value("Mercado"))
         .andExpect(jsonPath("$.createdAt").isNotEmpty())
         .andExpect(jsonPath("$.idCategoria").exists())
         .andExpect(jsonPath("$.idUser").exists());

      String idLazer = criar(auth, "Lazer");
      String idMercado = JsonPath.read(
         mvc.perform(get("/categoria/Mercado").header("Authorization", auth))
            .andReturn().getResponse().getContentAsString(), "$.idCategoria");

      // buscar por nome
      mvc.perform(get("/categoria/Mercado").header("Authorization", auth))
         .andExpect(status().isOk())
         .andExpect(jsonPath("$.nome").value("Mercado"))
         .andExpect(jsonPath("$.createdAt").isNotEmpty());

      // listar
      mvc.perform(get("/categoria").header("Authorization", auth))
         .andExpect(status().isOk())
         .andExpect(jsonPath("$.length()").value(2));

      // atualizar nome
      mvc.perform(put("/categoria/" + idMercado)
            .header("Authorization", auth)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
               {"nome":"Supermercado"}
               """))
         .andExpect(status().isOk())
         .andExpect(jsonPath("$.nome").value("Supermercado"));

      mvc.perform(get("/categoria/Mercado").header("Authorization", auth))
         .andExpect(status().isNotFound());

      // excluir
      mvc.perform(delete("/categoria/" + idLazer).header("Authorization", auth))
         .andExpect(status().isNoContent());

      mvc.perform(get("/categoria/Lazer").header("Authorization", auth))
         .andExpect(status().isNotFound());
   }

   @Test
   void criar_nomeDuplicado_deveRetornar409() throws Exception {
      String auth = autenticar("arthur@email.com");
      criar(auth, "Mercado");

      mvc.perform(post("/categoria")
            .header("Authorization", auth)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
               {"nome":"Mercado"}
               """))
         .andExpect(status().isConflict());
   }

   @Test
   void criar_nomeEmBranco_deveRetornar400() throws Exception {
      String auth = autenticar("arthur@email.com");

      mvc.perform(post("/categoria")
            .header("Authorization", auth)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
               {"nome":"  "}
               """))
         .andExpect(status().isBadRequest());
   }

   @Test
   void endpoints_semToken_devemRetornar401() throws Exception {
      mvc.perform(post("/categoria")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
               {"nome":"Mercado"}
               """))
         .andExpect(status().isUnauthorized());
      mvc.perform(get("/categoria/Mercado")).andExpect(status().isUnauthorized());
      mvc.perform(put("/categoria/" + java.util.UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
               {"nome":"X"}
               """))
         .andExpect(status().isUnauthorized());
      mvc.perform(delete("/categoria/" + java.util.UUID.randomUUID())).andExpect(status().isUnauthorized());
   }

   @Test
   void listar_semCategorias_deveRetornar404() throws Exception {
      String auth = autenticar("arthur@email.com");

      mvc.perform(get("/categoria").header("Authorization", auth))
         .andExpect(status().isNotFound());
   }

   @Test
   void listar_deveRetornarSomenteCategoriasDoProprioUsuario() throws Exception {
      String authA = autenticar("a@email.com");
      String authB = autenticar("b@email.com");
      criar(authA, "Mercado");
      criar(authB, "Transporte");

      mvc.perform(get("/categoria").header("Authorization", authA))
         .andExpect(status().isOk())
         .andExpect(jsonPath("$.length()").value(1))
         .andExpect(jsonPath("$[0].nome").value("Mercado"));

      mvc.perform(get("/categoria/Transporte").header("Authorization", authA))
         .andExpect(status().isNotFound());
   }

   @Test
   void atualizar_categoriaDeOutroUsuario_deveRetornar404EManterNome() throws Exception {
      String authA = autenticar("a@email.com");
      String authB = autenticar("b@email.com");
      String idDeA = criar(authA, "Mercado");

      mvc.perform(put("/categoria/" + idDeA)
            .header("Authorization", authB)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
               {"nome":"Invadida"}
               """))
         .andExpect(status().isNotFound());

      mvc.perform(get("/categoria/Mercado").header("Authorization", authA))
         .andExpect(status().isOk());
   }

   @Test
   void excluir_idMalformado_deveRetornar400() throws Exception {
      String auth = autenticar("arthur@email.com");

      mvc.perform(delete("/categoria/nao-e-uuid").header("Authorization", auth))
         .andExpect(status().isBadRequest());
   }

   @Test
   void excluir_categoriaDeOutroUsuario_deveRetornar404EManterCategoria() throws Exception {
      String authA = autenticar("a@email.com");
      String authB = autenticar("b@email.com");
      String idDeA = criar(authA, "Mercado");

      mvc.perform(delete("/categoria/" + idDeA).header("Authorization", authB))
         .andExpect(status().isNotFound());

      mvc.perform(get("/categoria/Mercado").header("Authorization", authA))
         .andExpect(status().isOk());
   }

   @Test
   void criar_mesmoNomeEmUsuariosDiferentes_devePermitirNosDois() throws Exception {
      String authA = autenticar("a@email.com");
      String authB = autenticar("b@email.com");

      criar(authA, "Mercado");
      criar(authB, "Mercado");

      mvc.perform(get("/categoria/Mercado").header("Authorization", authA))
         .andExpect(status().isOk());
      mvc.perform(get("/categoria/Mercado").header("Authorization", authB))
         .andExpect(status().isOk());
   }
}
