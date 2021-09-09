package com.mboss.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Criacao.
 */
@Table("criacao")
public class Criacao implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @NotNull(message = "must not be null")
    @Column("id_empresa")
    private Long idEmpresa;

    @NotNull(message = "must not be null")
    @Column("nome")
    private String nome;

    @Column("sexo")
    private String sexo;

    @Column("porte")
    private String porte;

    @Column("idade")
    private Integer idade;

    @Column("data_nascimento")
    private LocalDate dataNascimento;

    @Column("castrado")
    private Boolean castrado;

    @Column("anotacao")
    private String anotacao;

    @Column("pedigree")
    private Boolean pedigree;

    @Column("ativo")
    private Boolean ativo;

    private Long criacaoCorId;

    @Transient
    private CriacaoCor criacaoCor;

    private Long criacaoRacaId;

    @Transient
    private CriacaoRaca criacaoRaca;

    private Long criacaoEspecieId;

    @Transient
    private CriacaoEspecie criacaoEspecie;

    @Transient
    @JsonIgnoreProperties(value = { "empresaApp", "criacao" }, allowSetters = true)
    private Set<CriacaoAnamnese> criacaoAnamnese = new HashSet<>();

    @JsonIgnoreProperties(value = { "pessoas", "criacaos", "criacaoAnamnese", "criacaoConsumos", "userApps" }, allowSetters = true)
    @Transient
    private EmpresaApp empresaApp;

    @Column("empresa_app_id")
    private Long empresaAppId;

    @JsonIgnoreProperties(value = { "criacaos", "pessoa" }, allowSetters = true)
    @Transient
    private PessoaCriacao pessoaCriacao;

    @Column("pessoa_criacao_id")
    private Long pessoaCriacaoId;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Criacao id(Long id) {
        this.id = id;
        return this;
    }

    public Long getIdEmpresa() {
        return this.idEmpresa;
    }

    public Criacao idEmpresa(Long idEmpresa) {
        this.idEmpresa = idEmpresa;
        return this;
    }

    public void setIdEmpresa(Long idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getNome() {
        return this.nome;
    }

    public Criacao nome(String nome) {
        this.nome = nome;
        return this;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSexo() {
        return this.sexo;
    }

    public Criacao sexo(String sexo) {
        this.sexo = sexo;
        return this;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getPorte() {
        return this.porte;
    }

    public Criacao porte(String porte) {
        this.porte = porte;
        return this;
    }

    public void setPorte(String porte) {
        this.porte = porte;
    }

    public Integer getIdade() {
        return this.idade;
    }

    public Criacao idade(Integer idade) {
        this.idade = idade;
        return this;
    }

    public void setIdade(Integer idade) {
        this.idade = idade;
    }

    public LocalDate getDataNascimento() {
        return this.dataNascimento;
    }

    public Criacao dataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
        return this;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Boolean getCastrado() {
        return this.castrado;
    }

    public Criacao castrado(Boolean castrado) {
        this.castrado = castrado;
        return this;
    }

    public void setCastrado(Boolean castrado) {
        this.castrado = castrado;
    }

    public String getAnotacao() {
        return this.anotacao;
    }

    public Criacao anotacao(String anotacao) {
        this.anotacao = anotacao;
        return this;
    }

    public void setAnotacao(String anotacao) {
        this.anotacao = anotacao;
    }

    public Boolean getPedigree() {
        return this.pedigree;
    }

    public Criacao pedigree(Boolean pedigree) {
        this.pedigree = pedigree;
        return this;
    }

    public void setPedigree(Boolean pedigree) {
        this.pedigree = pedigree;
    }

    public Boolean getAtivo() {
        return this.ativo;
    }

    public Criacao ativo(Boolean ativo) {
        this.ativo = ativo;
        return this;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public CriacaoCor getCriacaoCor() {
        return this.criacaoCor;
    }

    public Criacao criacaoCor(CriacaoCor criacaoCor) {
        this.setCriacaoCor(criacaoCor);
        this.criacaoCorId = criacaoCor != null ? criacaoCor.getId() : null;
        return this;
    }

    public void setCriacaoCor(CriacaoCor criacaoCor) {
        this.criacaoCor = criacaoCor;
        this.criacaoCorId = criacaoCor != null ? criacaoCor.getId() : null;
    }

    public Long getCriacaoCorId() {
        return this.criacaoCorId;
    }

    public void setCriacaoCorId(Long criacaoCor) {
        this.criacaoCorId = criacaoCor;
    }

    public CriacaoRaca getCriacaoRaca() {
        return this.criacaoRaca;
    }

    public Criacao criacaoRaca(CriacaoRaca criacaoRaca) {
        this.setCriacaoRaca(criacaoRaca);
        this.criacaoRacaId = criacaoRaca != null ? criacaoRaca.getId() : null;
        return this;
    }

    public void setCriacaoRaca(CriacaoRaca criacaoRaca) {
        this.criacaoRaca = criacaoRaca;
        this.criacaoRacaId = criacaoRaca != null ? criacaoRaca.getId() : null;
    }

    public Long getCriacaoRacaId() {
        return this.criacaoRacaId;
    }

    public void setCriacaoRacaId(Long criacaoRaca) {
        this.criacaoRacaId = criacaoRaca;
    }

    public CriacaoEspecie getCriacaoEspecie() {
        return this.criacaoEspecie;
    }

    public Criacao criacaoEspecie(CriacaoEspecie criacaoEspecie) {
        this.setCriacaoEspecie(criacaoEspecie);
        this.criacaoEspecieId = criacaoEspecie != null ? criacaoEspecie.getId() : null;
        return this;
    }

    public void setCriacaoEspecie(CriacaoEspecie criacaoEspecie) {
        this.criacaoEspecie = criacaoEspecie;
        this.criacaoEspecieId = criacaoEspecie != null ? criacaoEspecie.getId() : null;
    }

    public Long getCriacaoEspecieId() {
        return this.criacaoEspecieId;
    }

    public void setCriacaoEspecieId(Long criacaoEspecie) {
        this.criacaoEspecieId = criacaoEspecie;
    }

    public Set<CriacaoAnamnese> getCriacaoAnamnese() {
        return this.criacaoAnamnese;
    }

    public Criacao criacaoAnamnese(Set<CriacaoAnamnese> criacaoAnamnese) {
        this.setCriacaoAnamnese(criacaoAnamnese);
        return this;
    }

    public Criacao addCriacaoAnamnese(CriacaoAnamnese criacaoAnamnese) {
        this.criacaoAnamnese.add(criacaoAnamnese);
        criacaoAnamnese.setCriacao(this);
        return this;
    }

    public Criacao removeCriacaoAnamnese(CriacaoAnamnese criacaoAnamnese) {
        this.criacaoAnamnese.remove(criacaoAnamnese);
        criacaoAnamnese.setCriacao(null);
        return this;
    }

    public void setCriacaoAnamnese(Set<CriacaoAnamnese> criacaoAnamnese) {
        if (this.criacaoAnamnese != null) {
            this.criacaoAnamnese.forEach(i -> i.setCriacao(null));
        }
        if (criacaoAnamnese != null) {
            criacaoAnamnese.forEach(i -> i.setCriacao(this));
        }
        this.criacaoAnamnese = criacaoAnamnese;
    }

    public EmpresaApp getEmpresaApp() {
        return this.empresaApp;
    }

    public Criacao empresaApp(EmpresaApp empresaApp) {
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

    public PessoaCriacao getPessoaCriacao() {
        return this.pessoaCriacao;
    }

    public Criacao pessoaCriacao(PessoaCriacao pessoaCriacao) {
        this.setPessoaCriacao(pessoaCriacao);
        this.pessoaCriacaoId = pessoaCriacao != null ? pessoaCriacao.getId() : null;
        return this;
    }

    public void setPessoaCriacao(PessoaCriacao pessoaCriacao) {
        this.pessoaCriacao = pessoaCriacao;
        this.pessoaCriacaoId = pessoaCriacao != null ? pessoaCriacao.getId() : null;
    }

    public Long getPessoaCriacaoId() {
        return this.pessoaCriacaoId;
    }

    public void setPessoaCriacaoId(Long pessoaCriacao) {
        this.pessoaCriacaoId = pessoaCriacao;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Criacao)) {
            return false;
        }
        return id != null && id.equals(((Criacao) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Criacao{" +
            "id=" + getId() +
            ", idEmpresa=" + getIdEmpresa() +
            ", nome='" + getNome() + "'" +
            ", sexo='" + getSexo() + "'" +
            ", porte='" + getPorte() + "'" +
            ", idade=" + getIdade() +
            ", dataNascimento='" + getDataNascimento() + "'" +
            ", castrado='" + getCastrado() + "'" +
            ", anotacao='" + getAnotacao() + "'" +
            ", pedigree='" + getPedigree() + "'" +
            ", ativo='" + getAtivo() + "'" +
            "}";
    }
}
