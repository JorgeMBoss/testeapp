package com.mboss.core.repository.rowmapper;

import com.mboss.core.domain.UserApp;
import com.mboss.core.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link UserApp}, with proper type conversions.
 */
@Service
public class UserAppRowMapper implements BiFunction<Row, String, UserApp> {

    private final ColumnConverter converter;

    public UserAppRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link UserApp} stored in the database.
     */
    @Override
    public UserApp apply(Row row, String prefix) {
        UserApp entity = new UserApp();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", String.class));
        return entity;
    }
}
