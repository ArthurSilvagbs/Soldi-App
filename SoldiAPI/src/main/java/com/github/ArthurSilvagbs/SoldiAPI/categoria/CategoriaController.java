package com.github.ArthurSilvagbs.SoldiAPI.categoria;

import com.github.ArthurSilvagbs.SoldiAPI.categoria.dtos.CategoriaCreateDTO;
import com.github.ArthurSilvagbs.SoldiAPI.categoria.dtos.CategoriaResponseDTO;
import com.github.ArthurSilvagbs.SoldiAPI.categoria.dtos.CategoriaUpdateDTO;
import com.github.ArthurSilvagbs.SoldiAPI.security.JWTUserData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categoria")
@RequiredArgsConstructor
public class CategoriaController{

   private final CategoriaService service;

   @PostMapping
   public ResponseEntity<CategoriaResponseDTO> criarCategoria(
      @Valid @RequestBody CategoriaCreateDTO dto,
      @AuthenticationPrincipal JWTUserData userData
   ) {
      CategoriaResponseDTO response = service.criarCategoria(dto, userData);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
   }

   @GetMapping("/{nome}")
   public ResponseEntity<CategoriaResponseDTO> buscarCategoria(
      @PathVariable("nome") String nome,
      @AuthenticationPrincipal JWTUserData userData
   ) {
      CategoriaResponseDTO response = service.buscarCategoriaPorNome(nome, userData);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @GetMapping()
   public ResponseEntity<List<CategoriaResponseDTO>> buscarCategoriasDoUser(
      @AuthenticationPrincipal JWTUserData userData
   ) {
      List<CategoriaResponseDTO> lista = service.buscarCategoriasDoUser(userData);
      return ResponseEntity.status(HttpStatus.OK).body(lista);
   }

   @PutMapping("/{nome}")
   public ResponseEntity<CategoriaResponseDTO> atualizarNomeCategoria(
      @Valid @RequestBody CategoriaUpdateDTO dto,
      @PathVariable("nome") String nome,
      @AuthenticationPrincipal JWTUserData userData
   ) {
      CategoriaResponseDTO response = service.editarNomeCategoria(dto, nome);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @DeleteMapping("/{id}")
   public ResponseEntity<Void> deletarCategoriaPorId(
      @PathVariable("id") String id,
      @AuthenticationPrincipal JWTUserData userData
   ) {
      service.excluirCategoria(id);
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
   }

}
