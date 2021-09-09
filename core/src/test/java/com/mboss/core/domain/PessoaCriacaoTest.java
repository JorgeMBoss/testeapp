package com.mboss.core.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mboss.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PessoaCriacaoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PessoaCriacao.class);
        PessoaCriacao pessoaCriacao1 = new PessoaCriacao();
        pessoaCriacao1.setId(1L);
        PessoaCriacao pessoaCriacao2 = new PessoaCriacao();
        pessoaCriacao2.setId(pessoaCriacao1.getId());
        assertThat(pessoaCriacao1).isEqualTo(pessoaCriacao2);
        pessoaCriacao2.setId(2L);
        assertThat(pessoaCriacao1).isNotEqualTo(pessoaCriacao2);
        pessoaCriacao1.setId(null);
        assertThat(pessoaCriacao1).isNotEqualTo(pessoaCriacao2);
    }
}
