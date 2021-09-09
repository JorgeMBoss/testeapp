package com.mboss.core.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mboss.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PessoaJuridicaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PessoaJuridica.class);
        PessoaJuridica pessoaJuridica1 = new PessoaJuridica();
        pessoaJuridica1.setId(1L);
        PessoaJuridica pessoaJuridica2 = new PessoaJuridica();
        pessoaJuridica2.setId(pessoaJuridica1.getId());
        assertThat(pessoaJuridica1).isEqualTo(pessoaJuridica2);
        pessoaJuridica2.setId(2L);
        assertThat(pessoaJuridica1).isNotEqualTo(pessoaJuridica2);
        pessoaJuridica1.setId(null);
        assertThat(pessoaJuridica1).isNotEqualTo(pessoaJuridica2);
    }
}
