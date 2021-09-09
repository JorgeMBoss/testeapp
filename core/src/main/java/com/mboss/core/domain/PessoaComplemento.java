package com.mboss.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A PessoaComplemento.
 */
@Table("pessoa_complemento")
public class PessoaComplemento implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column("ie")
    private Long ie;

    @Column("im")
    private Long im;

    @Column("email")
    private String email;

    @Transient
    private PessoaFisica pessoaFisica;

    @Transient
    private PessoaJuridica pessoaJuridica;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PessoaComplemento id(Long id) {
        this.id = id;
        return this;
    }

    public Long getIe() {
        return this.ie;
    }

    public PessoaComplemento ie(Long ie) {
        this.ie = ie;
        return this;
    }

    public void setIe(Long ie) {
        this.ie = ie;
    }

    public Long getIm() {
        return this.im;
    }

    public PessoaComplemento im(Long im) {
        this.im = im;
        return this;
    }

    public void setIm(Long im) {
        this.im = im;
    }

    public String getEmail() {
        return this.email;
    }

    public PessoaComplemento email(String email) {
        this.email = email;
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public PessoaFisica getPessoaFisica() {
        return this.pessoaFisica;
    }

    public PessoaComplemento pessoaFisica(PessoaFisica pessoaFisica) {
        this.setPessoaFisica(pessoaFisica);
        return this;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        if (this.pessoaFisica != null) {
            this.pessoaFisica.setPessoaComplemento(null);
        }
        if (pessoaFisica != null) {
            pessoaFisica.setPessoaComplemento(this);
        }
        this.pessoaFisica = pessoaFisica;
    }

    public PessoaJuridica getPessoaJuridica() {
        return this.pessoaJuridica;
    }

    public PessoaComplemento pessoaJuridica(PessoaJuridica pessoaJuridica) {
        this.setPessoaJuridica(pessoaJuridica);
        return this;
    }

    public void setPessoaJuridica(PessoaJuridica pessoaJuridica) {
        if (this.pessoaJuridica != null) {
            this.pessoaJuridica.setPessoaComplemento(null);
        }
        if (pessoaJuridica != null) {
            pessoaJuridica.setPessoaComplemento(this);
        }
        this.pessoaJuridica = pessoaJuridica;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PessoaComplemento)) {
            return false;
        }
        return id != null && id.equals(((PessoaComplemento) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PessoaComplemento{" +
            "id=" + getId() +
            ", ie=" + getIe() +
            ", im=" + getIm() +
            ", email='" + getEmail() + "'" +
            "}";
    }
}
