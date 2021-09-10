package com.mboss.core.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mboss.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CriacaoAnamneseTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CriacaoAnamnese.class);
        CriacaoAnamnese criacaoAnamnese1 = new CriacaoAnamnese();
        criacaoAnamnese1.setId(1L);
        CriacaoAnamnese criacaoAnamnese2 = new CriacaoAnamnese();
        criacaoAnamnese2.setId(criacaoAnamnese1.getId());
        assertThat(criacaoAnamnese1).isEqualTo(criacaoAnamnese2);
        criacaoAnamnese2.setId(2L);
        assertThat(criacaoAnamnese1).isNotEqualTo(criacaoAnamnese2);
        criacaoAnamnese1.setId(null);
        assertThat(criacaoAnamnese1).isNotEqualTo(criacaoAnamnese2);
    }
}
