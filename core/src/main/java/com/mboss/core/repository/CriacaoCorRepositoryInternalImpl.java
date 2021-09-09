package com.mboss.core.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.mboss.core.domain.CriacaoCor;
import com.mboss.core.repository.rowmapper.CriacaoCorRowMapper;
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
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoin;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the CriacaoCor entity.
 */
@SuppressWarnings("unused")
class CriacaoCorRepositoryInternalImpl implements CriacaoCorRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CriacaoCorRowMapper criacaocorMapper;

    private static final Table entityTable = Table.aliased("criacao_cor", EntityManager.ENTITY_ALIAS);

    public CriacaoCorRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CriacaoCorRowMapper criacaocorMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.criacaocorMapper = criacaocorMapper;
    }

    @Override
    public Flux<CriacaoCor> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<CriacaoCor> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<CriacaoCor> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = CriacaoCorSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);

        String select = entityManager.createSelect(selectFrom, CriacaoCor.class, pageable, criteria);
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
    public Flux<CriacaoCor> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<CriacaoCor> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private CriacaoCor process(Row row, RowMetadata metadata) {
        CriacaoCor entity = criacaocorMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends CriacaoCor> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends CriacaoCor> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update CriacaoCor with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(CriacaoCor entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class CriacaoCorSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("descricao", table, columnPrefix + "_descricao"));
        columns.add(Column.aliased("ativo", table, columnPrefix + "_ativo"));

        return columns;
    }
}
