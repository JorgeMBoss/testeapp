package com.mboss.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A PessoaFuncao.
 */
@Table("pessoa_funcao")
public class PessoaFuncao implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @NotNull(message = "must not be null")
    @Column("descricao")
    private String descricao;

    @Column("ativo")
    private Boolean ativo;

    @Transient
    private PessoaColaborador pessoaColaborador;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PessoaFuncao id(Long id) {
        this.id = id;
        return this;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public PessoaFuncao descricao(String descricao) {
        this.descricao = descricao;
        return this;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Boolean getAtivo() {
        return this.ativo;
    }

    public PessoaFuncao ativo(Boolean ativo) {
        this.ativo = ativo;
        return this;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public PessoaColaborador getPessoaColaborador() {
        return this.pessoaColaborador;
    }

    public PessoaFuncao pessoaColaborador(PessoaColaborador pessoaColaborador) {
        this.setPessoaColaborador(pessoaColaborador);
        return this;
    }

    public void setPessoaColaborador(PessoaColaborador pessoaColaborador) {
        if (this.pessoaColaborador != null) {
            this.pessoaColaborador.setPessoaFuncao(null);
        }
        if (pessoaColaborador != null) {
            pessoaColaborador.setPessoaFuncao(this);
        }
        this.pessoaColaborador = pessoaColaborador;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PessoaFuncao)) {
            return false;
        }
        return id != null && id.equals(((PessoaFuncao) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PessoaFuncao{" +
            "id=" + getId() +
            ", descricao='" + getDescricao() + "'" +
            ", ativo='" + getAtivo() + "'" +
            "}";
    }
}
