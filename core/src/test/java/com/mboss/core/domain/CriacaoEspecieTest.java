package com.mboss.core.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mboss.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CriacaoEspecieTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CriacaoEspecie.class);
        CriacaoEspecie criacaoEspecie1 = new CriacaoEspecie();
        criacaoEspecie1.setId(1L);
        CriacaoEspecie criacaoEspecie2 = new CriacaoEspecie();
        criacaoEspecie2.setId(criacaoEspecie1.getId());
        assertThat(criacaoEspecie1).isEqualTo(criacaoEspecie2);
        criacaoEspecie2.setId(2L);
        assertThat(criacaoEspecie1).isNotEqualTo(criacaoEspecie2);
        criacaoEspecie1.setId(null);
        assertThat(criacaoEspecie1).isNotEqualTo(criacaoEspecie2);
    }
}
