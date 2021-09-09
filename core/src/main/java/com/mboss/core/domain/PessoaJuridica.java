package com.mboss.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A PessoaJuridica.
 */
@Table("pessoa_juridica")
public class PessoaJuridica implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Size(min = 14, max = 14)
    @Column("cnpj")
    private String cnpj;

    @Column("nome_razao")
    private String nomeRazao;

    @Column("nome_fantasia")
    private String nomeFantasia;

    private Long pessoaComplementoId;

    @Transient
    private PessoaComplemento pessoaComplemento;

    @JsonIgnoreProperties(
        value = {
            "pessoaEnderecos", "pessoaColaboradors", "pessoaFiscas", "pessoaJuridicas", "pessoaMedicos", "pessoaCriacaos", "empresaApp",
        },
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

    public PessoaJuridica id(Long id) {
        this.id = id;
        return this;
    }

    public String getCnpj() {
        return this.cnpj;
    }

    public PessoaJuridica cnpj(String cnpj) {
        this.cnpj = cnpj;
        return this;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getNomeRazao() {
        return this.nomeRazao;
    }

    public PessoaJuridica nomeRazao(String nomeRazao) {
        this.nomeRazao = nomeRazao;
        return this;
    }

    public void setNomeRazao(String nomeRazao) {
        this.nomeRazao = nomeRazao;
    }

    public String getNomeFantasia() {
        return this.nomeFantasia;
    }

    public PessoaJuridica nomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
        return this;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public PessoaComplemento getPessoaComplemento() {
        return this.pessoaComplemento;
    }

    public PessoaJuridica pessoaComplemento(PessoaComplemento pessoaComplemento) {
        this.setPessoaComplemento(pessoaComplemento);
        this.pessoaComplementoId = pessoaComplemento != null ? pessoaComplemento.getId() : null;
        return this;
    }

    public void setPessoaComplemento(PessoaComplemento pessoaComplemento) {
        this.pessoaComplemento = pessoaComplemento;
        this.pessoaComplementoId = pessoaComplemento != null ? pessoaComplemento.getId() : null;
    }

    public Long getPessoaComplementoId() {
        return this.pessoaComplementoId;
    }

    public void setPessoaComplementoId(Long pessoaComplemento) {
        this.pessoaComplementoId = pessoaComplemento;
    }

    public Pessoa getPessoa() {
        return this.pessoa;
    }

    public PessoaJuridica pessoa(Pessoa pessoa) {
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
        if (!(o instanceof PessoaJuridica)) {
            return false;
        }
        return id != null && id.equals(((PessoaJuridica) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PessoaJuridica{" +
            "id=" + getId() +
            ", cnpj='" + getCnpj() + "'" +
            ", nomeRazao='" + getNomeRazao() + "'" +
            ", nomeFantasia='" + getNomeFantasia() + "'" +
            "}";
    }
}
