package com.mboss.core.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mboss.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CriacaoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Criacao.class);
        Criacao criacao1 = new Criacao();
        criacao1.setId(1L);
        Criacao criacao2 = new Criacao();
        criacao2.setId(criacao1.getId());
        assertThat(criacao1).isEqualTo(criacao2);
        criacao2.setId(2L);
        assertThat(criacao1).isNotEqualTo(criacao2);
        criacao1.setId(null);
        assertThat(criacao1).isNotEqualTo(criacao2);
    }
}
