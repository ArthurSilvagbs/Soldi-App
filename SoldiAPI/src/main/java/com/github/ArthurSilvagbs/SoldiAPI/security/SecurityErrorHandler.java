package com.github.ArthurSilvagbs.SoldiAPI.security;

import com.github.ArthurSilvagbs.SoldiAPI.exceptions.dtos.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Trata os erros que acontecem na cadeia de filtros do Spring Security,
 * antes de a requisição chegar a um controller. Por isso o GlobalHandlerException
 * não alcança esses casos: aqui não existe ResponseEntity, então o JSON é
 * escrito direto no HttpServletResponse.
 */
@Component
@RequiredArgsConstructor
public class SecurityErrorHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

   private final ObjectMapper objectMapper;

   // 401: rota protegida acessada sem token, ou com token inválido/expirado.
   @Override
   public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException
   ) throws IOException {
      escrever(request, response, HttpStatus.UNAUTHORIZED, "Autenticação necessária ou token inválido/expirado.");
   }

   // 403: usuário autenticado, mas sem permissão para a rota.
   @Override
   public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException
   ) throws IOException {
      escrever(request, response, HttpStatus.FORBIDDEN, "Acesso negado.");
   }

   private void escrever(
      HttpServletRequest request,
      HttpServletResponse response,
      HttpStatus status,
      String mensagem
   ) throws IOException {
      ErrorResponse body = new ErrorResponse(
         status.value(),
         status.name(),
         mensagem,
         request.getRequestURI(),
         LocalDateTime.now()
      );

      response.setStatus(status.value());
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.setCharacterEncoding("UTF-8");
      objectMapper.writeValue(response.getOutputStream(), body);
   }
}
