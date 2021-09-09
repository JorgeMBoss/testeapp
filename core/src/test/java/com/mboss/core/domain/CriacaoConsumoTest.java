package com.mboss.core.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mboss.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CriacaoConsumoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CriacaoConsumo.class);
        CriacaoConsumo criacaoConsumo1 = new CriacaoConsumo();
        criacaoConsumo1.setId(1L);
        CriacaoConsumo criacaoConsumo2 = new CriacaoConsumo();
        criacaoConsumo2.setId(criacaoConsumo1.getId());
        assertThat(criacaoConsumo1).isEqualTo(criacaoConsumo2);
        criacaoConsumo2.setId(2L);
        assertThat(criacaoConsumo1).isNotEqualTo(criacaoConsumo2);
        criacaoConsumo1.setId(null);
        assertThat(criacaoConsumo1).isNotEqualTo(criacaoConsumo2);
    }
}
