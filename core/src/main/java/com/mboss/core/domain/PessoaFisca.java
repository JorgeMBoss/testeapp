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
 * A PessoaFisca.
 */
@Table("pessoa_fisca")
public class PessoaFisca implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Size(min = 11, max = 11)
    @Column("cpf")
    private String cpf;

    @Column("rg")
    private String rg;

    @Column("data_nascimento")
    private LocalDate dataNascimento;

    @Column("idade")
    private Integer idade;

    @Column("sexo")
    private String sexo;

    @Column("cor")
    private String cor;

    @Column("estado_civil")
    private String estadoCivil;

    @Column("naturalidade")
    private String naturalidade;

    @Column("nacionalidade")
    private String nacionalidade;

    private Long pessoaComplementoId;

    @Transient
    private PessoaComplemento pessoaComplemento;

    @Transient
    @JsonIgnoreProperties(value = { "pessoaFisca" }, allowSetters = true)
    private Set<PessoaParentesco> pessoaParentescos = new HashSet<>();

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

    public PessoaFisca id(Long id) {
        this.id = id;
        return this;
    }

    public String getCpf() {
        return this.cpf;
    }

    public PessoaFisca cpf(String cpf) {
        this.cpf = cpf;
        return this;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getRg() {
        return this.rg;
    }

    public PessoaFisca rg(String rg) {
        this.rg = rg;
        return this;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public LocalDate getDataNascimento() {
        return this.dataNascimento;
    }

    public PessoaFisca dataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
        return this;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Integer getIdade() {
        return this.idade;
    }

    public PessoaFisca idade(Integer idade) {
        this.idade = idade;
        return this;
    }

    public void setIdade(Integer idade) {
        this.idade = idade;
    }

    public String getSexo() {
        return this.sexo;
    }

    public PessoaFisca sexo(String sexo) {
        this.sexo = sexo;
        return this;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getCor() {
        return this.cor;
    }

    public PessoaFisca cor(String cor) {
        this.cor = cor;
        return this;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public String getEstadoCivil() {
        return this.estadoCivil;
    }

    public PessoaFisca estadoCivil(String estadoCivil) {
        this.estadoCivil = estadoCivil;
        return this;
    }

    public void setEstadoCivil(String estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public String getNaturalidade() {
        return this.naturalidade;
    }

    public PessoaFisca naturalidade(String naturalidade) {
        this.naturalidade = naturalidade;
        return this;
    }

    public void setNaturalidade(String naturalidade) {
        this.naturalidade = naturalidade;
    }

    public String getNacionalidade() {
        return this.nacionalidade;
    }

    public PessoaFisca nacionalidade(String nacionalidade) {
        this.nacionalidade = nacionalidade;
        return this;
    }

    public void setNacionalidade(String nacionalidade) {
        this.nacionalidade = nacionalidade;
    }

    public PessoaComplemento getPessoaComplemento() {
        return this.pessoaComplemento;
    }

    public PessoaFisca pessoaComplemento(PessoaComplemento pessoaComplemento) {
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

    public Set<PessoaParentesco> getPessoaParentescos() {
        return this.pessoaParentescos;
    }

    public PessoaFisca pessoaParentescos(Set<PessoaParentesco> pessoaParentescos) {
        this.setPessoaParentescos(pessoaParentescos);
        return this;
    }

    public PessoaFisca addPessoaParentesco(PessoaParentesco pessoaParentesco) {
        this.pessoaParentescos.add(pessoaParentesco);
        pessoaParentesco.setPessoaFisca(this);
        return this;
    }

    public PessoaFisca removePessoaParentesco(PessoaParentesco pessoaParentesco) {
        this.pessoaParentescos.remove(pessoaParentesco);
        pessoaParentesco.setPessoaFisca(null);
        return this;
    }

    public void setPessoaParentescos(Set<PessoaParentesco> pessoaParentescos) {
        if (this.pessoaParentescos != null) {
            this.pessoaParentescos.forEach(i -> i.setPessoaFisca(null));
        }
        if (pessoaParentescos != null) {
            pessoaParentescos.forEach(i -> i.setPessoaFisca(this));
        }
        this.pessoaParentescos = pessoaParentescos;
    }

    public Pessoa getPessoa() {
        return this.pessoa;
    }

    public PessoaFisca pessoa(Pessoa pessoa) {
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
        if (!(o instanceof PessoaFisca)) {
            return false;
        }
        return id != null && id.equals(((PessoaFisca) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PessoaFisca{" +
            "id=" + getId() +
            ", cpf='" + getCpf() + "'" +
            ", rg='" + getRg() + "'" +
            ", dataNascimento='" + getDataNascimento() + "'" +
            ", idade=" + getIdade() +
            ", sexo='" + getSexo() + "'" +
            ", cor='" + getCor() + "'" +
            ", estadoCivil='" + getEstadoCivil() + "'" +
            ", naturalidade='" + getNaturalidade() + "'" +
            ", nacionalidade='" + getNacionalidade() + "'" +
            "}";
    }
}
