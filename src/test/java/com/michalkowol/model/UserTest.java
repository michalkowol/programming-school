package com.michalkowol.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void itShouldCreateNewUser() {
        User user = User.of("a", "b", "c");
        assertThat(user).isNotNull();
    }
}
