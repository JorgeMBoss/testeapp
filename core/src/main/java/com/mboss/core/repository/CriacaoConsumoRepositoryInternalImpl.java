package com.mboss.core.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.mboss.core.domain.Criacao;
import com.mboss.core.domain.CriacaoConsumo;
import com.mboss.core.repository.rowmapper.CriacaoConsumoRowMapper;
import com.mboss.core.service.EntityManager;
import com.mboss.core.service.EntityManager.LinkTable;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.LocalDate;
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
 * Spring Data SQL reactive custom repository implementation for the CriacaoConsumo entity.
 */
@SuppressWarnings("unused")
class CriacaoConsumoRepositoryInternalImpl implements CriacaoConsumoRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CriacaoConsumoRowMapper criacaoconsumoMapper;

    private static final Table entityTable = Table.aliased("criacao_consumo", EntityManager.ENTITY_ALIAS);

    private static final EntityManager.LinkTable criacaoLink = new LinkTable(
        "rel_criacao_consumo__criacao",
        "criacao_consumo_id",
        "criacao_id"
    );

    public CriacaoConsumoRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CriacaoConsumoRowMapper criacaoconsumoMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.criacaoconsumoMapper = criacaoconsumoMapper;
    }

    @Override
    public Flux<CriacaoConsumo> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<CriacaoConsumo> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<CriacaoConsumo> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = CriacaoConsumoSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);

        String select = entityManager.createSelect(selectFrom, CriacaoConsumo.class, pageable, criteria);
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
    public Flux<CriacaoConsumo> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<CriacaoConsumo> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    @Override
    public Mono<CriacaoConsumo> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<CriacaoConsumo> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<CriacaoConsumo> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private CriacaoConsumo process(Row row, RowMetadata metadata) {
        CriacaoConsumo entity = criacaoconsumoMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends CriacaoConsumo> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends CriacaoConsumo> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity).flatMap(savedEntity -> updateRelations(savedEntity));
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update CriacaoConsumo with id = " + entity.getId());
                        }
                        return entity;
                    }
                )
                .then(updateRelations(entity));
        }
    }

    @Override
    public Mono<Integer> update(CriacaoConsumo entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }

    @Override
    public Mono<Void> deleteById(Long entityId) {
        return deleteRelations(entityId)
            .then(r2dbcEntityTemplate.delete(CriacaoConsumo.class).matching(query(where("id").is(entityId))).all().then());
    }

    protected <S extends CriacaoConsumo> Mono<S> updateRelations(S entity) {
        Mono<Void> result = entityManager
            .updateLinkTable(criacaoLink, entity.getId(), entity.getCriacaos().stream().map(Criacao::getId))
            .then();
        return result.thenReturn(entity);
    }

    protected Mono<Void> deleteRelations(Long entityId) {
        return entityManager.deleteFromLinkTable(criacaoLink, entityId);
    }
}

class CriacaoConsumoSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("data_sistema", table, columnPrefix + "_data_sistema"));
        columns.add(Column.aliased("data_venda", table, columnPrefix + "_data_venda"));
        columns.add(Column.aliased("data_aviso", table, columnPrefix + "_data_aviso"));
        columns.add(Column.aliased("anotacao", table, columnPrefix + "_anotacao"));
        columns.add(Column.aliased("status", table, columnPrefix + "_status"));

        return columns;
    }
}
