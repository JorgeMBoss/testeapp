package com.mboss.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A CriacaoRaca.
 */
@Table("criacao_raca")
public class CriacaoRaca implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column("descricao")
    private String descricao;

    @Column("ativo")
    private Boolean ativo;

    @Transient
    private Criacao criacao;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CriacaoRaca id(Long id) {
        this.id = id;
        return this;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public CriacaoRaca descricao(String descricao) {
        this.descricao = descricao;
        return this;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Boolean getAtivo() {
        return this.ativo;
    }

    public CriacaoRaca ativo(Boolean ativo) {
        this.ativo = ativo;
        return this;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Criacao getCriacao() {
        return this.criacao;
    }

    public CriacaoRaca criacao(Criacao criacao) {
        this.setCriacao(criacao);
        return this;
    }

    public void setCriacao(Criacao criacao) {
        if (this.criacao != null) {
            this.criacao.setCriacaoRaca(null);
        }
        if (criacao != null) {
            criacao.setCriacaoRaca(this);
        }
        this.criacao = criacao;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CriacaoRaca)) {
            return false;
        }
        return id != null && id.equals(((CriacaoRaca) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CriacaoRaca{" +
            "id=" + getId() +
            ", descricao='" + getDescricao() + "'" +
            ", ativo='" + getAtivo() + "'" +
            "}";
    }
}
