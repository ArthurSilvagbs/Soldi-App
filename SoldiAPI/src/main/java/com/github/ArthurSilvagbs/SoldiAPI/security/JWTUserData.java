package com.github.ArthurSilvagbs.SoldiAPI.security;

import lombok.Builder;

import java.util.UUID;

@Builder
public record JWTUserData(
   UUID idUsuario,
   String email,
   String role
) {
}
