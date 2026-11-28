package com.github.ArthurSilvagbs.SoldiAPI.categoria;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {

    Optional<Categoria> findByNome(String nome);
    boolean existsByNome(String nome);

    @Query("SELECT c FROM Categoria c WHERE c.nome = :nome AND c.idUser = :idUser")
    Optional<Categoria> findByNomeAndIdUser(@Param("nome") String nome, @Param("idUser") UUID idUser);

    @Query("SELECT COUNT(c) > 0 FROM Categoria c WHERE c.nome = :nome AND c.idUser = :idUser")
    boolean existsByNomeAndIdUser(@Param("nome") String nome, @Param("idUser") UUID idUser);

    @Query("SELECT c FROM Categoria c WHERE c.idCategoria = :idCategoria AND c.idUser = :idUser")
    Optional<Categoria> findByIdCategoriaAndIdUser(@Param("idCategoria") UUID idCategoria, @Param("idUser") UUID idUser);

    @Query("SELECT COUNT(c) > 0 FROM Categoria c WHERE c.idCategoria = :idCategoria AND c.idUser = :idUser")
    boolean existsByIdCategoriaAndIdUser(@Param("idCategoria") UUID idCategoria, @Param("idUser") UUID idUser);

    @Query("SELECT c FROM Categoria c WHERE c.idUser = :idUser")
    List<Categoria> findByIdUser(@Param("idUser") UUID idUser);
}
