package com.mboss.core.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mboss.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PessoaMedicoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PessoaMedico.class);
        PessoaMedico pessoaMedico1 = new PessoaMedico();
        pessoaMedico1.setId(1L);
        PessoaMedico pessoaMedico2 = new PessoaMedico();
        pessoaMedico2.setId(pessoaMedico1.getId());
        assertThat(pessoaMedico1).isEqualTo(pessoaMedico2);
        pessoaMedico2.setId(2L);
        assertThat(pessoaMedico1).isNotEqualTo(pessoaMedico2);
        pessoaMedico1.setId(null);
        assertThat(pessoaMedico1).isNotEqualTo(pessoaMedico2);
    }
}
