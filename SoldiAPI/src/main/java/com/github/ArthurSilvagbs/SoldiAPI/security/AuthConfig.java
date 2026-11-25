package com.github.ArthurSilvagbs.SoldiAPI.security;

import com.github.ArthurSilvagbs.SoldiAPI.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthConfig implements UserDetailsService {

   private final UserRepository repository;

   @Override
   public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      return repository.findByEmail(username)
         .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));
   }
}
