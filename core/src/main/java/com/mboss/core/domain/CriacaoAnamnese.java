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
 * A CriacaoAnamnese.
 */
@Table("criacao_anamnese")
public class CriacaoAnamnese implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column("descricao")
    private String descricao;

    @Column("id_medico")
    private Long idMedico;

    @JsonIgnoreProperties(
        value = { "criacaoCor", "criacaoRaca", "criacaoEspecie", "pessoaCriacao", "criacaoAnamnese", "criacaoConsumos" },
        allowSetters = true
    )
    @Transient
    private Set<Criacao> criacaos = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CriacaoAnamnese id(Long id) {
        this.id = id;
        return this;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public CriacaoAnamnese descricao(String descricao) {
        this.descricao = descricao;
        return this;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getIdMedico() {
        return this.idMedico;
    }

    public CriacaoAnamnese idMedico(Long idMedico) {
        this.idMedico = idMedico;
        return this;
    }

    public void setIdMedico(Long idMedico) {
        this.idMedico = idMedico;
    }

    public Set<Criacao> getCriacaos() {
        return this.criacaos;
    }

    public CriacaoAnamnese criacaos(Set<Criacao> criacaos) {
        this.setCriacaos(criacaos);
        return this;
    }

    public CriacaoAnamnese addCriacao(Criacao criacao) {
        this.criacaos.add(criacao);
        criacao.getCriacaoAnamnese().add(this);
        return this;
    }

    public CriacaoAnamnese removeCriacao(Criacao criacao) {
        this.criacaos.remove(criacao);
        criacao.getCriacaoAnamnese().remove(this);
        return this;
    }

    public void setCriacaos(Set<Criacao> criacaos) {
        this.criacaos = criacaos;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CriacaoAnamnese)) {
            return false;
        }
        return id != null && id.equals(((CriacaoAnamnese) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CriacaoAnamnese{" +
            "id=" + getId() +
            ", descricao='" + getDescricao() + "'" +
            ", idMedico=" + getIdMedico() +
            "}";
    }
}
