package com.mboss.core.repository.rowmapper;

import com.mboss.core.domain.PessoaComplemento;
import com.mboss.core.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link PessoaComplemento}, with proper type conversions.
 */
@Service
public class PessoaComplementoRowMapper implements BiFunction<Row, String, PessoaComplemento> {

    private final ColumnConverter converter;

    public PessoaComplementoRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link PessoaComplemento} stored in the database.
     */
    @Override
    public PessoaComplemento apply(Row row, String prefix) {
        PessoaComplemento entity = new PessoaComplemento();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setIe(converter.fromRow(row, prefix + "_ie", Long.class));
        entity.setIm(converter.fromRow(row, prefix + "_im", Long.class));
        entity.setEmail(converter.fromRow(row, prefix + "_email", String.class));
        return entity;
    }
}
