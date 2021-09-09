package com.mboss.core.repository.rowmapper;

import com.mboss.core.domain.PessoaParentesco;
import com.mboss.core.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link PessoaParentesco}, with proper type conversions.
 */
@Service
public class PessoaParentescoRowMapper implements BiFunction<Row, String, PessoaParentesco> {

    private final ColumnConverter converter;

    public PessoaParentescoRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link PessoaParentesco} stored in the database.
     */
    @Override
    public PessoaParentesco apply(Row row, String prefix) {
        PessoaParentesco entity = new PessoaParentesco();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setCpf(converter.fromRow(row, prefix + "_cpf", String.class));
        entity.setNome(converter.fromRow(row, prefix + "_nome", String.class));
        entity.setEmail(converter.fromRow(row, prefix + "_email", String.class));
        entity.setPessoaFisicaId(converter.fromRow(row, prefix + "_pessoa_fisica_id", Long.class));
        entity.setPessoaColaboradorId(converter.fromRow(row, prefix + "_pessoa_colaborador_id", Long.class));
        return entity;
    }
}
