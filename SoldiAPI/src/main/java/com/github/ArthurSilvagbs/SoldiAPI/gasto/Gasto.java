package com.github.ArthurSilvagbs.SoldiAPI.gasto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "gasto")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Gasto {

   @Id
   @GeneratedValue(strategy = GenerationType.UUID)
   private UUID idGasto;

   @Column(name = "id_user", nullable = false, updatable = false)
   private UUID idUser;

   @Setter
   @Column(name = "id_categoria", nullable = false)
   private UUID idCategoria;

   @Setter
   @Column(name = "descricao", nullable = false)
   private String descricao;

   @Setter
   @Column(name = "valor", nullable = false, precision = 19, scale = 2)
   private BigDecimal valor;

   @Setter
   @Column(name = "data", nullable = false)
   private LocalDate data;

   @CreationTimestamp
   @Column(name = "created_at", updatable = false)
   private LocalDateTime createdAt;

   @UpdateTimestamp
   @Column(name = "updated_at")
   private LocalDateTime updatedAt;

   public Gasto(
      UUID idUser,
      UUID idCategoria,
      String descricao,
      BigDecimal valor,
      LocalDate data
   ) {
      this.idUser = idUser;
      this.idCategoria = idCategoria;
      this.descricao = descricao;
      this.valor = valor;
      this.data = data;
   }

}
