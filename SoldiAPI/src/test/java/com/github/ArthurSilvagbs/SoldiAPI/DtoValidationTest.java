package com.github.ArthurSilvagbs.SoldiAPI;

import com.github.ArthurSilvagbs.SoldiAPI.auth.dto.LoginRequestDTO;
import com.github.ArthurSilvagbs.SoldiAPI.auth.dto.RegisterRequestDTO;
import com.github.ArthurSilvagbs.SoldiAPI.categoria.dtos.CategoriaCreateDTO;
import com.github.ArthurSilvagbs.SoldiAPI.categoria.dtos.CategoriaUpdateDTO;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/** Testa as regras de Bean Validation dos DTOs, incluindo os limites exatos. */
class DtoValidationTest {

   private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

   private static String repetir(int n) {
      return "a".repeat(n);
   }

   // ---------- Categoria ----------

   @ParameterizedTest
   @NullAndEmptySource
   @ValueSource(strings = {" ", "   "})
   void categoriaCreate_nomeVazioOuEmBranco_deveSerInvalido(String nome) {
      assertThat(validator.validate(new CategoriaCreateDTO(nome))).isNotEmpty();
   }

   @Test
   void categoriaCreate_limiteDe255Caracteres() {
      assertThat(validator.validate(new CategoriaCreateDTO(repetir(255)))).isEmpty();
      assertThat(validator.validate(new CategoriaCreateDTO(repetir(256)))).isNotEmpty();
   }

   @ParameterizedTest
   @NullAndEmptySource
   @ValueSource(strings = {" "})
   void categoriaUpdate_nomeVazioOuEmBranco_deveSerInvalido(String nome) {
      assertThat(validator.validate(new CategoriaUpdateDTO(nome))).isNotEmpty();
   }

   @Test
   void categoriaUpdate_limiteDe255Caracteres() {
      assertThat(validator.validate(new CategoriaUpdateDTO(repetir(255)))).isEmpty();
      assertThat(validator.validate(new CategoriaUpdateDTO(repetir(256)))).isNotEmpty();
   }

   // ---------- Register ----------

   @Test
   void register_dadosValidos_deveSerValido() {
      assertThat(validator.validate(new RegisterRequestDTO("Arthur", "a@email.com", "senha123"))).isEmpty();
   }

   @ParameterizedTest
   @ValueSource(strings = {"sem-arroba", "@sem-usuario.com", "a@", "a b@email.com"})
   void register_emailInvalido_deveSerInvalido(String email) {
      assertThat(validator.validate(new RegisterRequestDTO("Arthur", email, "senha123"))).isNotEmpty();
   }

   @Test
   void register_limiteDoEmail_75Caracteres() {
      // parte local curta e domínio em labels <= 63, para só o @Size(max = 75) ser testado
      String ok = "a@" + "d".repeat(30) + "." + "d".repeat(30) + "." + "d".repeat(7) + ".com";
      assertThat(ok).hasSize(75);
      assertThat(validator.validate(new RegisterRequestDTO("Arthur", ok, "senha123"))).isEmpty();
      String estourado = "a@" + "d".repeat(31) + "." + "d".repeat(30) + "." + "d".repeat(7) + ".com";
      assertThat(estourado).hasSize(76);
      assertThat(validator.validate(new RegisterRequestDTO("Arthur", estourado, "senha123"))).isNotEmpty();
   }

   @Test
   void register_limitesDaSenha_8a72() {
      assertThat(validator.validate(new RegisterRequestDTO("Arthur", "a@email.com", repetir(7)))).isNotEmpty();
      assertThat(validator.validate(new RegisterRequestDTO("Arthur", "a@email.com", repetir(8)))).isEmpty();
      assertThat(validator.validate(new RegisterRequestDTO("Arthur", "a@email.com", repetir(72)))).isEmpty();
      assertThat(validator.validate(new RegisterRequestDTO("Arthur", "a@email.com", repetir(73)))).isNotEmpty();
   }

   @Test
   void register_nomeObrigatorioELimite255() {
      assertThat(validator.validate(new RegisterRequestDTO(" ", "a@email.com", "senha123"))).isNotEmpty();
      assertThat(validator.validate(new RegisterRequestDTO(null, "a@email.com", "senha123"))).isNotEmpty();
      assertThat(validator.validate(new RegisterRequestDTO(repetir(255), "a@email.com", "senha123"))).isEmpty();
      assertThat(validator.validate(new RegisterRequestDTO(repetir(256), "a@email.com", "senha123"))).isNotEmpty();
   }

   // ---------- Login ----------

   @Test
   void login_camposObrigatoriosEEmailValido() {
      assertThat(validator.validate(new LoginRequestDTO("a@email.com", "x"))).isEmpty();
      assertThat(validator.validate(new LoginRequestDTO(null, "x"))).isNotEmpty();
      assertThat(validator.validate(new LoginRequestDTO("invalido", "x"))).isNotEmpty();
      assertThat(validator.validate(new LoginRequestDTO("a@email.com", " "))).isNotEmpty();
   }
}
