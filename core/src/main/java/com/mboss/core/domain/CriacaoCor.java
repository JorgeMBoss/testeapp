package com.mboss.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A CriacaoCor.
 */
@Table("criacao_cor")
public class CriacaoCor implements Serializable {

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

    public CriacaoCor id(Long id) {
        this.id = id;
        return this;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public CriacaoCor descricao(String descricao) {
        this.descricao = descricao;
        return this;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Boolean getAtivo() {
        return this.ativo;
    }

    public CriacaoCor ativo(Boolean ativo) {
        this.ativo = ativo;
        return this;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Criacao getCriacao() {
        return this.criacao;
    }

    public CriacaoCor criacao(Criacao criacao) {
        this.setCriacao(criacao);
        return this;
    }

    public void setCriacao(Criacao criacao) {
        if (this.criacao != null) {
            this.criacao.setCriacaoCor(null);
        }
        if (criacao != null) {
            criacao.setCriacaoCor(this);
        }
        this.criacao = criacao;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CriacaoCor)) {
            return false;
        }
        return id != null && id.equals(((CriacaoCor) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CriacaoCor{" +
            "id=" + getId() +
            ", descricao='" + getDescricao() + "'" +
            ", ativo='" + getAtivo() + "'" +
            "}";
    }
}
