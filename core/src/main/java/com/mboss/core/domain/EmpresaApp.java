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
 * A EmpresaApp.
 */
@Table("empresa_app")
public class EmpresaApp implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Size(min = 1, max = 60)
    @Column("razao_social")
    private String razaoSocial;

    @Size(min = 2, max = 255)
    @Column("nome_fantasia")
    private String nomeFantasia;

    @Transient
    @JsonIgnoreProperties(
        value = {
            "pessoaEnderecos", "pessoaColaboradors", "pessoaFiscas", "pessoaJuridicas", "pessoaMedicos", "pessoaCriacaos", "empresaApp",
        },
        allowSetters = true
    )
    private Set<Pessoa> pessoas = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(
        value = { "criacaoCor", "criacaoRaca", "criacaoEspecie", "criacaoAnamnese", "empresaApp", "pessoaCriacao" },
        allowSetters = true
    )
    private Set<Criacao> criacaos = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "empresaApp", "criacao" }, allowSetters = true)
    private Set<CriacaoAnamnese> criacaoAnamnese = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "empresaApp" }, allowSetters = true)
    private Set<CriacaoConsumo> criacaoConsumos = new HashSet<>();

    @JsonIgnoreProperties(value = { "user", "empresaApps" }, allowSetters = true)
    @Transient
    private Set<UserApp> userApps = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EmpresaApp id(Long id) {
        this.id = id;
        return this;
    }

    public String getRazaoSocial() {
        return this.razaoSocial;
    }

    public EmpresaApp razaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
        return this;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getNomeFantasia() {
        return this.nomeFantasia;
    }

    public EmpresaApp nomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
        return this;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public Set<Pessoa> getPessoas() {
        return this.pessoas;
    }

    public EmpresaApp pessoas(Set<Pessoa> pessoas) {
        this.setPessoas(pessoas);
        return this;
    }

    public EmpresaApp addPessoa(Pessoa pessoa) {
        this.pessoas.add(pessoa);
        pessoa.setEmpresaApp(this);
        return this;
    }

    public EmpresaApp removePessoa(Pessoa pessoa) {
        this.pessoas.remove(pessoa);
        pessoa.setEmpresaApp(null);
        return this;
    }

    public void setPessoas(Set<Pessoa> pessoas) {
        if (this.pessoas != null) {
            this.pessoas.forEach(i -> i.setEmpresaApp(null));
        }
        if (pessoas != null) {
            pessoas.forEach(i -> i.setEmpresaApp(this));
        }
        this.pessoas = pessoas;
    }

    public Set<Criacao> getCriacaos() {
        return this.criacaos;
    }

    public EmpresaApp criacaos(Set<Criacao> criacaos) {
        this.setCriacaos(criacaos);
        return this;
    }

    public EmpresaApp addCriacao(Criacao criacao) {
        this.criacaos.add(criacao);
        criacao.setEmpresaApp(this);
        return this;
    }

    public EmpresaApp removeCriacao(Criacao criacao) {
        this.criacaos.remove(criacao);
        criacao.setEmpresaApp(null);
        return this;
    }

    public void setCriacaos(Set<Criacao> criacaos) {
        if (this.criacaos != null) {
            this.criacaos.forEach(i -> i.setEmpresaApp(null));
        }
        if (criacaos != null) {
            criacaos.forEach(i -> i.setEmpresaApp(this));
        }
        this.criacaos = criacaos;
    }

    public Set<CriacaoAnamnese> getCriacaoAnamnese() {
        return this.criacaoAnamnese;
    }

    public EmpresaApp criacaoAnamnese(Set<CriacaoAnamnese> criacaoAnamnese) {
        this.setCriacaoAnamnese(criacaoAnamnese);
        return this;
    }

    public EmpresaApp addCriacaoAnamnese(CriacaoAnamnese criacaoAnamnese) {
        this.criacaoAnamnese.add(criacaoAnamnese);
        criacaoAnamnese.setEmpresaApp(this);
        return this;
    }

    public EmpresaApp removeCriacaoAnamnese(CriacaoAnamnese criacaoAnamnese) {
        this.criacaoAnamnese.remove(criacaoAnamnese);
        criacaoAnamnese.setEmpresaApp(null);
        return this;
    }

    public void setCriacaoAnamnese(Set<CriacaoAnamnese> criacaoAnamnese) {
        if (this.criacaoAnamnese != null) {
            this.criacaoAnamnese.forEach(i -> i.setEmpresaApp(null));
        }
        if (criacaoAnamnese != null) {
            criacaoAnamnese.forEach(i -> i.setEmpresaApp(this));
        }
        this.criacaoAnamnese = criacaoAnamnese;
    }

    public Set<CriacaoConsumo> getCriacaoConsumos() {
        return this.criacaoConsumos;
    }

    public EmpresaApp criacaoConsumos(Set<CriacaoConsumo> criacaoConsumos) {
        this.setCriacaoConsumos(criacaoConsumos);
        return this;
    }

    public EmpresaApp addCriacaoConsumo(CriacaoConsumo criacaoConsumo) {
        this.criacaoConsumos.add(criacaoConsumo);
        criacaoConsumo.setEmpresaApp(this);
        return this;
    }

    public EmpresaApp removeCriacaoConsumo(CriacaoConsumo criacaoConsumo) {
        this.criacaoConsumos.remove(criacaoConsumo);
        criacaoConsumo.setEmpresaApp(null);
        return this;
    }

    public void setCriacaoConsumos(Set<CriacaoConsumo> criacaoConsumos) {
        if (this.criacaoConsumos != null) {
            this.criacaoConsumos.forEach(i -> i.setEmpresaApp(null));
        }
        if (criacaoConsumos != null) {
            criacaoConsumos.forEach(i -> i.setEmpresaApp(this));
        }
        this.criacaoConsumos = criacaoConsumos;
    }

    public Set<UserApp> getUserApps() {
        return this.userApps;
    }

    public EmpresaApp userApps(Set<UserApp> userApps) {
        this.setUserApps(userApps);
        return this;
    }

    public EmpresaApp addUserApp(UserApp userApp) {
        this.userApps.add(userApp);
        userApp.getEmpresaApps().add(this);
        return this;
    }

    public EmpresaApp removeUserApp(UserApp userApp) {
        this.userApps.remove(userApp);
        userApp.getEmpresaApps().remove(this);
        return this;
    }

    public void setUserApps(Set<UserApp> userApps) {
        this.userApps = userApps;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EmpresaApp)) {
            return false;
        }
        return id != null && id.equals(((EmpresaApp) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EmpresaApp{" +
            "id=" + getId() +
            ", razaoSocial='" + getRazaoSocial() + "'" +
            ", nomeFantasia='" + getNomeFantasia() + "'" +
            "}";
    }
}
