package com.github.ArthurSilvagbs.SoldiAPI.auth;

import com.github.ArthurSilvagbs.SoldiAPI.auth.dto.LoginRequestDTO;
import com.github.ArthurSilvagbs.SoldiAPI.auth.dto.LoginResponseDTO;
import com.github.ArthurSilvagbs.SoldiAPI.auth.dto.RegisterRequestDTO;
import com.github.ArthurSilvagbs.SoldiAPI.auth.dto.RegisterResponseDTO;
import com.github.ArthurSilvagbs.SoldiAPI.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

   private final UserService userService;
   private final AuthService authService;

   @PostMapping("/register")
   public ResponseEntity<RegisterResponseDTO> register(@Valid @RequestBody RegisterRequestDTO dto) {
      return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(dto));
   }

   @PostMapping("/login")
   public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
      return ResponseEntity.status(HttpStatus.OK).body(authService.login(dto));
   }
}
