package com.github.ArthurSilvagbs.SoldiAPI.categoria;

import com.github.ArthurSilvagbs.SoldiAPI.categoria.dtos.CategoriaCreateDTO;
import com.github.ArthurSilvagbs.SoldiAPI.categoria.dtos.CategoriaResponseDTO;
import com.github.ArthurSilvagbs.SoldiAPI.categoria.dtos.CategoriaUpdateDTO;
import com.github.ArthurSilvagbs.SoldiAPI.exceptions.dtos.ErrorResponse;
import com.github.ArthurSilvagbs.SoldiAPI.security.JWTUserData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/categoria")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Categoria", description = "cadastro, consulta, atualização e exclusao de uma categoria por user")
public class CategoriaController{

   private final CategoriaService service;

   @Operation(summary = "Cadastrar uma categoria", description = "O usuário cadastra uma categoria vinculada a ele e somente a ele. Exige autenticação")
   @ApiResponses({
      @ApiResponse(
         responseCode = "201",
         description = "Categoria cadastrada com sucesso.",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = CategoriaResponseDTO.class)
         )
      ),
      @ApiResponse(
         responseCode = "404",
         description = "Id do usuario não encontrado. Conferencia antes de salvar",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = """
            {
               "status": 404,
               "error": "NOT_FOUND",
               "message": "",
               "path": "/categoria",
               "timestamp": "2026-09-29T12:40:00"
            }
            """)
         )
      ),
      @ApiResponse(
         responseCode = "409",
         description = "O nome recebido já existe em outra categoria",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = """
               {
                  "status": 409,
                  "error": "CONFLICT",
                  "message": "Já existe uma categoria com este nome",
                  "path": "/categoria",
                  "timestamp": "2026-09-29T12:40:00"
               }
               """
            )
         )
      )
   })
   @PostMapping
   public ResponseEntity<CategoriaResponseDTO> criarCategoria(
      @Valid @RequestBody CategoriaCreateDTO dto,
      @AuthenticationPrincipal JWTUserData userData
   ) {
      CategoriaResponseDTO response = service.criarCategoria(dto, userData);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
   }

   @Operation(summary = "Buscar uma categoria pelo nome", description = "O usuário consulta uma categoria dele pelo nome. Exige autenticação")
   @ApiResponses({
      @ApiResponse(
         responseCode = "200",
         description = "Categoria encontrada.",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = CategoriaResponseDTO.class)
         )
      ),
      @ApiResponse(
         responseCode = "404",
         description = "Não há categoria do usuário com este nome",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = """
               {
                  "status": 404,
                  "error": "NOT_FOUND",
                  "message": "Categoria não encontrada.",
                  "path": "/categoria/Mercado",
                  "timestamp": "2026-09-29T12:40:00"
               }
               """
            )
         )
      )
   })
   @GetMapping("/{nome}")
   public ResponseEntity<CategoriaResponseDTO> buscarCategoria(
      @PathVariable("nome") String nome,
      @AuthenticationPrincipal JWTUserData userData
   ) {
      CategoriaResponseDTO response = service.buscarCategoriaPorNome(nome, userData);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @Operation(summary = "Listar as categorias", description = "O usuário lista todas as categorias vinculadas a ele e somente a ele. Exige autenticação")
   @ApiResponses({
      @ApiResponse(
         responseCode = "200",
         description = "Categorias listadas com sucesso.",
         content = @Content(
            mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = CategoriaResponseDTO.class))
         )
      ),
      @ApiResponse(
         responseCode = "404",
         description = "O usuário não possui categorias cadastradas",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = """
               {
                  "status": 404,
                  "error": "NOT_FOUND",
                  "message": "Não há categorias cadastradas",
                  "path": "/categoria",
                  "timestamp": "2026-09-29T12:40:00"
               }
               """
            )
         )
      )
   })
   @GetMapping()
   public ResponseEntity<List<CategoriaResponseDTO>> buscarCategoriasDoUser(
      @AuthenticationPrincipal JWTUserData userData
   ) {
      List<CategoriaResponseDTO> lista = service.buscarCategoriasDoUser(userData);
      return ResponseEntity.status(HttpStatus.OK).body(lista);
   }

   @Operation(summary = "Atualizar o nome de uma categoria", description = "O usuário altera o nome de uma categoria dele, identificada pelo id. Exige autenticação")
   @ApiResponses({
      @ApiResponse(
         responseCode = "200",
         description = "Categoria atualizada com sucesso.",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = CategoriaResponseDTO.class)
         )
      ),
      @ApiResponse(
         responseCode = "400",
         description = "Nome inválido ou id fora do formato UUID",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = """
               {
                  "status": 400,
                  "error": "BAD_REQUEST",
                  "message": "nome: O nome é obrigatório.",
                  "path": "/categoria/3fa85f64-5717-4562-b3fc-2c963f66afa6",
                  "timestamp": "2026-09-29T12:40:00"
               }
               """
            )
         )
      ),
      @ApiResponse(
         responseCode = "404",
         description = "Não há categoria do usuário com este id",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = """
               {
                  "status": 404,
                  "error": "NOT_FOUND",
                  "message": "Categoria não encontrada.",
                  "path": "/categoria/3fa85f64-5717-4562-b3fc-2c963f66afa6",
                  "timestamp": "2026-09-29T12:40:00"
               }
               """
            )
         )
      ),
      @ApiResponse(
         responseCode = "409",
         description = "O novo nome é igual ao anterior ou já existe em outra categoria do usuário",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = """
               {
                  "status": 409,
                  "error": "CONFLICT",
                  "message": "O novo nome é igual ao anterior.",
                  "path": "/categoria/3fa85f64-5717-4562-b3fc-2c963f66afa6",
                  "timestamp": "2026-09-29T12:40:00"
               }
               """
            )
         )
      )
   })
   @PutMapping("/{id}")
   public ResponseEntity<CategoriaResponseDTO> atualizarNomeCategoria(
      @Valid @RequestBody CategoriaUpdateDTO dto,
      @PathVariable("id") UUID id,
      @AuthenticationPrincipal JWTUserData userData
   ) {
      CategoriaResponseDTO response = service.editarNomeCategoria(dto, id, userData);
      return ResponseEntity.status(HttpStatus.OK).body(response);
   }

   @Operation(summary = "Excluir uma categoria", description = "O usuário exclui uma categoria dele, identificada pelo id. Exige autenticação")
   @ApiResponses({
      @ApiResponse(
         responseCode = "204",
         description = "Categoria excluída com sucesso.",
         content = @Content
      ),
      @ApiResponse(
         responseCode = "400",
         description = "Id fora do formato UUID",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = """
               {
                  "status": 400,
                  "error": "BAD_REQUEST",
                  "message": "Valor inválido para o parâmetro 'id'.",
                  "path": "/categoria/123",
                  "timestamp": "2026-09-29T12:40:00"
               }
               """
            )
         )
      ),
      @ApiResponse(
         responseCode = "404",
         description = "Não há categoria do usuário com este id",
         content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(value = """
               {
                  "status": 404,
                  "error": "NOT_FOUND",
                  "message": "Categoria não encontrada",
                  "path": "/categoria/3fa85f64-5717-4562-b3fc-2c963f66afa6",
                  "timestamp": "2026-09-29T12:40:00"
               }
               """
            )
         )
      )
   })
   @DeleteMapping("/{id}")
   public ResponseEntity<Void> deletarCategoriaPorId(
      @PathVariable("id") UUID id,
      @AuthenticationPrincipal JWTUserData userData
   ) {
      service.excluirCategoria(id, userData);
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
   }

}
