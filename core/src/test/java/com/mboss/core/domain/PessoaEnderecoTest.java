package com.mboss.core.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mboss.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PessoaEnderecoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PessoaEndereco.class);
        PessoaEndereco pessoaEndereco1 = new PessoaEndereco();
        pessoaEndereco1.setId(1L);
        PessoaEndereco pessoaEndereco2 = new PessoaEndereco();
        pessoaEndereco2.setId(pessoaEndereco1.getId());
        assertThat(pessoaEndereco1).isEqualTo(pessoaEndereco2);
        pessoaEndereco2.setId(2L);
        assertThat(pessoaEndereco1).isNotEqualTo(pessoaEndereco2);
        pessoaEndereco1.setId(null);
        assertThat(pessoaEndereco1).isNotEqualTo(pessoaEndereco2);
    }
}
