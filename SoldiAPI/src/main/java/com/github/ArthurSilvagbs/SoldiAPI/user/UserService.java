package com.github.ArthurSilvagbs.SoldiAPI.user;

import com.github.ArthurSilvagbs.SoldiAPI.auth.dto.RegisterRequestDTO;
import com.github.ArthurSilvagbs.SoldiAPI.auth.dto.RegisterResponseDTO;
import com.github.ArthurSilvagbs.SoldiAPI.exceptions.EmailJaExistenteException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

   private final UserRepository repository;
   private final UserMapper mapper;
   private final PasswordEncoder encoder;

   @Transactional
   public RegisterResponseDTO register(RegisterRequestDTO dto) {

      String email = dto.email().trim().toLowerCase();

      if (repository.existsByEmail(email)) {
         throw new EmailJaExistenteException("O email informado já existe no cadastro do sistema.");
      }

      User user = new User(
         dto.nome(),
         email,
         encoder.encode(dto.senha()),
         Role.USER
      );

      return mapper.userParaRegisterResponse(repository.save(user));
   }
}
