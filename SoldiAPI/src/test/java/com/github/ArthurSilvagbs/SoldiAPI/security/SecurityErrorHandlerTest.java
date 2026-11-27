package com.github.ArthurSilvagbs.SoldiAPI.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityErrorHandlerTest {

   private final ObjectMapper objectMapper = JsonMapper.builder().build();
   private final SecurityErrorHandler handler = new SecurityErrorHandler(objectMapper);

   @Test
   void commence_deveResponder401EmJson() throws Exception {
      MockHttpServletRequest request = new MockHttpServletRequest("GET", "/categoria");
      MockHttpServletResponse response = new MockHttpServletResponse();

      handler.commence(request, response, new BadCredentialsException("sem token"));

      assertThat(response.getStatus()).isEqualTo(401);
      assertThat(response.getContentType()).startsWith("application/json");
      assertThat(response.getContentAsString())
         .contains("\"status\":401")
         .contains("UNAUTHORIZED")
         .contains("/categoria");
   }

   @Test
   void handle_deveResponder403EmJson() throws Exception {
      MockHttpServletRequest request = new MockHttpServletRequest("GET", "/admin");
      MockHttpServletResponse response = new MockHttpServletResponse();

      handler.handle(request, response, new AccessDeniedException("negado"));

      assertThat(response.getStatus()).isEqualTo(403);
      assertThat(response.getContentAsString())
         .contains("\"status\":403")
         .contains("FORBIDDEN")
         .contains("/admin");
   }
}
