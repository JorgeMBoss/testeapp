package com.mboss.core.repository.rowmapper;

import com.mboss.core.domain.PessoaJuridica;
import com.mboss.core.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link PessoaJuridica}, with proper type conversions.
 */
@Service
public class PessoaJuridicaRowMapper implements BiFunction<Row, String, PessoaJuridica> {

    private final ColumnConverter converter;

    public PessoaJuridicaRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link PessoaJuridica} stored in the database.
     */
    @Override
    public PessoaJuridica apply(Row row, String prefix) {
        PessoaJuridica entity = new PessoaJuridica();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setCnpj(converter.fromRow(row, prefix + "_cnpj", String.class));
        entity.setNomeRazao(converter.fromRow(row, prefix + "_nome_razao", String.class));
        entity.setNomeFantasia(converter.fromRow(row, prefix + "_nome_fantasia", String.class));
        entity.setPessoaComplementoId(converter.fromRow(row, prefix + "_pessoa_complemento_id", Long.class));
        entity.setPessoaId(converter.fromRow(row, prefix + "_pessoa_id", Long.class));
        return entity;
    }
}
