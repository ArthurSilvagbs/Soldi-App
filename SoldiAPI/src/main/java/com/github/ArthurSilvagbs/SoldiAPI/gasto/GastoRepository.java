package com.github.ArthurSilvagbs.SoldiAPI.gasto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GastoRepository extends JpaRepository<Gasto, UUID> {

   @Query("SELECT g FROM Gasto g WHERE g.idGasto = :idGasto AND g.idUser = :idUser")
   Optional<Gasto> findByIdGastoAndIdUser(@Param("idGasto") UUID idGasto, @Param("idUser") UUID idUser);

   @Query("SELECT g FROM Gasto g WHERE g.idUser = :idUser ORDER BY g.data DESC, g.createdAt DESC")
   Page<Gasto> findByIdUser(@Param("idUser") UUID idUser, Pageable pg);

   @Query("""
      SELECT g FROM Gasto g
      WHERE g.valor = :valor AND g.idUser = :idUser
      ORDER BY g.data DESC, g.createdAt DESC
      """)
   Page<Gasto> findByValorAndIdUser(@Param("valor") BigDecimal valor, @Param("idUser") UUID idUser, Pageable pg);

   @Query("""
      SELECT g FROM Gasto g
      WHERE g.idUser = :idUser AND g.data >= :inicio AND g.data < :fim
      ORDER BY g.data DESC, g.createdAt DESC
      """)
   List<Gasto> findByIdUserNoPeriodo(
      @Param("idUser") UUID idUser,
      @Param("inicio") LocalDate inicio,
      @Param("fim") LocalDate fim
   );

   @Query("""
      SELECT g FROM Gasto g
      WHERE g.idCategoria = :idCategoria AND g.idUser = :idUser
      ORDER BY g.data DESC, g.createdAt DESC
      """)
   Page<Gasto> findByIdCategoriaAndIdUser(@Param("idCategoria") UUID idCategoria, @Param("idUser") UUID idUser, Pageable pg);

   @Query("SELECT COUNT(g) > 0 FROM Gasto g WHERE g.idCategoria = :idCategoria AND g.idUser = :idUser")
   boolean existsByIdCategoriaAndIdUser(@Param("idCategoria") UUID idCategoria, @Param("idUser") UUID idUser);
}
