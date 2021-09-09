package com.mboss.core.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mboss.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CriacaoRacaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CriacaoRaca.class);
        CriacaoRaca criacaoRaca1 = new CriacaoRaca();
        criacaoRaca1.setId(1L);
        CriacaoRaca criacaoRaca2 = new CriacaoRaca();
        criacaoRaca2.setId(criacaoRaca1.getId());
        assertThat(criacaoRaca1).isEqualTo(criacaoRaca2);
        criacaoRaca2.setId(2L);
        assertThat(criacaoRaca1).isNotEqualTo(criacaoRaca2);
        criacaoRaca1.setId(null);
        assertThat(criacaoRaca1).isNotEqualTo(criacaoRaca2);
    }
}
