package com.mboss.core.repository.rowmapper;

import com.mboss.core.domain.PessoaFuncao;
import com.mboss.core.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link PessoaFuncao}, with proper type conversions.
 */
@Service
public class PessoaFuncaoRowMapper implements BiFunction<Row, String, PessoaFuncao> {

    private final ColumnConverter converter;

    public PessoaFuncaoRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link PessoaFuncao} stored in the database.
     */
    @Override
    public PessoaFuncao apply(Row row, String prefix) {
        PessoaFuncao entity = new PessoaFuncao();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setDescricao(converter.fromRow(row, prefix + "_descricao", String.class));
        entity.setAtivo(converter.fromRow(row, prefix + "_ativo", Boolean.class));
        return entity;
    }
}
