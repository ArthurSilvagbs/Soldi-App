package com.github.ArthurSilvagbs.SoldiAPI.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class User implements UserDetails {

   @Id
   @GeneratedValue(strategy = GenerationType.UUID)
   private UUID idUser;

   @Setter
   @Column(name = "nome", nullable = false)
   private String nome;

   @Setter
   @Column(name = "email", unique = true, nullable = false, length = 75)
   private String email;

   @Setter
   @Column(name = "senha_hash", nullable = false, length = 60)
   private String senhaHash;

   @Setter
   @Enumerated(value = EnumType.STRING)
   private Role roles;

   @CreationTimestamp
   @Column(name = "created_at", updatable = false)
   private LocalDateTime createdAt;

   @UpdateTimestamp
   private LocalDateTime updatedAt;

   public User(
      String nome,
      String email,
      String senhaHash,
      Role roles
   ) {
      this.nome = nome;
      this.email = email;
      this.senhaHash = senhaHash;
      this.roles = roles;
   }

   public User(
      String nome,
      String email,
      String senhaHash
   ) {
      this.nome = nome;
      this.email = email;
      this.senhaHash = senhaHash;
   }

   @Override
   public Collection<? extends GrantedAuthority> getAuthorities() {
      return List.of(new SimpleGrantedAuthority("ROLE_" + this.getRoles().name()));
   }

   @Override
   public @Nullable String getPassword() {
      return this.senhaHash;
   }

   @Override
   public String getUsername() {
      return this.email;
   }
}
