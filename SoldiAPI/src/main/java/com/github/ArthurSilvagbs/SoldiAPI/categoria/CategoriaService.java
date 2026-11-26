package com.github.ArthurSilvagbs.SoldiAPI.categoria;

import com.github.ArthurSilvagbs.SoldiAPI.categoria.dtos.CategoriaCreateDTO;
import com.github.ArthurSilvagbs.SoldiAPI.categoria.dtos.CategoriaResponseDTO;
import com.github.ArthurSilvagbs.SoldiAPI.categoria.dtos.CategoriaUpdateDTO;
import com.github.ArthurSilvagbs.SoldiAPI.exceptions.EntidadeDuplicadaException;
import com.github.ArthurSilvagbs.SoldiAPI.exceptions.EntidadeNaoEncontradaException;
import com.github.ArthurSilvagbs.SoldiAPI.security.JWTUserData;
import com.github.ArthurSilvagbs.SoldiAPI.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoriaService {

   private final CategoriaRepository repository;
   private final CategoriaMapper mapper;
   private final UserService userService;

   @Transactional
   public CategoriaResponseDTO criarCategoria(CategoriaCreateDTO dto, JWTUserData userData) {

      UUID idUser = userData.idUsuario();

      if (!userService.verificarSeIdUserJaCadastrado(idUser)) {
         throw new EntidadeNaoEncontradaException("O id do usuário não existe.");
      }

      if (repository.existsByNome(dto.nome())) {
         throw new EntidadeDuplicadaException("Já existe uma categoria com este nome.");
      }

      Categoria categoria = repository.save(mapper.mapearParaCategoria(idUser, dto));

      return mapper.mapearParaResponse(categoria);
   }

   @Transactional(readOnly = true)
   public CategoriaResponseDTO buscarCategoriaPorNome(String nome, JWTUserData userData) {
      Categoria categoria = repository.findByNomeAndIdUser(nome, userData.idUsuario())
         .orElseThrow(() -> new EntidadeNaoEncontradaException("Categoria não encontrada."));

      return mapper.mapearParaResponse(categoria);
   }

   @Transactional(readOnly = true)
   public List<CategoriaResponseDTO> buscarCategoriasDoUser(JWTUserData userData) {
      List<Categoria> lista = repository.findByIdUser(userData.idUsuario());

      if (lista.isEmpty()) {
         throw new EntidadeNaoEncontradaException("Não há categorias cadastradas");
      }

      return lista.stream().map(mapper::mapearParaResponse).toList();
   }

   @Transactional
   public CategoriaResponseDTO editarNomeCategoria(CategoriaUpdateDTO dto, String nome) {
      Categoria categoria = repository.findByNome(nome)
         .orElseThrow(() -> new EntidadeNaoEncontradaException("Categoria não encontrada."));

      if (categoria.getNome().equals(dto.nome())) {
         throw new EntidadeDuplicadaException("O novo nome é igual ao anterior.");
      }

      categoria.setNome(dto.nome());

      return mapper.mapearParaResponse(repository.save(categoria));
   }

   @Transactional
   public void excluirCategoria(String id) {
      Categoria categoria = repository.findById(UUID.fromString(id))
         .orElseThrow(() -> new EntidadeNaoEncontradaException("Categoria não encontrada"));

      repository.delete(categoria);
   }


}
