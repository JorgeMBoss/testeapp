package com.mboss.core.repository.rowmapper;

import com.mboss.core.domain.PessoaFisica;
import com.mboss.core.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link PessoaFisica}, with proper type conversions.
 */
@Service
public class PessoaFisicaRowMapper implements BiFunction<Row, String, PessoaFisica> {

    private final ColumnConverter converter;

    public PessoaFisicaRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link PessoaFisica} stored in the database.
     */
    @Override
    public PessoaFisica apply(Row row, String prefix) {
        PessoaFisica entity = new PessoaFisica();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setCpf(converter.fromRow(row, prefix + "_cpf", String.class));
        entity.setRg(converter.fromRow(row, prefix + "_rg", String.class));
        entity.setDataNascimento(converter.fromRow(row, prefix + "_data_nascimento", LocalDate.class));
        entity.setIdade(converter.fromRow(row, prefix + "_idade", Integer.class));
        entity.setSexo(converter.fromRow(row, prefix + "_sexo", String.class));
        entity.setCor(converter.fromRow(row, prefix + "_cor", String.class));
        entity.setEstadoCivil(converter.fromRow(row, prefix + "_estado_civil", String.class));
        entity.setNaturalidade(converter.fromRow(row, prefix + "_naturalidade", String.class));
        entity.setNacionalidade(converter.fromRow(row, prefix + "_nacionalidade", String.class));
        entity.setPessoaComplementoId(converter.fromRow(row, prefix + "_pessoa_complemento_id", Long.class));
        entity.setPessoaId(converter.fromRow(row, prefix + "_pessoa_id", Long.class));
        return entity;
    }
}
