package com.github.ArthurSilvagbs.SoldiAPI.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.ArthurSilvagbs.SoldiAPI.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
public class TokenService {

   private final Integer time = 180;

   @Value("${JWT_SECRET}")
   String secret;

   public String generateToken(User user) {
      Algorithm algorithm = Algorithm.HMAC256(secret);

      return JWT.create()
         .withClaim("userId", user.getIdUser().toString())
         .withClaim("role", user.getRoles().name())
         .withSubject(user.getEmail())
         .withIssuedAt(Instant.now())
         .withExpiresAt(Instant.now().plusSeconds(time))
         .sign(algorithm);
   }

   public Optional<JWTUserData> validateToken(String token) {
      try {
         Algorithm algorithm = Algorithm.HMAC256(secret);

         DecodedJWT decode = JWT.require(algorithm).build().verify(token);
         return Optional.of(new JWTUserData(
            UUID.fromString(decode.getClaim("userId").asString()),
            decode.getSubject(),
            decode.getClaim("role").asString()
         ));
      } catch (JWTVerificationException e) {
         return Optional.empty();
      }
   }
}
