package com.mboss.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A PessoaParentesco.
 */
@Table("pessoa_parentesco")
public class PessoaParentesco implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column("cpf")
    private String cpf;

    @Column("nome")
    private String nome;

    @Column("email")
    private String email;

    @JsonIgnoreProperties(value = { "pessoaComplemento", "pessoaParentescos", "pessoa" }, allowSetters = true)
    @Transient
    private PessoaFisca pessoaFisca;

    @Column("pessoa_fisca_id")
    private Long pessoaFiscaId;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PessoaParentesco id(Long id) {
        this.id = id;
        return this;
    }

    public String getCpf() {
        return this.cpf;
    }

    public PessoaParentesco cpf(String cpf) {
        this.cpf = cpf;
        return this;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return this.nome;
    }

    public PessoaParentesco nome(String nome) {
        this.nome = nome;
        return this;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return this.email;
    }

    public PessoaParentesco email(String email) {
        this.email = email;
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public PessoaFisca getPessoaFisca() {
        return this.pessoaFisca;
    }

    public PessoaParentesco pessoaFisca(PessoaFisca pessoaFisca) {
        this.setPessoaFisca(pessoaFisca);
        this.pessoaFiscaId = pessoaFisca != null ? pessoaFisca.getId() : null;
        return this;
    }

    public void setPessoaFisca(PessoaFisca pessoaFisca) {
        this.pessoaFisca = pessoaFisca;
        this.pessoaFiscaId = pessoaFisca != null ? pessoaFisca.getId() : null;
    }

    public Long getPessoaFiscaId() {
        return this.pessoaFiscaId;
    }

    public void setPessoaFiscaId(Long pessoaFisca) {
        this.pessoaFiscaId = pessoaFisca;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PessoaParentesco)) {
            return false;
        }
        return id != null && id.equals(((PessoaParentesco) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PessoaParentesco{" +
            "id=" + getId() +
            ", cpf='" + getCpf() + "'" +
            ", nome='" + getNome() + "'" +
            ", email='" + getEmail() + "'" +
            "}";
    }
}
