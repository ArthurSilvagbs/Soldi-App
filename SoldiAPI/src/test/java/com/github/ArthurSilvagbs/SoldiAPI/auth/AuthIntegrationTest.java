package com.github.ArthurSilvagbs.SoldiAPI.auth;

import com.github.ArthurSilvagbs.SoldiAPI.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.emptyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthIntegrationTest extends IntegrationTestBase {

   @Test
   void fluxoFeliz_registrarLogarEAcessarRotaProtegida() throws Exception {
      mvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
               {"nome":"Arthur","email":"Arthur@Email.com","senha":"senha12345"}
               """))
         .andExpect(status().isCreated())
         .andExpect(jsonPath("$.nome").value("Arthur"))
         .andExpect(jsonPath("$.email").value("arthur@email.com"))
         .andExpect(jsonPath("$.senha").doesNotExist())
         .andExpect(jsonPath("$.senhaHash").doesNotExist());

      String token = login("arthur@email.com", "senha12345");

      mvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
               {"email":"arthur@email.com","senha":"senha12345"}
               """))
         .andExpect(status().isOk())
         .andExpect(jsonPath("$.token").value(not(emptyString())));

      // Com o token a rota protegida responde (404 = autenticado, mas ainda sem categorias).
      mvc.perform(get("/categoria").header("Authorization", "Bearer " + token))
         .andExpect(status().isNotFound());
   }

   @Test
   void registrar_emailDuplicado_deveRetornar409() throws Exception {
      registrar("Arthur", "arthur@email.com", "senha12345");

      mvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
               {"nome":"Outro","email":"ARTHUR@email.com","senha":"senha12345"}
               """))
         .andExpect(status().isConflict());
   }

   @Test
   void registrar_dadosInvalidos_deveRetornar400() throws Exception {
      mvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
               {"nome":"","email":"nao-e-email","senha":"123"}
               """))
         .andExpect(status().isBadRequest());
   }

   @Test
   void login_senhaErrada_deveRetornar401() throws Exception {
      registrar("Arthur", "arthur@email.com", "senha12345");

      mvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
               {"email":"arthur@email.com","senha":"senha-errada"}
               """))
         .andExpect(status().isUnauthorized());
   }

   @Test
   void login_usuarioInexistente_deveRetornar401() throws Exception {
      mvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
               {"email":"ninguem@email.com","senha":"senha12345"}
               """))
         .andExpect(status().isUnauthorized());
   }

   @Test
   void rotaProtegida_semToken_deveRetornar401() throws Exception {
      mvc.perform(get("/categoria"))
         .andExpect(status().isUnauthorized());
   }

   @Test
   void rotaProtegida_comTokenInvalido_deveRetornar401() throws Exception {
      mvc.perform(get("/categoria").header("Authorization", "Bearer token.invalido.aqui"))
         .andExpect(status().isUnauthorized());
   }
}
