package com.github.ArthurSilvagbs.SoldiAPI.exceptions;

import com.github.ArthurSilvagbs.SoldiAPI.exceptions.dtos.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalHandlerException {

   @ExceptionHandler({EmailJaExistenteException.class, EntidadeDuplicadaException.class})
   public ResponseEntity<ErrorResponse> handlerDuplicado(
      RuntimeException e,
      HttpServletRequest request
   ) {
      return resposta(HttpStatus.CONFLICT, e.getMessage(), request);
   }

   @ExceptionHandler(DataIntegrityViolationException.class)
   public ResponseEntity<ErrorResponse> handlerIntegridadeDeDados(
      DataIntegrityViolationException e,
      HttpServletRequest request
   ) {
      log.warn("Violação de integridade em {}: {}", request.getRequestURI(), e.getMostSpecificCause().getMessage());
      return resposta(HttpStatus.CONFLICT, "O registro já existe ou viola uma restrição de dados.", request);
   }

   @ExceptionHandler(MethodArgumentNotValidException.class)
   public ResponseEntity<ErrorResponse> handlerValidacaoBody(
      MethodArgumentNotValidException e,
      HttpServletRequest request
   ) {
      String mensagem = e.getBindingResult().getFieldErrors().stream()
         .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
         .collect(Collectors.joining("; "));
      return resposta(HttpStatus.BAD_REQUEST, mensagem, request);
   }

   @ExceptionHandler(HandlerMethodValidationException.class)
   public ResponseEntity<ErrorResponse> handlerValidacaoParametros(
      HandlerMethodValidationException e,
      HttpServletRequest request
   ) {
      return resposta(HttpStatus.BAD_REQUEST, "Parâmetros da requisição inválidos.", request);
   }

   @ExceptionHandler(ConstraintViolationException.class)
   public ResponseEntity<ErrorResponse> handlerConstraintViolation(
      ConstraintViolationException e,
      HttpServletRequest request
   ) {
      String mensagem = e.getConstraintViolations().stream()
         .map(v -> v.getPropertyPath() + ": " + v.getMessage())
         .collect(Collectors.joining("; "));
      return resposta(HttpStatus.BAD_REQUEST, mensagem, request);
   }

   @ExceptionHandler(HttpMessageNotReadableException.class)
   public ResponseEntity<ErrorResponse> handlerCorpoIlegivel(
      HttpMessageNotReadableException e,
      HttpServletRequest request
   ) {
      return resposta(HttpStatus.BAD_REQUEST, "Corpo da requisição ausente, malformado ou com tipo inválido.", request);
   }

   @ExceptionHandler(MethodArgumentTypeMismatchException.class)
   public ResponseEntity<ErrorResponse> handlerTipoInvalido(
      MethodArgumentTypeMismatchException e,
      HttpServletRequest request
   ) {
      return resposta(HttpStatus.BAD_REQUEST, "Valor inválido para o parâmetro '" + e.getName() + "'.", request);
   }

   @ExceptionHandler(MissingServletRequestParameterException.class)
   public ResponseEntity<ErrorResponse> handlerParametroAusente(
      MissingServletRequestParameterException e,
      HttpServletRequest request
   ) {
      return resposta(HttpStatus.BAD_REQUEST, "O parâmetro '" + e.getParameterName() + "' é obrigatório.", request);
   }

   @ExceptionHandler(BadCredentialsException.class)
   public ResponseEntity<ErrorResponse> handlerCredenciaisInvalidas(
      BadCredentialsException e,
      HttpServletRequest request
   ) {
      return resposta(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos.", request);
   }

   @ExceptionHandler(AuthenticationException.class)
   public ResponseEntity<ErrorResponse> handlerAutenticacao(
      AuthenticationException e,
      HttpServletRequest request
   ) {
      return resposta(HttpStatus.UNAUTHORIZED, "Falha na autenticação.", request);
   }

   @ExceptionHandler(AccessDeniedException.class)
   public ResponseEntity<ErrorResponse> handlerAcessoNegado(
      AccessDeniedException e,
      HttpServletRequest request
   ) {
      return resposta(HttpStatus.FORBIDDEN, "Acesso negado.", request);
   }

   @ExceptionHandler({EntityNotFoundException.class, NoSuchElementException.class})
   public ResponseEntity<ErrorResponse> handlerNaoEncontrado(
      RuntimeException e,
      HttpServletRequest request
   ) {
      return resposta(HttpStatus.NOT_FOUND, "Recurso não encontrado.", request);
   }

   @ExceptionHandler(NoResourceFoundException.class)
   public ResponseEntity<ErrorResponse> handlerRotaInexistente(
      NoResourceFoundException e,
      HttpServletRequest request
   ) {
      return resposta(HttpStatus.NOT_FOUND, "Rota não encontrada.", request);
   }

   @ExceptionHandler(EntidadeNaoEncontradaException.class)
   public ResponseEntity<ErrorResponse> handlerEntidadeNaoEcontrada(
      EntidadeNaoEncontradaException e,
      HttpServletRequest request
   ) {
      return resposta(HttpStatus.NOT_FOUND, e.getMessage(), request);
   }

   @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
   public ResponseEntity<ErrorResponse> handlerMetodoNaoSuportado(
      HttpRequestMethodNotSupportedException e,
      HttpServletRequest request
   ) {
      return resposta(HttpStatus.METHOD_NOT_ALLOWED, "Método HTTP '" + e.getMethod() + "' não suportado nesta rota.", request);
   }

   @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
   public ResponseEntity<ErrorResponse> handlerMediaTypeNaoSuportado(
      HttpMediaTypeNotSupportedException e,
      HttpServletRequest request
   ) {
      return resposta(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Tipo de conteúdo não suportado. Use application/json.", request);
   }

   @ExceptionHandler(Exception.class)
   public ResponseEntity<ErrorResponse> handlerGenerico(
      Exception e,
      HttpServletRequest request
   ) {
      log.error("Erro inesperado em {}", request.getRequestURI(), e);
      return resposta(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor.", request);
   }



   private ResponseEntity<ErrorResponse> resposta(
      HttpStatus status,
      String mensagem,
      HttpServletRequest request
   ) {
      ErrorResponse body = new ErrorResponse(
         status.value(),
         status.name(),
         mensagem,
         request.getRequestURI(),
         LocalDateTime.now()
      );
      return ResponseEntity.status(status).body(body);
   }
}
