package com.mboss.core.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mboss.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PessoaFuncaoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PessoaFuncao.class);
        PessoaFuncao pessoaFuncao1 = new PessoaFuncao();
        pessoaFuncao1.setId(1L);
        PessoaFuncao pessoaFuncao2 = new PessoaFuncao();
        pessoaFuncao2.setId(pessoaFuncao1.getId());
        assertThat(pessoaFuncao1).isEqualTo(pessoaFuncao2);
        pessoaFuncao2.setId(2L);
        assertThat(pessoaFuncao1).isNotEqualTo(pessoaFuncao2);
        pessoaFuncao1.setId(null);
        assertThat(pessoaFuncao1).isNotEqualTo(pessoaFuncao2);
    }
}
