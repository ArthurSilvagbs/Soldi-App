package com.github.ArthurSilvagbs.SoldiAPI.categoria;

import com.github.ArthurSilvagbs.SoldiAPI.TestcontainersConfiguration;
import com.github.ArthurSilvagbs.SoldiAPI.user.Role;
import com.github.ArthurSilvagbs.SoldiAPI.user.User;
import com.github.ArthurSilvagbs.SoldiAPI.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/** Testa as consultas JPQL do repository contra um Postgres real. */
@DataJpaTest(properties = {"JWT_SECRET=teste", "POSTGRES_USER=test", "POSTGRES_PASSWORD=test"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
class CategoriaRepositoryTest {

   @Autowired
   private CategoriaRepository repository;

   @Autowired
   private UserRepository userRepository;

   private UUID idA;
   private UUID idB;

   @BeforeEach
   void prepararUsuarios() {
      repository.deleteAll();
      userRepository.deleteAll();
      idA = userRepository.saveAndFlush(new User("A", "a@email.com", "hash", Role.USER)).getIdUser();
      idB = userRepository.saveAndFlush(new User("B", "b@email.com", "hash", Role.USER)).getIdUser();
   }

   @Test
   void existsByNomeAndIdUser_deveConsiderarSomenteOUsuarioInformado() {
      repository.saveAndFlush(new Categoria(idA, "Mercado"));

      assertThat(repository.existsByNomeAndIdUser("Mercado", idA)).isTrue();
      assertThat(repository.existsByNomeAndIdUser("Mercado", idB)).isFalse();
      assertThat(repository.existsByNomeAndIdUser("Lazer", idA)).isFalse();
   }

   @Test
   void findByIdCategoriaAndIdUser_deveRetornarSoParaODono() {
      Categoria categoria = repository.saveAndFlush(new Categoria(idA, "Mercado"));

      assertThat(repository.findByIdCategoriaAndIdUser(categoria.getIdCategoria(), idA)).isPresent();
      assertThat(repository.findByIdCategoriaAndIdUser(categoria.getIdCategoria(), idB)).isEmpty();
      assertThat(repository.findByIdCategoriaAndIdUser(UUID.randomUUID(), idA)).isEmpty();
   }

   @Test
   void findByNomeAndIdUser_deveFiltrarPorUsuario() {
      repository.saveAndFlush(new Categoria(idA, "Mercado"));
      repository.saveAndFlush(new Categoria(idB, "Mercado"));

      assertThat(repository.findByNomeAndIdUser("Mercado", idA)).get()
         .extracting(Categoria::getIdUser).isEqualTo(idA);
      assertThat(repository.findByNomeAndIdUser("Mercado", idB)).get()
         .extracting(Categoria::getIdUser).isEqualTo(idB);
   }

   @Test
   void findByIdUser_deveListarSomenteDoUsuario() {
      repository.saveAndFlush(new Categoria(idA, "Mercado"));
      repository.saveAndFlush(new Categoria(idA, "Lazer"));
      repository.saveAndFlush(new Categoria(idB, "Transporte"));

      assertThat(repository.findByIdUser(idA)).extracting(Categoria::getNome)
         .containsExactlyInAnyOrder("Mercado", "Lazer");
      assertThat(repository.findByIdUser(UUID.randomUUID())).isEmpty();
   }

   @Test
   void saveAndFlush_devePreencherCreatedAtNaPropriaEntidade() {
      Categoria salva = repository.saveAndFlush(new Categoria(idA, "Mercado"));

      assertThat(salva.getCreatedAt()).isNotNull();
   }
}
