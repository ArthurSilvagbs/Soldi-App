package com.github.ArthurSilvagbs.SoldiAPI.security;

import com.github.ArthurSilvagbs.SoldiAPI.user.Role;
import com.github.ArthurSilvagbs.SoldiAPI.user.User;
import com.github.ArthurSilvagbs.SoldiAPI.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthConfigTest {

   @Mock
   private UserRepository repository;

   @InjectMocks
   private AuthConfig authConfig;

   @Test
   void loadUserByUsername_existente_deveRetornarUsuario() {
      User user = new User("Arthur", "arthur@email.com", "hash", Role.USER);
      when(repository.findByEmail("arthur@email.com")).thenReturn(Optional.of(user));

      UserDetails details = authConfig.loadUserByUsername("arthur@email.com");

      assertThat(details.getUsername()).isEqualTo("arthur@email.com");
   }

   @Test
   void loadUserByUsername_inexistente_deveLancarExcecao() {
      when(repository.findByEmail("nao@existe.com")).thenReturn(Optional.empty());

      assertThatThrownBy(() -> authConfig.loadUserByUsername("nao@existe.com"))
         .isInstanceOf(UsernameNotFoundException.class);
   }
}
