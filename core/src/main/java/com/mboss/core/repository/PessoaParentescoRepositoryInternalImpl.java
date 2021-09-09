package com.mboss.core.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.mboss.core.domain.PessoaParentesco;
import com.mboss.core.repository.rowmapper.PessoaFiscaRowMapper;
import com.mboss.core.repository.rowmapper.PessoaParentescoRowMapper;
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
 * Spring Data SQL reactive custom repository implementation for the PessoaParentesco entity.
 */
@SuppressWarnings("unused")
class PessoaParentescoRepositoryInternalImpl implements PessoaParentescoRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final PessoaFiscaRowMapper pessoafiscaMapper;
    private final PessoaParentescoRowMapper pessoaparentescoMapper;

    private static final Table entityTable = Table.aliased("pessoa_parentesco", EntityManager.ENTITY_ALIAS);
    private static final Table pessoaFiscaTable = Table.aliased("pessoa_fisca", "pessoaFisca");

    public PessoaParentescoRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        PessoaFiscaRowMapper pessoafiscaMapper,
        PessoaParentescoRowMapper pessoaparentescoMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.pessoafiscaMapper = pessoafiscaMapper;
        this.pessoaparentescoMapper = pessoaparentescoMapper;
    }

    @Override
    public Flux<PessoaParentesco> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<PessoaParentesco> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<PessoaParentesco> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = PessoaParentescoSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(PessoaFiscaSqlHelper.getColumns(pessoaFiscaTable, "pessoaFisca"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(pessoaFiscaTable)
            .on(Column.create("pessoa_fisca_id", entityTable))
            .equals(Column.create("id", pessoaFiscaTable));

        String select = entityManager.createSelect(selectFrom, PessoaParentesco.class, pageable, criteria);
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
    public Flux<PessoaParentesco> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<PessoaParentesco> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private PessoaParentesco process(Row row, RowMetadata metadata) {
        PessoaParentesco entity = pessoaparentescoMapper.apply(row, "e");
        entity.setPessoaFisca(pessoafiscaMapper.apply(row, "pessoaFisca"));
        return entity;
    }

    @Override
    public <S extends PessoaParentesco> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends PessoaParentesco> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update PessoaParentesco with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(PessoaParentesco entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class PessoaParentescoSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("cpf", table, columnPrefix + "_cpf"));
        columns.add(Column.aliased("nome", table, columnPrefix + "_nome"));
        columns.add(Column.aliased("email", table, columnPrefix + "_email"));

        columns.add(Column.aliased("pessoa_fisca_id", table, columnPrefix + "_pessoa_fisca_id"));
        return columns;
    }
}
