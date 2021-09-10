package com.mboss.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A CriacaoConsumo.
 */
@Table("criacao_consumo")
public class CriacaoConsumo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column("data_sistema")
    private LocalDate dataSistema;

    @Column("data_venda")
    private LocalDate dataVenda;

    @Column("data_aviso")
    private LocalDate dataAviso;

    @Column("anotacao")
    private String anotacao;

    @Column("status")
    private Boolean status;

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

    public CriacaoConsumo id(Long id) {
        this.id = id;
        return this;
    }

    public LocalDate getDataSistema() {
        return this.dataSistema;
    }

    public CriacaoConsumo dataSistema(LocalDate dataSistema) {
        this.dataSistema = dataSistema;
        return this;
    }

    public void setDataSistema(LocalDate dataSistema) {
        this.dataSistema = dataSistema;
    }

    public LocalDate getDataVenda() {
        return this.dataVenda;
    }

    public CriacaoConsumo dataVenda(LocalDate dataVenda) {
        this.dataVenda = dataVenda;
        return this;
    }

    public void setDataVenda(LocalDate dataVenda) {
        this.dataVenda = dataVenda;
    }

    public LocalDate getDataAviso() {
        return this.dataAviso;
    }

    public CriacaoConsumo dataAviso(LocalDate dataAviso) {
        this.dataAviso = dataAviso;
        return this;
    }

    public void setDataAviso(LocalDate dataAviso) {
        this.dataAviso = dataAviso;
    }

    public String getAnotacao() {
        return this.anotacao;
    }

    public CriacaoConsumo anotacao(String anotacao) {
        this.anotacao = anotacao;
        return this;
    }

    public void setAnotacao(String anotacao) {
        this.anotacao = anotacao;
    }

    public Boolean getStatus() {
        return this.status;
    }

    public CriacaoConsumo status(Boolean status) {
        this.status = status;
        return this;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Set<Criacao> getCriacaos() {
        return this.criacaos;
    }

    public CriacaoConsumo criacaos(Set<Criacao> criacaos) {
        this.setCriacaos(criacaos);
        return this;
    }

    public CriacaoConsumo addCriacao(Criacao criacao) {
        this.criacaos.add(criacao);
        criacao.getCriacaoConsumos().add(this);
        return this;
    }

    public CriacaoConsumo removeCriacao(Criacao criacao) {
        this.criacaos.remove(criacao);
        criacao.getCriacaoConsumos().remove(this);
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
        if (!(o instanceof CriacaoConsumo)) {
            return false;
        }
        return id != null && id.equals(((CriacaoConsumo) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CriacaoConsumo{" +
            "id=" + getId() +
            ", dataSistema='" + getDataSistema() + "'" +
            ", dataVenda='" + getDataVenda() + "'" +
            ", dataAviso='" + getDataAviso() + "'" +
            ", anotacao='" + getAnotacao() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
