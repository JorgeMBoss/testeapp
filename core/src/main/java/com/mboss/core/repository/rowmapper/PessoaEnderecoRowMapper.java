package com.mboss.core.repository.rowmapper;

import com.mboss.core.domain.PessoaEndereco;
import com.mboss.core.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link PessoaEndereco}, with proper type conversions.
 */
@Service
public class PessoaEnderecoRowMapper implements BiFunction<Row, String, PessoaEndereco> {

    private final ColumnConverter converter;

    public PessoaEnderecoRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link PessoaEndereco} stored in the database.
     */
    @Override
    public PessoaEndereco apply(Row row, String prefix) {
        PessoaEndereco entity = new PessoaEndereco();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setPais(converter.fromRow(row, prefix + "_pais", String.class));
        entity.setEstado(converter.fromRow(row, prefix + "_estado", String.class));
        entity.setCidade(converter.fromRow(row, prefix + "_cidade", String.class));
        entity.setBairro(converter.fromRow(row, prefix + "_bairro", String.class));
        entity.setNumeroResidencia(converter.fromRow(row, prefix + "_numero_residencia", Integer.class));
        entity.setLogradouro(converter.fromRow(row, prefix + "_logradouro", String.class));
        entity.setComplemento(converter.fromRow(row, prefix + "_complemento", String.class));
        entity.setPessoaId(converter.fromRow(row, prefix + "_pessoa_id", Long.class));
        return entity;
    }
}
