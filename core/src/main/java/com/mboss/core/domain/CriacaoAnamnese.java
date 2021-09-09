package com.mboss.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
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

    @JsonIgnoreProperties(value = { "pessoas", "criacaos", "criacaoAnamnese", "criacaoConsumos", "userApps" }, allowSetters = true)
    @Transient
    private EmpresaApp empresaApp;

    @Column("empresa_app_id")
    private Long empresaAppId;

    @JsonIgnoreProperties(
        value = { "criacaoCor", "criacaoRaca", "criacaoEspecie", "criacaoAnamnese", "empresaApp", "pessoaCriacao" },
        allowSetters = true
    )
    @Transient
    private Criacao criacao;

    @Column("criacao_id")
    private Long criacaoId;

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

    public EmpresaApp getEmpresaApp() {
        return this.empresaApp;
    }

    public CriacaoAnamnese empresaApp(EmpresaApp empresaApp) {
        this.setEmpresaApp(empresaApp);
        this.empresaAppId = empresaApp != null ? empresaApp.getId() : null;
        return this;
    }

    public void setEmpresaApp(EmpresaApp empresaApp) {
        this.empresaApp = empresaApp;
        this.empresaAppId = empresaApp != null ? empresaApp.getId() : null;
    }

    public Long getEmpresaAppId() {
        return this.empresaAppId;
    }

    public void setEmpresaAppId(Long empresaApp) {
        this.empresaAppId = empresaApp;
    }

    public Criacao getCriacao() {
        return this.criacao;
    }

    public CriacaoAnamnese criacao(Criacao criacao) {
        this.setCriacao(criacao);
        this.criacaoId = criacao != null ? criacao.getId() : null;
        return this;
    }

    public void setCriacao(Criacao criacao) {
        this.criacao = criacao;
        this.criacaoId = criacao != null ? criacao.getId() : null;
    }

    public Long getCriacaoId() {
        return this.criacaoId;
    }

    public void setCriacaoId(Long criacao) {
        this.criacaoId = criacao;
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
