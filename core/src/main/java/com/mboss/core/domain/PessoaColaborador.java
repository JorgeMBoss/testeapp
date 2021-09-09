package com.mboss.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A PessoaColaborador.
 */
@Table("pessoa_colaborador")
public class PessoaColaborador implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column("data_adimissao")
    private LocalDate dataAdimissao;

    @Column("data_saida")
    private LocalDate dataSaida;

    @Column("carga_horaria")
    private Long cargaHoraria;

    @Column("primeiro_horario")
    private ZonedDateTime primeiroHorario;

    @Column("segundo_horario")
    private ZonedDateTime segundoHorario;

    @Column("salario")
    private Double salario;

    @Column("comissao")
    private Double comissao;

    @Column("desconto_maximo")
    private Double descontoMaximo;

    private Long pessoaFuncaoId;

    @Transient
    private PessoaFuncao pessoaFuncao;

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

    public PessoaColaborador id(Long id) {
        this.id = id;
        return this;
    }

    public LocalDate getDataAdimissao() {
        return this.dataAdimissao;
    }

    public PessoaColaborador dataAdimissao(LocalDate dataAdimissao) {
        this.dataAdimissao = dataAdimissao;
        return this;
    }

    public void setDataAdimissao(LocalDate dataAdimissao) {
        this.dataAdimissao = dataAdimissao;
    }

    public LocalDate getDataSaida() {
        return this.dataSaida;
    }

    public PessoaColaborador dataSaida(LocalDate dataSaida) {
        this.dataSaida = dataSaida;
        return this;
    }

    public void setDataSaida(LocalDate dataSaida) {
        this.dataSaida = dataSaida;
    }

    public Long getCargaHoraria() {
        return this.cargaHoraria;
    }

    public PessoaColaborador cargaHoraria(Long cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
        return this;
    }

    public void setCargaHoraria(Long cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }

    public ZonedDateTime getPrimeiroHorario() {
        return this.primeiroHorario;
    }

    public PessoaColaborador primeiroHorario(ZonedDateTime primeiroHorario) {
        this.primeiroHorario = primeiroHorario;
        return this;
    }

    public void setPrimeiroHorario(ZonedDateTime primeiroHorario) {
        this.primeiroHorario = primeiroHorario;
    }

    public ZonedDateTime getSegundoHorario() {
        return this.segundoHorario;
    }

    public PessoaColaborador segundoHorario(ZonedDateTime segundoHorario) {
        this.segundoHorario = segundoHorario;
        return this;
    }

    public void setSegundoHorario(ZonedDateTime segundoHorario) {
        this.segundoHorario = segundoHorario;
    }

    public Double getSalario() {
        return this.salario;
    }

    public PessoaColaborador salario(Double salario) {
        this.salario = salario;
        return this;
    }

    public void setSalario(Double salario) {
        this.salario = salario;
    }

    public Double getComissao() {
        return this.comissao;
    }

    public PessoaColaborador comissao(Double comissao) {
        this.comissao = comissao;
        return this;
    }

    public void setComissao(Double comissao) {
        this.comissao = comissao;
    }

    public Double getDescontoMaximo() {
        return this.descontoMaximo;
    }

    public PessoaColaborador descontoMaximo(Double descontoMaximo) {
        this.descontoMaximo = descontoMaximo;
        return this;
    }

    public void setDescontoMaximo(Double descontoMaximo) {
        this.descontoMaximo = descontoMaximo;
    }

    public PessoaFuncao getPessoaFuncao() {
        return this.pessoaFuncao;
    }

    public PessoaColaborador pessoaFuncao(PessoaFuncao pessoaFuncao) {
        this.setPessoaFuncao(pessoaFuncao);
        this.pessoaFuncaoId = pessoaFuncao != null ? pessoaFuncao.getId() : null;
        return this;
    }

    public void setPessoaFuncao(PessoaFuncao pessoaFuncao) {
        this.pessoaFuncao = pessoaFuncao;
        this.pessoaFuncaoId = pessoaFuncao != null ? pessoaFuncao.getId() : null;
    }

    public Long getPessoaFuncaoId() {
        return this.pessoaFuncaoId;
    }

    public void setPessoaFuncaoId(Long pessoaFuncao) {
        this.pessoaFuncaoId = pessoaFuncao;
    }

    public Pessoa getPessoa() {
        return this.pessoa;
    }

    public PessoaColaborador pessoa(Pessoa pessoa) {
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
        if (!(o instanceof PessoaColaborador)) {
            return false;
        }
        return id != null && id.equals(((PessoaColaborador) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PessoaColaborador{" +
            "id=" + getId() +
            ", dataAdimissao='" + getDataAdimissao() + "'" +
            ", dataSaida='" + getDataSaida() + "'" +
            ", cargaHoraria=" + getCargaHoraria() +
            ", primeiroHorario='" + getPrimeiroHorario() + "'" +
            ", segundoHorario='" + getSegundoHorario() + "'" +
            ", salario=" + getSalario() +
            ", comissao=" + getComissao() +
            ", descontoMaximo=" + getDescontoMaximo() +
            "}";
    }
}
