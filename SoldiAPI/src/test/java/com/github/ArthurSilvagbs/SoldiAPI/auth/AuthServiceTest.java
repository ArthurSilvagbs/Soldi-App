package com.github.ArthurSilvagbs.SoldiAPI.auth;

import com.github.ArthurSilvagbs.SoldiAPI.auth.dto.LoginRequestDTO;
import com.github.ArthurSilvagbs.SoldiAPI.auth.dto.LoginResponseDTO;
import com.github.ArthurSilvagbs.SoldiAPI.security.TokenService;
import com.github.ArthurSilvagbs.SoldiAPI.user.Role;
import com.github.ArthurSilvagbs.SoldiAPI.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

   @Mock
   private AuthenticationManager authManager;

   @Mock
   private TokenService tokenService;

   @InjectMocks
   private AuthService service;

   @Test
   void login_deveAutenticarComEmailNormalizadoERetornarToken() {
      User user = new User("Arthur", "arthur@email.com", "hash", Role.USER);
      Authentication authentication = mock(Authentication.class);
      when(authentication.getPrincipal()).thenReturn(user);
      when(authManager.authenticate(any())).thenReturn(authentication);
      when(tokenService.generateToken(user)).thenReturn("token-jwt");

      LoginResponseDTO response = service.login(new LoginRequestDTO("  Arthur@Email.COM ", "senha12345"));

      ArgumentCaptor<Authentication> captor = ArgumentCaptor.forClass(Authentication.class);
      verify(authManager).authenticate(captor.capture());
      assertThat(captor.getValue()).isInstanceOf(UsernamePasswordAuthenticationToken.class);
      assertThat(captor.getValue().getPrincipal()).isEqualTo("arthur@email.com");
      assertThat(response.token()).isEqualTo("token-jwt");
   }

   @Test
   void login_credenciaisInvalidas_devePropagarExcecaoESemGerarToken() {
      when(authManager.authenticate(any())).thenThrow(new BadCredentialsException("ruim"));

      assertThatThrownBy(() -> service.login(new LoginRequestDTO("arthur@email.com", "errada")))
         .isInstanceOf(BadCredentialsException.class);

      verify(tokenService, never()).generateToken(any());
   }
}
