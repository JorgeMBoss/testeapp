package com.mboss.core.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mboss.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PessoaComplementoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PessoaComplemento.class);
        PessoaComplemento pessoaComplemento1 = new PessoaComplemento();
        pessoaComplemento1.setId(1L);
        PessoaComplemento pessoaComplemento2 = new PessoaComplemento();
        pessoaComplemento2.setId(pessoaComplemento1.getId());
        assertThat(pessoaComplemento1).isEqualTo(pessoaComplemento2);
        pessoaComplemento2.setId(2L);
        assertThat(pessoaComplemento1).isNotEqualTo(pessoaComplemento2);
        pessoaComplemento1.setId(null);
        assertThat(pessoaComplemento1).isNotEqualTo(pessoaComplemento2);
    }
}
