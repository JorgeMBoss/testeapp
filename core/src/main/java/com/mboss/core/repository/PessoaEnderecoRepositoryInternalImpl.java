package com.mboss.core.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.mboss.core.domain.PessoaEndereco;
import com.mboss.core.repository.rowmapper.PessoaEnderecoRowMapper;
import com.mboss.core.repository.rowmapper.PessoaRowMapper;
import com.mboss.core.service.EntityManager;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the PessoaEndereco entity.
 */
@SuppressWarnings("unused")
class PessoaEnderecoRepositoryInternalImpl implements PessoaEnderecoRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final PessoaRowMapper pessoaMapper;
    private final PessoaEnderecoRowMapper pessoaenderecoMapper;

    private static final Table entityTable = Table.aliased("pessoa_endereco", EntityManager.ENTITY_ALIAS);
    private static final Table pessoaTable = Table.aliased("pessoa", "pessoa");

    public PessoaEnderecoRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        PessoaRowMapper pessoaMapper,
        PessoaEnderecoRowMapper pessoaenderecoMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.pessoaMapper = pessoaMapper;
        this.pessoaenderecoMapper = pessoaenderecoMapper;
    }

    @Override
    public Flux<PessoaEndereco> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<PessoaEndereco> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<PessoaEndereco> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = PessoaEnderecoSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(PessoaSqlHelper.getColumns(pessoaTable, "pessoa"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(pessoaTable)
            .on(Column.create("pessoa_id", entityTable))
            .equals(Column.create("id", pessoaTable));

        String select = entityManager.createSelect(selectFrom, PessoaEndereco.class, pageable, criteria);
        String alias = entityTable.getReferenceName().getReference();
        String selectWhere = Optional
            .ofNullable(criteria)
            .map(
                crit ->
                    new StringBuilder(select)
                        .append(" ")
                        .append("WHERE")
                        .append(" ")
                        .append(alias)
                        .append(".")
                        .append(crit.toString())
                        .toString()
            )
            .orElse(select); // TODO remove once https://github.com/spring-projects/spring-data-jdbc/issues/907 will be fixed
        return db.sql(selectWhere).map(this::process);
    }

    @Override
    public Flux<PessoaEndereco> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<PessoaEndereco> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private PessoaEndereco process(Row row, RowMetadata metadata) {
        PessoaEndereco entity = pessoaenderecoMapper.apply(row, "e");
        entity.setPessoa(pessoaMapper.apply(row, "pessoa"));
        return entity;
    }

    @Override
    public <S extends PessoaEndereco> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends PessoaEndereco> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update PessoaEndereco with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(PessoaEndereco entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class PessoaEnderecoSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("pais", table, columnPrefix + "_pais"));
        columns.add(Column.aliased("estado", table, columnPrefix + "_estado"));
        columns.add(Column.aliased("cidade", table, columnPrefix + "_cidade"));
        columns.add(Column.aliased("bairro", table, columnPrefix + "_bairro"));
        columns.add(Column.aliased("numero_residencia", table, columnPrefix + "_numero_residencia"));
        columns.add(Column.aliased("logradouro", table, columnPrefix + "_logradouro"));
        columns.add(Column.aliased("complemento", table, columnPrefix + "_complemento"));

        columns.add(Column.aliased("pessoa_id", table, columnPrefix + "_pessoa_id"));
        return columns;
    }
}
