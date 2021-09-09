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
        value = { "pessoaEnderecos", "pessoaColaboradors", "pessoaFisicas", "pessoaJuridicas", "empresaApp", "pessoaMedico" },
        allowSetters = true
    )
    private Set<Pessoa> pessoas = new HashSet<>();

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
