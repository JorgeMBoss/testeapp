package com.mboss.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A PessoaEndereco.
 */
@Table("pessoa_endereco")
public class PessoaEndereco implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column("pais")
    private String pais;

    @Column("estado")
    private String estado;

    @Column("cidade")
    private String cidade;

    @Column("bairro")
    private String bairro;

    @Column("numero_residencia")
    private Integer numeroResidencia;

    @Column("logradouro")
    private String logradouro;

    @Column("complemento")
    private String complemento;

    @JsonIgnoreProperties(
        value = { "pessoaEnderecos", "pessoaColaboradors", "pessoaFisicas", "pessoaJuridicas", "empresaApp", "pessoaMedico" },
        allowSetters = true
    )
    @Transient
    private Pessoa pessoa;

    @Column("pessoa_id")
    private Long pessoaId;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PessoaEndereco id(Long id) {
        this.id = id;
        return this;
    }

    public String getPais() {
        return this.pais;
    }

    public PessoaEndereco pais(String pais) {
        this.pais = pais;
        return this;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getEstado() {
        return this.estado;
    }

    public PessoaEndereco estado(String estado) {
        this.estado = estado;
        return this;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCidade() {
        return this.cidade;
    }

    public PessoaEndereco cidade(String cidade) {
        this.cidade = cidade;
        return this;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getBairro() {
        return this.bairro;
    }

    public PessoaEndereco bairro(String bairro) {
        this.bairro = bairro;
        return this;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public Integer getNumeroResidencia() {
        return this.numeroResidencia;
    }

    public PessoaEndereco numeroResidencia(Integer numeroResidencia) {
        this.numeroResidencia = numeroResidencia;
        return this;
    }

    public void setNumeroResidencia(Integer numeroResidencia) {
        this.numeroResidencia = numeroResidencia;
    }

    public String getLogradouro() {
        return this.logradouro;
    }

    public PessoaEndereco logradouro(String logradouro) {
        this.logradouro = logradouro;
        return this;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getComplemento() {
        return this.complemento;
    }

    public PessoaEndereco complemento(String complemento) {
        this.complemento = complemento;
        return this;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public Pessoa getPessoa() {
        return this.pessoa;
    }

    public PessoaEndereco pessoa(Pessoa pessoa) {
        this.setPessoa(pessoa);
        this.pessoaId = pessoa != null ? pessoa.getId() : null;
        return this;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
        this.pessoaId = pessoa != null ? pessoa.getId() : null;
    }

    public Long getPessoaId() {
        return this.pessoaId;
    }

    public void setPessoaId(Long pessoa) {
        this.pessoaId = pessoa;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PessoaEndereco)) {
            return false;
        }
        return id != null && id.equals(((PessoaEndereco) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PessoaEndereco{" +
            "id=" + getId() +
            ", pais='" + getPais() + "'" +
            ", estado='" + getEstado() + "'" +
            ", cidade='" + getCidade() + "'" +
            ", bairro='" + getBairro() + "'" +
            ", numeroResidencia=" + getNumeroResidencia() +
            ", logradouro='" + getLogradouro() + "'" +
            ", complemento='" + getComplemento() + "'" +
            "}";
    }
}
