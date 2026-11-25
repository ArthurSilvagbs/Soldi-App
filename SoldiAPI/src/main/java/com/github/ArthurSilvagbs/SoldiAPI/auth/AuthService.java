package com.github.ArthurSilvagbs.SoldiAPI.auth;

import com.github.ArthurSilvagbs.SoldiAPI.auth.dto.LoginRequestDTO;
import com.github.ArthurSilvagbs.SoldiAPI.auth.dto.LoginResponseDTO;
import com.github.ArthurSilvagbs.SoldiAPI.security.TokenService;
import com.github.ArthurSilvagbs.SoldiAPI.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

   private final AuthenticationManager authManager;
   private final TokenService tokenService;

   @Transactional
   public LoginResponseDTO login(LoginRequestDTO dto) {

      UsernamePasswordAuthenticationToken userAndPass = new UsernamePasswordAuthenticationToken(
         dto.email().trim().toLowerCase(),
         dto.senha()
      );

      Authentication authentication = authManager.authenticate(userAndPass);

      User user = (User) authentication.getPrincipal();

      String token = tokenService.generateToken(user);

      return new LoginResponseDTO(token);

   }

}
