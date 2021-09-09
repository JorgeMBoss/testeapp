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
 * A Pessoa.
 */
@Table("pessoa")
public class Pessoa implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column("id_empresa")
    private Long idEmpresa;

    @Column("data_cadastro")
    private LocalDate dataCadastro;

    @Column("ativo")
    private Boolean ativo;

    @Transient
    @JsonIgnoreProperties(value = { "pessoa" }, allowSetters = true)
    private Set<PessoaEndereco> pessoaEnderecos = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "pessoaFuncao", "pessoa" }, allowSetters = true)
    private Set<PessoaColaborador> pessoaColaboradors = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "pessoaComplemento", "pessoaParentescos", "pessoa" }, allowSetters = true)
    private Set<PessoaFisca> pessoaFiscas = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "pessoaComplemento", "pessoa" }, allowSetters = true)
    private Set<PessoaJuridica> pessoaJuridicas = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "pessoa" }, allowSetters = true)
    private Set<PessoaMedico> pessoaMedicos = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "criacaos", "pessoa" }, allowSetters = true)
    private Set<PessoaCriacao> pessoaCriacaos = new HashSet<>();

    @JsonIgnoreProperties(value = { "pessoas", "criacaos", "criacaoAnamnese", "criacaoConsumos", "userApps" }, allowSetters = true)
    @Transient
    private EmpresaApp empresaApp;

    @Column("empresa_app_id")
    private Long empresaAppId;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pessoa id(Long id) {
        this.id = id;
        return this;
    }

    public Long getIdEmpresa() {
        return this.idEmpresa;
    }

    public Pessoa idEmpresa(Long idEmpresa) {
        this.idEmpresa = idEmpresa;
        return this;
    }

    public void setIdEmpresa(Long idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public LocalDate getDataCadastro() {
        return this.dataCadastro;
    }

    public Pessoa dataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
        return this;
    }

    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Boolean getAtivo() {
        return this.ativo;
    }

    public Pessoa ativo(Boolean ativo) {
        this.ativo = ativo;
        return this;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Set<PessoaEndereco> getPessoaEnderecos() {
        return this.pessoaEnderecos;
    }

    public Pessoa pessoaEnderecos(Set<PessoaEndereco> pessoaEnderecos) {
        this.setPessoaEnderecos(pessoaEnderecos);
        return this;
    }

    public Pessoa addPessoaEndereco(PessoaEndereco pessoaEndereco) {
        this.pessoaEnderecos.add(pessoaEndereco);
        pessoaEndereco.setPessoa(this);
        return this;
    }

    public Pessoa removePessoaEndereco(PessoaEndereco pessoaEndereco) {
        this.pessoaEnderecos.remove(pessoaEndereco);
        pessoaEndereco.setPessoa(null);
        return this;
    }

    public void setPessoaEnderecos(Set<PessoaEndereco> pessoaEnderecos) {
        if (this.pessoaEnderecos != null) {
            this.pessoaEnderecos.forEach(i -> i.setPessoa(null));
        }
        if (pessoaEnderecos != null) {
            pessoaEnderecos.forEach(i -> i.setPessoa(this));
        }
        this.pessoaEnderecos = pessoaEnderecos;
    }

    public Set<PessoaColaborador> getPessoaColaboradors() {
        return this.pessoaColaboradors;
    }

    public Pessoa pessoaColaboradors(Set<PessoaColaborador> pessoaColaboradors) {
        this.setPessoaColaboradors(pessoaColaboradors);
        return this;
    }

    public Pessoa addPessoaColaborador(PessoaColaborador pessoaColaborador) {
        this.pessoaColaboradors.add(pessoaColaborador);
        pessoaColaborador.setPessoa(this);
        return this;
    }

    public Pessoa removePessoaColaborador(PessoaColaborador pessoaColaborador) {
        this.pessoaColaboradors.remove(pessoaColaborador);
        pessoaColaborador.setPessoa(null);
        return this;
    }

    public void setPessoaColaboradors(Set<PessoaColaborador> pessoaColaboradors) {
        if (this.pessoaColaboradors != null) {
            this.pessoaColaboradors.forEach(i -> i.setPessoa(null));
        }
        if (pessoaColaboradors != null) {
            pessoaColaboradors.forEach(i -> i.setPessoa(this));
        }
        this.pessoaColaboradors = pessoaColaboradors;
    }

    public Set<PessoaFisca> getPessoaFiscas() {
        return this.pessoaFiscas;
    }

    public Pessoa pessoaFiscas(Set<PessoaFisca> pessoaFiscas) {
        this.setPessoaFiscas(pessoaFiscas);
        return this;
    }

    public Pessoa addPessoaFisca(PessoaFisca pessoaFisca) {
        this.pessoaFiscas.add(pessoaFisca);
        pessoaFisca.setPessoa(this);
        return this;
    }

    public Pessoa removePessoaFisca(PessoaFisca pessoaFisca) {
        this.pessoaFiscas.remove(pessoaFisca);
        pessoaFisca.setPessoa(null);
        return this;
    }

    public void setPessoaFiscas(Set<PessoaFisca> pessoaFiscas) {
        if (this.pessoaFiscas != null) {
            this.pessoaFiscas.forEach(i -> i.setPessoa(null));
        }
        if (pessoaFiscas != null) {
            pessoaFiscas.forEach(i -> i.setPessoa(this));
        }
        this.pessoaFiscas = pessoaFiscas;
    }

    public Set<PessoaJuridica> getPessoaJuridicas() {
        return this.pessoaJuridicas;
    }

    public Pessoa pessoaJuridicas(Set<PessoaJuridica> pessoaJuridicas) {
        this.setPessoaJuridicas(pessoaJuridicas);
        return this;
    }

    public Pessoa addPessoaJuridica(PessoaJuridica pessoaJuridica) {
        this.pessoaJuridicas.add(pessoaJuridica);
        pessoaJuridica.setPessoa(this);
        return this;
    }

    public Pessoa removePessoaJuridica(PessoaJuridica pessoaJuridica) {
        this.pessoaJuridicas.remove(pessoaJuridica);
        pessoaJuridica.setPessoa(null);
        return this;
    }

    public void setPessoaJuridicas(Set<PessoaJuridica> pessoaJuridicas) {
        if (this.pessoaJuridicas != null) {
            this.pessoaJuridicas.forEach(i -> i.setPessoa(null));
        }
        if (pessoaJuridicas != null) {
            pessoaJuridicas.forEach(i -> i.setPessoa(this));
        }
        this.pessoaJuridicas = pessoaJuridicas;
    }

    public Set<PessoaMedico> getPessoaMedicos() {
        return this.pessoaMedicos;
    }

    public Pessoa pessoaMedicos(Set<PessoaMedico> pessoaMedicos) {
        this.setPessoaMedicos(pessoaMedicos);
        return this;
    }

    public Pessoa addPessoaMedico(PessoaMedico pessoaMedico) {
        this.pessoaMedicos.add(pessoaMedico);
        pessoaMedico.setPessoa(this);
        return this;
    }

    public Pessoa removePessoaMedico(PessoaMedico pessoaMedico) {
        this.pessoaMedicos.remove(pessoaMedico);
        pessoaMedico.setPessoa(null);
        return this;
    }

    public void setPessoaMedicos(Set<PessoaMedico> pessoaMedicos) {
        if (this.pessoaMedicos != null) {
            this.pessoaMedicos.forEach(i -> i.setPessoa(null));
        }
        if (pessoaMedicos != null) {
            pessoaMedicos.forEach(i -> i.setPessoa(this));
        }
        this.pessoaMedicos = pessoaMedicos;
    }

    public Set<PessoaCriacao> getPessoaCriacaos() {
        return this.pessoaCriacaos;
    }

    public Pessoa pessoaCriacaos(Set<PessoaCriacao> pessoaCriacaos) {
        this.setPessoaCriacaos(pessoaCriacaos);
        return this;
    }

    public Pessoa addPessoaCriacao(PessoaCriacao pessoaCriacao) {
        this.pessoaCriacaos.add(pessoaCriacao);
        pessoaCriacao.setPessoa(this);
        return this;
    }

    public Pessoa removePessoaCriacao(PessoaCriacao pessoaCriacao) {
        this.pessoaCriacaos.remove(pessoaCriacao);
        pessoaCriacao.setPessoa(null);
        return this;
    }

    public void setPessoaCriacaos(Set<PessoaCriacao> pessoaCriacaos) {
        if (this.pessoaCriacaos != null) {
            this.pessoaCriacaos.forEach(i -> i.setPessoa(null));
        }
        if (pessoaCriacaos != null) {
            pessoaCriacaos.forEach(i -> i.setPessoa(this));
        }
        this.pessoaCriacaos = pessoaCriacaos;
    }

    public EmpresaApp getEmpresaApp() {
        return this.empresaApp;
    }

    public Pessoa empresaApp(EmpresaApp empresaApp) {
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

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pessoa)) {
            return false;
        }
        return id != null && id.equals(((Pessoa) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Pessoa{" +
            "id=" + getId() +
            ", idEmpresa=" + getIdEmpresa() +
            ", dataCadastro='" + getDataCadastro() + "'" +
            ", ativo='" + getAtivo() + "'" +
            "}";
    }
}
