package com.github.ArthurSilvagbs.SoldiAPI;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Contrato HTTP: entradas malformadas, limites de validação, segredos e dono vindo do token. */
class HttpContratoIntegrationTest extends IntegrationTestBase {

   private static String json(String nome) {
      return "{\"nome\":\"" + nome + "\"}";
   }

   // ---------- Categoria: validação ----------

   @Test
   void criarCategoria_nomeCom255Caracteres_deveCriar_e256DeveSer400() throws Exception {
      String auth = autenticar("arthur@email.com");

      mvc.perform(post("/categoria").header("Authorization", auth)
            .contentType(MediaType.APPLICATION_JSON).content(json("a".repeat(255))))
         .andExpect(status().isCreated());

      mvc.perform(post("/categoria").header("Authorization", auth)
            .contentType(MediaType.APPLICATION_JSON).content(json("b".repeat(256))))
         .andExpect(status().isBadRequest())
         .andExpect(jsonPath("$.status").value(400));
   }

   @Test
   void atualizarCategoria_nomeEmBranco_deveRetornar400() throws Exception {
      String auth = autenticar("arthur@email.com");
      String id = com.jayway.jsonpath.JsonPath.read(
         mvc.perform(post("/categoria").header("Authorization", auth)
               .contentType(MediaType.APPLICATION_JSON).content(json("Mercado")))
            .andReturn().getResponse().getContentAsString(), "$.idCategoria");

      mvc.perform(put("/categoria/" + id).header("Authorization", auth)
            .contentType(MediaType.APPLICATION_JSON).content(json(" ")))
         .andExpect(status().isBadRequest());
   }

   @Test
   void atualizarCategoria_mesmoNome_deveRetornar409() throws Exception {
      String auth = autenticar("arthur@email.com");
      String id = com.jayway.jsonpath.JsonPath.read(
         mvc.perform(post("/categoria").header("Authorization", auth)
               .contentType(MediaType.APPLICATION_JSON).content(json("Mercado")))
            .andReturn().getResponse().getContentAsString(), "$.idCategoria");

      mvc.perform(put("/categoria/" + id).header("Authorization", auth)
            .contentType(MediaType.APPLICATION_JSON).content(json("Mercado")))
         .andExpect(status().isConflict());
   }

   @Test
   void atualizarCategoria_idMalformado_deveRetornar400() throws Exception {
      String auth = autenticar("arthur@email.com");

      mvc.perform(put("/categoria/nao-e-uuid").header("Authorization", auth)
            .contentType(MediaType.APPLICATION_JSON).content(json("Novo")))
         .andExpect(status().isBadRequest());
   }

   // ---------- Dono vem do token, nunca do corpo ----------

   @Test
   void criarCategoria_idUserNoCorpoDeveSerIgnorado() throws Exception {
      String authA = autenticar("a@email.com");
      autenticar("b@email.com");
      String idDeB = jdbc.queryForObject("SELECT id_user::text FROM users WHERE email = 'b@email.com'", String.class);
      String idDeA = jdbc.queryForObject("SELECT id_user::text FROM users WHERE email = 'a@email.com'", String.class);

      mvc.perform(post("/categoria").header("Authorization", authA)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"nome\":\"Mercado\",\"idUser\":\"" + idDeB + "\"}"))
         .andExpect(status().isCreated())
         .andExpect(jsonPath("$.idUser").value(idDeA));
   }

   // ---------- Auth: segredos e limites ----------

   @Test
   void registrar_senhaDeveSerGravadaComoHashBCrypt() throws Exception {
      registrar("Arthur", "arthur@email.com", "senha12345");

      String hash = jdbc.queryForObject("SELECT senha_hash FROM users WHERE email = 'arthur@email.com'", String.class);

      assertNotEquals("senha12345", hash);
      assertEquals(60, hash.length());
      assertEquals(true, hash.startsWith("$2"));
   }

   @Test
   void registrar_respostaNaoDeveConterSenha() throws Exception {
      mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
            .content("{\"nome\":\"Arthur\",\"email\":\"arthur@email.com\",\"senha\":\"senha12345\"}"))
         .andExpect(status().isCreated())
         .andExpect(content().string(not(containsString("senha12345"))))
         .andExpect(content().string(not(containsString("$2"))));
   }

   @Test
   void registrar_senhaCom72Caracteres_deveFuncionar_e73DeveSer400() throws Exception {
      registrar("Arthur", "a@email.com", "x".repeat(72));
      login("a@email.com", "x".repeat(72));

      mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
            .content("{\"nome\":\"B\",\"email\":\"b@email.com\",\"senha\":\"" + "x".repeat(73) + "\"}"))
         .andExpect(status().isBadRequest());
   }

   @Test
   void login_emailEmCaixaAlta_deveFuncionar() throws Exception {
      registrar("Arthur", "arthur@email.com", "senha12345");

      mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
            .content("{\"email\":\"ARTHUR@Email.com\",\"senha\":\"senha12345\"}"))
         .andExpect(status().isOk());
   }

   @Test
   void login_mensagemDeErroNaoDeveDistinguirUsuarioInexistenteDeSenhaErrada() throws Exception {
      registrar("Arthur", "arthur@email.com", "senha12345");

      String semUsuario = mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
            .content("{\"email\":\"ninguem@email.com\",\"senha\":\"senha12345\"}"))
         .andExpect(status().isUnauthorized()).andReturn().getResponse().getContentAsString();
      String senhaErrada = mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
            .content("{\"email\":\"arthur@email.com\",\"senha\":\"errada12345\"}"))
         .andExpect(status().isUnauthorized()).andReturn().getResponse().getContentAsString();

      assertEquals(
         com.jayway.jsonpath.JsonPath.<String>read(semUsuario, "$.message"),
         com.jayway.jsonpath.JsonPath.<String>read(senhaErrada, "$.message")
      );
   }

   // ---------- Entradas malformadas ----------

   @Test
   void jsonMalformado_deveRetornar400() throws Exception {
      mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content("{ isso nao e json"))
         .andExpect(status().isBadRequest());
   }

   @Test
   void corpoAusente_deveRetornar400() throws Exception {
      mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON))
         .andExpect(status().isBadRequest());
   }

   @Test
   void contentTypeNaoSuportado_deveRetornar415() throws Exception {
      mvc.perform(post("/auth/login").contentType(MediaType.TEXT_PLAIN).content("x"))
         .andExpect(status().isUnsupportedMediaType());
   }

   @Test
   void metodoNaoSuportado_deveRetornar405() throws Exception {
      String auth = autenticar("arthur@email.com");

      mvc.perform(patch("/categoria/" + java.util.UUID.randomUUID()).header("Authorization", auth)
            .contentType(MediaType.APPLICATION_JSON).content(json("x")))
         .andExpect(status().isMethodNotAllowed());
   }

   @Test
   void rotaInexistente_comToken_deveRetornar404() throws Exception {
      String auth = autenticar("arthur@email.com");

      mvc.perform(get("/rota-que-nao-existe").header("Authorization", auth))
         .andExpect(status().isNotFound());
   }

   @Test
   void tokenComPrefixoErrado_deveRetornar401() throws Exception {
      String token = login(autenticarSemPrefixo(), "senha12345");

      mvc.perform(get("/categoria").header("Authorization", "Token " + token))
         .andExpect(status().isUnauthorized());
   }

   private String autenticarSemPrefixo() throws Exception {
      registrar("Arthur", "arthur@email.com", "senha12345");
      return "arthur@email.com";
   }
}
