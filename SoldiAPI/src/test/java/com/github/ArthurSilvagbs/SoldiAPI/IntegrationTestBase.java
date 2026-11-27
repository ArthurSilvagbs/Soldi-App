package com.github.ArthurSilvagbs.SoldiAPI;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Base dos testes de integração: sobe a aplicação inteira contra um Postgres real
 * (Testcontainers) e limpa as tabelas antes de cada teste.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
public abstract class IntegrationTestBase {

   @Autowired
   protected MockMvc mvc;

   @Autowired
   protected JdbcTemplate jdbc;

   @BeforeEach
   void limparBanco() {
      jdbc.execute("DELETE FROM categoria");
      jdbc.execute("DELETE FROM users");
   }

   protected void registrar(String nome, String email, String senha) throws Exception {
      mvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
               {"nome":"%s","email":"%s","senha":"%s"}
               """.formatted(nome, email, senha)))
         .andExpect(status().isCreated());
   }

   protected String login(String email, String senha) throws Exception {
      String body = mvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
               {"email":"%s","senha":"%s"}
               """.formatted(email, senha)))
         .andExpect(status().isOk())
         .andReturn().getResponse().getContentAsString();
      return JsonPath.read(body, "$.token");
   }

   /** Registra o usuário, faz login e devolve o header Authorization pronto. */
   protected String autenticar(String email) throws Exception {
      registrar("Usuario Teste", email, "senha12345");
      return "Bearer " + login(email, "senha12345");
   }
}
