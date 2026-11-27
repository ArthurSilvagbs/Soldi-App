package com.github.ArthurSilvagbs.SoldiAPI.exceptions;

import com.github.ArthurSilvagbs.SoldiAPI.exceptions.dtos.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.NoSuchElementException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class GlobalHandlerExceptionTest {

   private final GlobalHandlerException handler = new GlobalHandlerException();
   private final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/rota");

   private void assertResposta(ResponseEntity<ErrorResponse> resp, HttpStatus status) {
      assertThat(resp.getStatusCode()).isEqualTo(status);
      assertThat(resp.getBody()).isNotNull();
      assertThat(resp.getBody().status()).isEqualTo(status.value());
      assertThat(resp.getBody().error()).isEqualTo(status.name());
      assertThat(resp.getBody().path()).isEqualTo("/rota");
      assertThat(resp.getBody().timestamp()).isNotNull();
   }

   @Test
   void duplicado_deveRetornar409ComMensagem() {
      ResponseEntity<ErrorResponse> resp = handler.handlerDuplicado(new EntidadeDuplicadaException("já existe"), request);

      assertResposta(resp, HttpStatus.CONFLICT);
      assertThat(resp.getBody().message()).isEqualTo("já existe");
   }

   @Test
   void emailJaExistente_deveRetornar409() {
      assertResposta(handler.handlerDuplicado(new EmailJaExistenteException("email"), request), HttpStatus.CONFLICT);
   }

   @Test
   void integridadeDeDados_deveRetornar409() {
      var e = new DataIntegrityViolationException("erro", new RuntimeException("causa"));

      assertResposta(handler.handlerIntegridadeDeDados(e, request), HttpStatus.CONFLICT);
   }

   @Test
   void validacaoBody_deveListarCamposInvalidos() throws Exception {
      BeanPropertyBindingResult binding = new BeanPropertyBindingResult(new Object(), "dto");
      binding.addError(new FieldError("dto", "nome", "O nome é obrigatório."));
      MethodParameter parametro = new MethodParameter(Object.class.getMethod("toString"), -1);

      var resp = handler.handlerValidacaoBody(new MethodArgumentNotValidException(parametro, binding), request);

      assertResposta(resp, HttpStatus.BAD_REQUEST);
      assertThat(resp.getBody().message()).isEqualTo("nome: O nome é obrigatório.");
   }

   @Test
   void validacaoParametros_deveRetornar400() {
      var resp = handler.handlerValidacaoParametros(mock(HandlerMethodValidationException.class), request);

      assertResposta(resp, HttpStatus.BAD_REQUEST);
   }

   @Test
   void constraintViolation_deveRetornar400() {
      var resp = handler.handlerConstraintViolation(new ConstraintViolationException(Set.of()), request);

      assertResposta(resp, HttpStatus.BAD_REQUEST);
   }

   @Test
   void corpoIlegivel_deveRetornar400() {
      var e = new HttpMessageNotReadableException("ruim", new MockHttpInputMessage(new byte[0]));

      assertResposta(handler.handlerCorpoIlegivel(e, request), HttpStatus.BAD_REQUEST);
   }

   @Test
   void tipoInvalido_deveCitarNomeDoParametro() {
      var e = new MethodArgumentTypeMismatchException("x", Integer.class, "id", null, null);

      var resp = handler.handlerTipoInvalido(e, request);

      assertResposta(resp, HttpStatus.BAD_REQUEST);
      assertThat(resp.getBody().message()).contains("id");
   }

   @Test
   void parametroAusente_deveCitarNomeDoParametro() {
      var e = new MissingServletRequestParameterException("page", "int");

      var resp = handler.handlerParametroAusente(e, request);

      assertResposta(resp, HttpStatus.BAD_REQUEST);
      assertThat(resp.getBody().message()).contains("page");
   }

   @Test
   void credenciaisInvalidas_deveRetornar401SemVazarDetalhes() {
      var resp = handler.handlerCredenciaisInvalidas(new BadCredentialsException("usuario nao existe"), request);

      assertResposta(resp, HttpStatus.UNAUTHORIZED);
      assertThat(resp.getBody().message()).isEqualTo("E-mail ou senha inválidos.");
   }

   @Test
   void autenticacao_deveRetornar401() {
      assertResposta(handler.handlerAutenticacao(new InsufficientAuthenticationException("x"), request), HttpStatus.UNAUTHORIZED);
   }

   @Test
   void acessoNegado_deveRetornar403() {
      assertResposta(handler.handlerAcessoNegado(new AccessDeniedException("x"), request), HttpStatus.FORBIDDEN);
   }

   @Test
   void naoEncontrado_deveRetornar404() {
      assertResposta(handler.handlerNaoEncontrado(new EntityNotFoundException(), request), HttpStatus.NOT_FOUND);
      assertResposta(handler.handlerNaoEncontrado(new NoSuchElementException(), request), HttpStatus.NOT_FOUND);
   }

   @Test
   void rotaInexistente_deveRetornar404() {
      assertResposta(handler.handlerRotaInexistente(mock(NoResourceFoundException.class), request), HttpStatus.NOT_FOUND);
   }

   @Test
   void entidadeNaoEncontrada_deveRetornar404ComMensagem() {
      var resp = handler.handlerEntidadeNaoEcontrada(new EntidadeNaoEncontradaException("sumiu"), request);

      assertResposta(resp, HttpStatus.NOT_FOUND);
      assertThat(resp.getBody().message()).isEqualTo("sumiu");
   }

   @Test
   void metodoNaoSuportado_deveRetornar405() {
      var resp = handler.handlerMetodoNaoSuportado(new HttpRequestMethodNotSupportedException("PATCH"), request);

      assertResposta(resp, HttpStatus.METHOD_NOT_ALLOWED);
      assertThat(resp.getBody().message()).contains("PATCH");
   }

   @Test
   void mediaTypeNaoSuportado_deveRetornar415() {
      var resp = handler.handlerMediaTypeNaoSuportado(new HttpMediaTypeNotSupportedException("text/plain"), request);

      assertResposta(resp, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
   }

   @Test
   void generico_deveRetornar500SemVazarDetalhes() {
      var resp = handler.handlerGenerico(new RuntimeException("senha do banco = 123"), request);

      assertResposta(resp, HttpStatus.INTERNAL_SERVER_ERROR);
      assertThat(resp.getBody().message()).isEqualTo("Erro interno do servidor.");
   }
}
