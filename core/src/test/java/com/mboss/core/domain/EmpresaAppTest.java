package com.mboss.core.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mboss.core.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EmpresaAppTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EmpresaApp.class);
        EmpresaApp empresaApp1 = new EmpresaApp();
        empresaApp1.setId(1L);
        EmpresaApp empresaApp2 = new EmpresaApp();
        empresaApp2.setId(empresaApp1.getId());
        assertThat(empresaApp1).isEqualTo(empresaApp2);
        empresaApp2.setId(2L);
        assertThat(empresaApp1).isNotEqualTo(empresaApp2);
        empresaApp1.setId(null);
        assertThat(empresaApp1).isNotEqualTo(empresaApp2);
    }
}
