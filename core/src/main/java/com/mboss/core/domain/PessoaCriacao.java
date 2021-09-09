package com.mboss.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A PessoaCriacao.
 */
@Table("pessoa_criacao")
public class PessoaCriacao implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column("id_pessoa")
    private Long idPessoa;

    @Column("id_criacao")
    private Long idCriacao;

    @Transient
    @JsonIgnoreProperties(
        value = { "criacaoCor", "criacaoRaca", "criacaoEspecie", "pessoaCriacao", "criacaoAnamnese", "criacaoConsumos" },
        allowSetters = true
    )
    private Set<Criacao> criacaos = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PessoaCriacao id(Long id) {
        this.id = id;
        return this;
    }

    public Long getIdPessoa() {
        return this.idPessoa;
    }

    public PessoaCriacao idPessoa(Long idPessoa) {
        this.idPessoa = idPessoa;
        return this;
    }

    public void setIdPessoa(Long idPessoa) {
        this.idPessoa = idPessoa;
    }

    public Long getIdCriacao() {
        return this.idCriacao;
    }

    public PessoaCriacao idCriacao(Long idCriacao) {
        this.idCriacao = idCriacao;
        return this;
    }

    public void setIdCriacao(Long idCriacao) {
        this.idCriacao = idCriacao;
    }

    public Set<Criacao> getCriacaos() {
        return this.criacaos;
    }

    public PessoaCriacao criacaos(Set<Criacao> criacaos) {
        this.setCriacaos(criacaos);
        return this;
    }

    public PessoaCriacao addCriacao(Criacao criacao) {
        this.criacaos.add(criacao);
        criacao.setPessoaCriacao(this);
        return this;
    }

    public PessoaCriacao removeCriacao(Criacao criacao) {
        this.criacaos.remove(criacao);
        criacao.setPessoaCriacao(null);
        return this;
    }

    public void setCriacaos(Set<Criacao> criacaos) {
        if (this.criacaos != null) {
            this.criacaos.forEach(i -> i.setPessoaCriacao(null));
        }
        if (criacaos != null) {
            criacaos.forEach(i -> i.setPessoaCriacao(this));
        }
        this.criacaos = criacaos;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PessoaCriacao)) {
            return false;
        }
        return id != null && id.equals(((PessoaCriacao) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PessoaCriacao{" +
            "id=" + getId() +
            ", idPessoa=" + getIdPessoa() +
            ", idCriacao=" + getIdCriacao() +
            "}";
    }
}
