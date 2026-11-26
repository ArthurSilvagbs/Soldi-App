package com.github.ArthurSilvagbs.SoldiAPI.categoria;

import com.github.ArthurSilvagbs.SoldiAPI.security.TokenService;
import com.github.ArthurSilvagbs.SoldiAPI.user.Role;
import com.github.ArthurSilvagbs.SoldiAPI.user.User;
import com.github.ArthurSilvagbs.SoldiAPI.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.utility.TestcontainersConfiguration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "jwt.secret=chave-usada-somente-nos-testes-123456")
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@RequiredArgsConstructor
class CategoriaControllerTest {

   MockMvc mvc;
   CategoriaRepository categoriaRepository;
   UserRepository userRepository;
   TokenService tokenService;

   @Test
   void criarCategoria() {

      categoriaRepository.deleteAll();
      userRepository.deleteAll();

      User user = userRepository.save(
         new User("Arthur", "arthur@email.com", "senhaTeste", Role.USER)
      );
      String token = tokenService.generateToken(user);

      mvc.perform(post("/categoria")
         .header("Authorization", "Bearer " + token)
         .contentType(MediaType.APPLICATION_JSON)
         .content("""
            {"nome":"Mercado"}
            """))
         .andExpect(status().isCreated());
   }
}