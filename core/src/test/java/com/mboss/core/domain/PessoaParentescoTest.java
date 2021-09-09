package com.mboss.core.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mboss.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PessoaParentescoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PessoaParentesco.class);
        PessoaParentesco pessoaParentesco1 = new PessoaParentesco();
        pessoaParentesco1.setId(1L);
        PessoaParentesco pessoaParentesco2 = new PessoaParentesco();
        pessoaParentesco2.setId(pessoaParentesco1.getId());
        assertThat(pessoaParentesco1).isEqualTo(pessoaParentesco2);
        pessoaParentesco2.setId(2L);
        assertThat(pessoaParentesco1).isNotEqualTo(pessoaParentesco2);
        pessoaParentesco1.setId(null);
        assertThat(pessoaParentesco1).isNotEqualTo(pessoaParentesco2);
    }
}
