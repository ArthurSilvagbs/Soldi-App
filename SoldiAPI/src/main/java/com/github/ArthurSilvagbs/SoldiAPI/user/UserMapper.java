package com.github.ArthurSilvagbs.SoldiAPI.user;

import com.github.ArthurSilvagbs.SoldiAPI.auth.dto.RegisterRequestDTO;
import com.github.ArthurSilvagbs.SoldiAPI.auth.dto.RegisterResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

   public RegisterResponseDTO userParaRegisterResponse(User user) {
      return new RegisterResponseDTO(user.getNome(), user.getEmail());
   }

}
