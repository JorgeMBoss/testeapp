package com.mboss.core.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mboss.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PessoaColaboradorTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PessoaColaborador.class);
        PessoaColaborador pessoaColaborador1 = new PessoaColaborador();
        pessoaColaborador1.setId(1L);
        PessoaColaborador pessoaColaborador2 = new PessoaColaborador();
        pessoaColaborador2.setId(pessoaColaborador1.getId());
        assertThat(pessoaColaborador1).isEqualTo(pessoaColaborador2);
        pessoaColaborador2.setId(2L);
        assertThat(pessoaColaborador1).isNotEqualTo(pessoaColaborador2);
        pessoaColaborador1.setId(null);
        assertThat(pessoaColaborador1).isNotEqualTo(pessoaColaborador2);
    }
}
