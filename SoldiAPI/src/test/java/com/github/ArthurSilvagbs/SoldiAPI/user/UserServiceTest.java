package com.github.ArthurSilvagbs.SoldiAPI.user;

import com.github.ArthurSilvagbs.SoldiAPI.auth.dto.RegisterRequestDTO;
import com.github.ArthurSilvagbs.SoldiAPI.auth.dto.RegisterResponseDTO;
import com.github.ArthurSilvagbs.SoldiAPI.exceptions.EmailJaExistenteException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

   @Mock
   private UserRepository repository;

   @Mock
   private PasswordEncoder encoder;

   @Spy
   private UserMapper mapper = new UserMapper();

   @InjectMocks
   private UserService service;

   @Test
   void register_deveNormalizarEmail_codificarSenhaEAtribuirRoleUser() {
      when(repository.existsByEmail("arthur@email.com")).thenReturn(false);
      when(encoder.encode("senha12345")).thenReturn("hash");
      when(repository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

      RegisterResponseDTO response = service.register(
         new RegisterRequestDTO("Arthur", "  Arthur@Email.COM ", "senha12345")
      );

      ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
      verify(repository).save(captor.capture());
      User salvo = captor.getValue();

      assertThat(salvo.getEmail()).isEqualTo("arthur@email.com");
      assertThat(salvo.getSenhaHash()).isEqualTo("hash");
      assertThat(salvo.getRoles()).isEqualTo(Role.USER);
      assertThat(response.email()).isEqualTo("arthur@email.com");
      assertThat(response.nome()).isEqualTo("Arthur");
   }

   @Test
   void register_emailJaCadastrado_deveLancarExcecaoESemSalvar() {
      when(repository.existsByEmail("arthur@email.com")).thenReturn(true);

      assertThatThrownBy(() -> service.register(
         new RegisterRequestDTO("Arthur", "arthur@email.com", "senha12345")
      )).isInstanceOf(EmailJaExistenteException.class);

      verify(repository, never()).save(any());
   }

   @Test
   void verificarSeIdUserJaCadastrado_deveDelegarParaRepository() {
      UUID id = UUID.randomUUID();
      when(repository.existsById(id)).thenReturn(true);

      assertThat(service.verificarSeIdUserJaCadastrado(id)).isTrue();
   }
}
