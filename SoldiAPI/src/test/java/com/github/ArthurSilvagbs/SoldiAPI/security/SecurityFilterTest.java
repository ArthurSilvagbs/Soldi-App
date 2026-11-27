package com.github.ArthurSilvagbs.SoldiAPI.security;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityFilterTest {

   @Mock
   private TokenService tokenService;

   @Mock
   private FilterChain chain;

   @InjectMocks
   private SecurityFilter filter;

   private final MockHttpServletRequest request = new MockHttpServletRequest();
   private final MockHttpServletResponse response = new MockHttpServletResponse();

   @AfterEach
   void limparContexto() {
      SecurityContextHolder.clearContext();
   }

   @Test
   void semHeader_naoDeveAutenticarMasSegueACadeia() throws Exception {
      filter.doFilter(request, response, chain);

      assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
      verify(chain).doFilter(request, response);
   }

   @Test
   void headerSemBearer_naoDeveAutenticar() throws Exception {
      request.addHeader("Authorization", "Basic abc");

      filter.doFilter(request, response, chain);

      assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
      verify(chain).doFilter(request, response);
   }

   @Test
   void tokenValido_deveAutenticarComRole() throws Exception {
      JWTUserData dados = new JWTUserData(UUID.randomUUID(), "arthur@email.com", "USER");
      request.addHeader("Authorization", "Bearer token-ok");
      when(tokenService.validateToken("token-ok")).thenReturn(Optional.of(dados));

      filter.doFilter(request, response, chain);

      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      assertThat(auth).isNotNull();
      assertThat(auth.getPrincipal()).isEqualTo(dados);
      assertThat(auth.getAuthorities()).extracting(Object::toString).containsExactly("ROLE_USER");
      verify(chain).doFilter(request, response);
   }

   @Test
   void tokenValidoSemRole_deveAutenticarSemAuthorities() throws Exception {
      JWTUserData dados = new JWTUserData(UUID.randomUUID(), "arthur@email.com", null);
      request.addHeader("Authorization", "Bearer token-ok");
      when(tokenService.validateToken("token-ok")).thenReturn(Optional.of(dados));

      filter.doFilter(request, response, chain);

      assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities()).isEmpty();
   }

   @Test
   void tokenInvalido_naoDeveAutenticar() throws Exception {
      request.addHeader("Authorization", "Bearer lixo");
      when(tokenService.validateToken("lixo")).thenReturn(Optional.empty());

      filter.doFilter(request, response, chain);

      assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
      verify(chain).doFilter(request, response);
   }
}
