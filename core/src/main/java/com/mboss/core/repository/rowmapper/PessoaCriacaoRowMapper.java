package com.mboss.core.repository.rowmapper;

import com.mboss.core.domain.PessoaCriacao;
import com.mboss.core.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link PessoaCriacao}, with proper type conversions.
 */
@Service
public class PessoaCriacaoRowMapper implements BiFunction<Row, String, PessoaCriacao> {

    private final ColumnConverter converter;

    public PessoaCriacaoRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link PessoaCriacao} stored in the database.
     */
    @Override
    public PessoaCriacao apply(Row row, String prefix) {
        PessoaCriacao entity = new PessoaCriacao();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setIdPessoa(converter.fromRow(row, prefix + "_id_pessoa", Long.class));
        entity.setIdCriacao(converter.fromRow(row, prefix + "_id_criacao", Long.class));
        return entity;
    }
}
