package com.github.ArthurSilvagbs.SoldiAPI.user;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

   @Test
   void userDetails_deveExporEmailSenhaEAuthority() {
      User user = new User("Arthur", "arthur@email.com", "hash", Role.ADMIN);

      assertThat(user.getUsername()).isEqualTo("arthur@email.com");
      assertThat(user.getPassword()).isEqualTo("hash");
      assertThat(user.getAuthorities()).extracting(Object::toString).containsExactly("ROLE_ADMIN");
   }

   @Test
   void construtorSemRole_deveDeixarRoleNula() {
      User user = new User("Arthur", "arthur@email.com", "hash");

      assertThat(user.getRoles()).isNull();
   }
}
