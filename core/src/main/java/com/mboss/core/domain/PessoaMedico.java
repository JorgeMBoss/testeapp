package com.mboss.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A PessoaMedico.
 */
@Table("pessoa_medico")
public class PessoaMedico implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @NotNull(message = "must not be null")
    @Column("crm")
    private String crm;

    @Transient
    @JsonIgnoreProperties(
        value = { "pessoaEnderecos", "pessoaColaboradors", "pessoaFisicas", "pessoaJuridicas", "empresaApp", "pessoaMedico" },
        allowSetters = true
    )
    private Set<Pessoa> pessoas = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PessoaMedico id(Long id) {
        this.id = id;
        return this;
    }

    public String getCrm() {
        return this.crm;
    }

    public PessoaMedico crm(String crm) {
        this.crm = crm;
        return this;
    }

    public void setCrm(String crm) {
        this.crm = crm;
    }

    public Set<Pessoa> getPessoas() {
        return this.pessoas;
    }

    public PessoaMedico pessoas(Set<Pessoa> pessoas) {
        this.setPessoas(pessoas);
        return this;
    }

    public PessoaMedico addPessoa(Pessoa pessoa) {
        this.pessoas.add(pessoa);
        pessoa.setPessoaMedico(this);
        return this;
    }

    public PessoaMedico removePessoa(Pessoa pessoa) {
        this.pessoas.remove(pessoa);
        pessoa.setPessoaMedico(null);
        return this;
    }

    public void setPessoas(Set<Pessoa> pessoas) {
        if (this.pessoas != null) {
            this.pessoas.forEach(i -> i.setPessoaMedico(null));
        }
        if (pessoas != null) {
            pessoas.forEach(i -> i.setPessoaMedico(this));
        }
        this.pessoas = pessoas;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PessoaMedico)) {
            return false;
        }
        return id != null && id.equals(((PessoaMedico) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PessoaMedico{" +
            "id=" + getId() +
            ", crm='" + getCrm() + "'" +
            "}";
    }
}
