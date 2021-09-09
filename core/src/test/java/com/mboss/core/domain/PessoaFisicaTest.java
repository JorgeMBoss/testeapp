package com.mboss.core.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mboss.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PessoaFisicaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PessoaFisica.class);
        PessoaFisica pessoaFisica1 = new PessoaFisica();
        pessoaFisica1.setId(1L);
        PessoaFisica pessoaFisica2 = new PessoaFisica();
        pessoaFisica2.setId(pessoaFisica1.getId());
        assertThat(pessoaFisica1).isEqualTo(pessoaFisica2);
        pessoaFisica2.setId(2L);
        assertThat(pessoaFisica1).isNotEqualTo(pessoaFisica2);
        pessoaFisica1.setId(null);
        assertThat(pessoaFisica1).isNotEqualTo(pessoaFisica2);
    }
}
