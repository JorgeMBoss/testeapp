package com.mboss.core.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mboss.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CriacaoCorTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CriacaoCor.class);
        CriacaoCor criacaoCor1 = new CriacaoCor();
        criacaoCor1.setId(1L);
        CriacaoCor criacaoCor2 = new CriacaoCor();
        criacaoCor2.setId(criacaoCor1.getId());
        assertThat(criacaoCor1).isEqualTo(criacaoCor2);
        criacaoCor2.setId(2L);
        assertThat(criacaoCor1).isNotEqualTo(criacaoCor2);
        criacaoCor1.setId(null);
        assertThat(criacaoCor1).isNotEqualTo(criacaoCor2);
    }
}
