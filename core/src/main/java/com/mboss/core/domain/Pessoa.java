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

    @NotNull(message = "must not be null")
    @Column("id_empresa")
    private Long idEmpresa;

    @Column("data_cadastro")
    private LocalDate dataCadastro;

    @Column("is_pessoa_fisica")
    private Boolean isPessoaFisica;

    @Column("is_colaborador")
    private Boolean isColaborador;

    @Column("is_medico")
    private Boolean isMedico;

    @Column("ativo")
    private Boolean ativo;

    @Transient
    @JsonIgnoreProperties(value = { "pessoa" }, allowSetters = true)
    private Set<PessoaEndereco> pessoaEnderecos = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "pessoaFuncao", "pessoaParentescos", "pessoa" }, allowSetters = true)
    private Set<PessoaColaborador> pessoaColaboradors = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "pessoaComplemento", "pessoaParentescos", "pessoa" }, allowSetters = true)
    private Set<PessoaFisica> pessoaFisicas = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "pessoaComplemento", "pessoa" }, allowSetters = true)
    private Set<PessoaJuridica> pessoaJuridicas = new HashSet<>();

    @JsonIgnoreProperties(value = { "pessoas", "userApps" }, allowSetters = true)
    @Transient
    private EmpresaApp empresaApp;

    @Column("empresa_app_id")
    private Long empresaAppId;

    @JsonIgnoreProperties(value = { "pessoas" }, allowSetters = true)
    @Transient
    private PessoaMedico pessoaMedico;

    @Column("pessoa_medico_id")
    private Long pessoaMedicoId;

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

    public Boolean getIsPessoaFisica() {
        return this.isPessoaFisica;
    }

    public Pessoa isPessoaFisica(Boolean isPessoaFisica) {
        this.isPessoaFisica = isPessoaFisica;
        return this;
    }

    public void setIsPessoaFisica(Boolean isPessoaFisica) {
        this.isPessoaFisica = isPessoaFisica;
    }

    public Boolean getIsColaborador() {
        return this.isColaborador;
    }

    public Pessoa isColaborador(Boolean isColaborador) {
        this.isColaborador = isColaborador;
        return this;
    }

    public void setIsColaborador(Boolean isColaborador) {
        this.isColaborador = isColaborador;
    }

    public Boolean getIsMedico() {
        return this.isMedico;
    }

    public Pessoa isMedico(Boolean isMedico) {
        this.isMedico = isMedico;
        return this;
    }

    public void setIsMedico(Boolean isMedico) {
        this.isMedico = isMedico;
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

    public Set<PessoaFisica> getPessoaFisicas() {
        return this.pessoaFisicas;
    }

    public Pessoa pessoaFisicas(Set<PessoaFisica> pessoaFisicas) {
        this.setPessoaFisicas(pessoaFisicas);
        return this;
    }

    public Pessoa addPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisicas.add(pessoaFisica);
        pessoaFisica.setPessoa(this);
        return this;
    }

    public Pessoa removePessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisicas.remove(pessoaFisica);
        pessoaFisica.setPessoa(null);
        return this;
    }

    public void setPessoaFisicas(Set<PessoaFisica> pessoaFisicas) {
        if (this.pessoaFisicas != null) {
            this.pessoaFisicas.forEach(i -> i.setPessoa(null));
        }
        if (pessoaFisicas != null) {
            pessoaFisicas.forEach(i -> i.setPessoa(this));
        }
        this.pessoaFisicas = pessoaFisicas;
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

    public PessoaMedico getPessoaMedico() {
        return this.pessoaMedico;
    }

    public Pessoa pessoaMedico(PessoaMedico pessoaMedico) {
        this.setPessoaMedico(pessoaMedico);
        this.pessoaMedicoId = pessoaMedico != null ? pessoaMedico.getId() : null;
        return this;
    }

    public void setPessoaMedico(PessoaMedico pessoaMedico) {
        this.pessoaMedico = pessoaMedico;
        this.pessoaMedicoId = pessoaMedico != null ? pessoaMedico.getId() : null;
    }

    public Long getPessoaMedicoId() {
        return this.pessoaMedicoId;
    }

    public void setPessoaMedicoId(Long pessoaMedico) {
        this.pessoaMedicoId = pessoaMedico;
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
            ", isPessoaFisica='" + getIsPessoaFisica() + "'" +
            ", isColaborador='" + getIsColaborador() + "'" +
            ", isMedico='" + getIsMedico() + "'" +
            ", ativo='" + getAtivo() + "'" +
            "}";
    }
}
