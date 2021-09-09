package com.mboss.core.repository.rowmapper;

import com.mboss.core.domain.Pessoa;
import com.mboss.core.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Pessoa}, with proper type conversions.
 */
@Service
public class PessoaRowMapper implements BiFunction<Row, String, Pessoa> {

    private final ColumnConverter converter;

    public PessoaRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Pessoa} stored in the database.
     */
    @Override
    public Pessoa apply(Row row, String prefix) {
        Pessoa entity = new Pessoa();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setIdEmpresa(converter.fromRow(row, prefix + "_id_empresa", Long.class));
        entity.setDataCadastro(converter.fromRow(row, prefix + "_data_cadastro", LocalDate.class));
        entity.setAtivo(converter.fromRow(row, prefix + "_ativo", Boolean.class));
        entity.setEmpresaAppId(converter.fromRow(row, prefix + "_empresa_app_id", Long.class));
        return entity;
    }
}
