package com.github.ArthurSilvagbs.SoldiAPI.categoria;

import com.github.ArthurSilvagbs.SoldiAPI.categoria.dtos.CategoriaCreateDTO;
import com.github.ArthurSilvagbs.SoldiAPI.categoria.dtos.CategoriaResponseDTO;
import com.github.ArthurSilvagbs.SoldiAPI.categoria.dtos.CategoriaUpdateDTO;
import com.github.ArthurSilvagbs.SoldiAPI.exceptions.EntidadeDuplicadaException;
import com.github.ArthurSilvagbs.SoldiAPI.exceptions.EntidadeNaoEncontradaException;
import com.github.ArthurSilvagbs.SoldiAPI.security.JWTUserData;
import com.github.ArthurSilvagbs.SoldiAPI.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

   @Mock
   private CategoriaRepository repository;

   @Mock
   private UserService userService;

   @Spy
   private CategoriaMapper mapper = new CategoriaMapper();

   @InjectMocks
   private CategoriaService service;

   private final UUID idUser = UUID.randomUUID();
   private final JWTUserData userData = new JWTUserData(idUser, "arthur@email.com", "USER");

   @Test
   void criarCategoria_deveSalvarERetornarResponse() {
      when(userService.verificarSeIdUserJaCadastrado(idUser)).thenReturn(true);
      when(repository.existsByNomeAndIdUser("Mercado", idUser)).thenReturn(false);
      when(repository.saveAndFlush(any(Categoria.class))).thenAnswer(inv -> inv.getArgument(0));

      CategoriaResponseDTO response = service.criarCategoria(new CategoriaCreateDTO("Mercado"), userData);

      assertThat(response.nome()).isEqualTo("Mercado");
      assertThat(response.idUser()).isEqualTo(idUser);
      verify(repository).saveAndFlush(any(Categoria.class));
   }

   @Test
   void criarCategoria_usuarioInexistente_deveLancarNaoEncontrada() {
      when(userService.verificarSeIdUserJaCadastrado(idUser)).thenReturn(false);

      assertThatThrownBy(() -> service.criarCategoria(new CategoriaCreateDTO("Mercado"), userData))
         .isInstanceOf(EntidadeNaoEncontradaException.class);

      verify(repository, never()).saveAndFlush(any());
   }

   @Test
   void criarCategoria_nomeDuplicado_deveLancarDuplicada() {
      when(userService.verificarSeIdUserJaCadastrado(idUser)).thenReturn(true);
      when(repository.existsByNomeAndIdUser("Mercado", idUser)).thenReturn(true);

      assertThatThrownBy(() -> service.criarCategoria(new CategoriaCreateDTO("Mercado"), userData))
         .isInstanceOf(EntidadeDuplicadaException.class);

      verify(repository, never()).saveAndFlush(any());
   }

   @Test
   void buscarCategoriaPorNome_deveRetornarCategoriaDoUsuario() {
      when(repository.findByNomeAndIdUser("Mercado", idUser))
         .thenReturn(Optional.of(new Categoria(idUser, "Mercado")));

      CategoriaResponseDTO response = service.buscarCategoriaPorNome("Mercado", userData);

      assertThat(response.nome()).isEqualTo("Mercado");
   }

   @Test
   void buscarCategoriaPorNome_inexistente_deveLancarNaoEncontrada() {
      when(repository.findByNomeAndIdUser("Mercado", idUser)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.buscarCategoriaPorNome("Mercado", userData))
         .isInstanceOf(EntidadeNaoEncontradaException.class);
   }

   @Test
   void buscarCategoriasDoUser_deveRetornarLista() {
      when(repository.findByIdUser(idUser)).thenReturn(List.of(
         new Categoria(idUser, "Mercado"),
         new Categoria(idUser, "Lazer")
      ));

      List<CategoriaResponseDTO> lista = service.buscarCategoriasDoUser(userData);

      assertThat(lista).extracting(CategoriaResponseDTO::nome).containsExactly("Mercado", "Lazer");
   }

   @Test
   void buscarCategoriasDoUser_listaVazia_deveLancarNaoEncontrada() {
      when(repository.findByIdUser(idUser)).thenReturn(List.of());

      assertThatThrownBy(() -> service.buscarCategoriasDoUser(userData))
         .isInstanceOf(EntidadeNaoEncontradaException.class);
   }

   @Test
   void editarNomeCategoria_deveAtualizarNome() {
      UUID id = UUID.randomUUID();
      Categoria categoria = new Categoria(idUser, "Mercado");
      when(repository.findByIdCategoriaAndIdUser(id, idUser)).thenReturn(Optional.of(categoria));
      when(repository.save(categoria)).thenReturn(categoria);

      CategoriaResponseDTO response = service.editarNomeCategoria(new CategoriaUpdateDTO("Supermercado"), id, userData);

      assertThat(response.nome()).isEqualTo("Supermercado");
      assertThat(categoria.getNome()).isEqualTo("Supermercado");
   }

   @Test
   void editarNomeCategoria_inexistenteOuDeOutroUsuario_deveLancarNaoEncontrada() {
      UUID id = UUID.randomUUID();
      when(repository.findByIdCategoriaAndIdUser(id, idUser)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.editarNomeCategoria(new CategoriaUpdateDTO("Outro"), id, userData))
         .isInstanceOf(EntidadeNaoEncontradaException.class);

      verify(repository, never()).save(any());
   }

   @Test
   void editarNomeCategoria_mesmoNome_deveLancarDuplicada() {
      UUID id = UUID.randomUUID();
      when(repository.findByIdCategoriaAndIdUser(id, idUser)).thenReturn(Optional.of(new Categoria(idUser, "Mercado")));

      assertThatThrownBy(() -> service.editarNomeCategoria(new CategoriaUpdateDTO("Mercado"), id, userData))
         .isInstanceOf(EntidadeDuplicadaException.class);

      verify(repository, never()).save(any());
   }

   @Test
   void excluirCategoria_deveRemover() {
      UUID id = UUID.randomUUID();
      Categoria categoria = new Categoria(idUser, "Mercado");
      when(repository.findByIdCategoriaAndIdUser(id, idUser)).thenReturn(Optional.of(categoria));

      service.excluirCategoria(id, userData);

      verify(repository).delete(categoria);
   }

   @Test
   void excluirCategoria_inexistenteOuDeOutroUsuario_deveLancarNaoEncontrada() {
      UUID id = UUID.randomUUID();
      when(repository.findByIdCategoriaAndIdUser(id, idUser)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.excluirCategoria(id, userData))
         .isInstanceOf(EntidadeNaoEncontradaException.class);

      verify(repository, never()).delete(any());
   }
}
