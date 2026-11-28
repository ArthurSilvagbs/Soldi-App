package com.github.ArthurSilvagbs.SoldiAPI.gasto;

import com.github.ArthurSilvagbs.SoldiAPI.categoria.CategoriaService;
import com.github.ArthurSilvagbs.SoldiAPI.exceptions.EntidadeNaoEncontradaException;
import com.github.ArthurSilvagbs.SoldiAPI.exceptions.ValorInvalidoException;
import com.github.ArthurSilvagbs.SoldiAPI.gasto.dtos.GastoCreateDTO;
import com.github.ArthurSilvagbs.SoldiAPI.gasto.dtos.GastoResponseDTO;
import com.github.ArthurSilvagbs.SoldiAPI.gasto.dtos.GastoUpdateDTO;
import com.github.ArthurSilvagbs.SoldiAPI.security.JWTUserData;
import com.github.ArthurSilvagbs.SoldiAPI.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GastoService {

   private final GastoRepository repository;
   private final GastoMapper mapper;
   private final CategoriaService categoriaService;
   private final UserService userService;

   @Transactional
   public GastoResponseDTO registrarGasto(GastoCreateDTO dto, JWTUserData userData) {

      if (!userService.verificarSeIdUserJaCadastrado(userData.idUsuario())) {
         throw new EntidadeNaoEncontradaException("User não encontrado.");
      }

      if (!categoriaService.verificarSeCategoriaExiste(dto.idCategoria(), userData)) {
         throw new EntidadeNaoEncontradaException("Categoria não encontrada.");
      }

      if (dto.valor().compareTo(BigDecimal.ZERO) <= 0) {
         throw new ValorInvalidoException("Valor entregue é menor que 0 (zero).");
      }

      Gasto gasto = repository.saveAndFlush(mapper.mapearParaGasto(userData.idUsuario(), dto));

      return mapper.mapearParaResponse(gasto);
   }

   @Transactional(readOnly = true)
   public GastoResponseDTO buscarGastoPorId(UUID idGasto, JWTUserData userData) {
      Gasto gasto = repository.findByIdGastoAndIdUser(idGasto, userData.idUsuario())
         .orElseThrow(() -> new EntidadeNaoEncontradaException("Gasto não encontrado"));

      return mapper.mapearParaResponse(gasto);
   }

   @Transactional(readOnly = true)
   public Page<GastoResponseDTO> buscarGastoPorValor(BigDecimal valor, JWTUserData userData) {
      Pageable pg = PageRequest.of(0, 20, Sort.by("data").descending());
      Page<Gasto> listaPaginada = repository.findByValorAndIdUser(valor, userData.idUsuario(), pg);

      return listaPaginada.map(mapper::mapearParaResponse);
   }

   @Transactional(readOnly = true)
   public Page<GastoResponseDTO> buscarTodosOsGastos(JWTUserData userData) {
      Pageable pg = PageRequest.of(0, 20, Sort.by("data").descending());
      Page<Gasto> listaPaginada = repository.findByIdUser(userData.idUsuario(), pg);

      return listaPaginada.map(mapper::mapearParaResponse);
   }

   @Transactional
   public Page<GastoResponseDTO> buscarTodosOsGastosPorCategoria(UUID idCategoria, JWTUserData userData) {

      if (!categoriaService.verificarSeCategoriaExiste(idCategoria, userData)) {
         throw new EntidadeNaoEncontradaException("Categoria não encontrada");
      }

      Pageable pg = PageRequest.of(0, 20, Sort.by("data").descending());
      Page<Gasto> listaPaginada = repository.findByIdCategoriaAndIdUser(idCategoria, userData.idUsuario(), pg);

      return listaPaginada.map(mapper::mapearParaResponse);
   }

   @Transactional
   public GastoResponseDTO editarGasto(UUID idGasto, GastoUpdateDTO dto, JWTUserData userData) {
      Gasto gasto = repository.findByIdGastoAndIdUser(idGasto, userData.idUsuario())
         .orElseThrow(() -> new EntidadeNaoEncontradaException("Gasto não encontrado."));

      if (dto.valor().compareTo(BigDecimal.ZERO) <= 0) {
         throw new ValorInvalidoException("Valor entregue é menor que 0 (zero).");
      }

      gasto.setIdCategoria(dto.idCategoria());
      gasto.setDescricao(dto.descricao());
      gasto.setValor(dto.valor());
      gasto.setData(dto.data());

      repository.saveAndFlush(gasto);

      return mapper.mapearParaResponse(gasto);
   }

   @Transactional
   public void excluirGasto(UUID idGasto, JWTUserData userData) {
      Gasto gasto = repository.findByIdGastoAndIdUser(idGasto, userData.idUsuario())
         .orElseThrow(() -> new EntidadeNaoEncontradaException("Gassto não encontrado."));

      repository.delete(gasto);
   }


}
