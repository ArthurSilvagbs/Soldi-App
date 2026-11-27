package com.github.ArthurSilvagbs.SoldiAPI.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.github.ArthurSilvagbs.SoldiAPI.user.Role;
import com.github.ArthurSilvagbs.SoldiAPI.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TokenServiceTest {

   private static final String SECRET = "segredo-somente-de-teste-1234567890";

   private TokenService tokenService;

   @BeforeEach
   void setUp() {
      tokenService = new TokenService();
      ReflectionTestUtils.setField(tokenService, "secret", SECRET);
   }

   @Test
   void generateToken_eValidateToken_deveFazerIdaEVolta() {
      UUID id = UUID.randomUUID();
      User user = new User("Arthur", "arthur@email.com", "hash", Role.USER);
      ReflectionTestUtils.setField(user, "idUser", id);

      String token = tokenService.generateToken(user);
      Optional<JWTUserData> dados = tokenService.validateToken(token);

      assertThat(dados).isPresent();
      assertThat(dados.get().idUsuario()).isEqualTo(id);
      assertThat(dados.get().email()).isEqualTo("arthur@email.com");
      assertThat(dados.get().role()).isEqualTo("USER");
   }

   @Test
   void validateToken_tokenLixo_deveRetornarVazio() {
      assertThat(tokenService.validateToken("isso-nao-e-um-jwt")).isEmpty();
   }

   @Test
   void validateToken_assinadoComOutroSegredo_deveRetornarVazio() {
      String forjado = JWT.create()
         .withClaim("userId", UUID.randomUUID().toString())
         .withClaim("role", "ADMIN")
         .withSubject("hacker@email.com")
         .withExpiresAt(Instant.now().plusSeconds(60))
         .sign(Algorithm.HMAC256("outro-segredo"));

      assertThat(tokenService.validateToken(forjado)).isEmpty();
   }

   @Test
   void validateToken_expirado_deveRetornarVazio() {
      String expirado = JWT.create()
         .withClaim("userId", UUID.randomUUID().toString())
         .withClaim("role", "USER")
         .withSubject("arthur@email.com")
         .withExpiresAt(Instant.now().minusSeconds(10))
         .sign(Algorithm.HMAC256(SECRET));

      assertThat(tokenService.validateToken(expirado)).isEmpty();
   }
}
