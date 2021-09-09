package com.mboss.core.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mboss.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PessoaFiscaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PessoaFisca.class);
        PessoaFisca pessoaFisca1 = new PessoaFisca();
        pessoaFisca1.setId(1L);
        PessoaFisca pessoaFisca2 = new PessoaFisca();
        pessoaFisca2.setId(pessoaFisca1.getId());
        assertThat(pessoaFisca1).isEqualTo(pessoaFisca2);
        pessoaFisca2.setId(2L);
        assertThat(pessoaFisca1).isNotEqualTo(pessoaFisca2);
        pessoaFisca1.setId(null);
        assertThat(pessoaFisca1).isNotEqualTo(pessoaFisca2);
    }
}
