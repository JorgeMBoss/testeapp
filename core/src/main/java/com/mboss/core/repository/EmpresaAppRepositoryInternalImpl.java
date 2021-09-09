package com.mboss.core.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.mboss.core.domain.EmpresaApp;
import com.mboss.core.domain.UserApp;
import com.mboss.core.repository.rowmapper.EmpresaAppRowMapper;
import com.mboss.core.service.EntityManager;
import com.mboss.core.service.EntityManager.LinkTable;
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
 * Spring Data SQL reactive custom repository implementation for the EmpresaApp entity.
 */
@SuppressWarnings("unused")
class EmpresaAppRepositoryInternalImpl implements EmpresaAppRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final EmpresaAppRowMapper empresaappMapper;

    private static final Table entityTable = Table.aliased("empresa_app", EntityManager.ENTITY_ALIAS);

    private static final EntityManager.LinkTable userAppLink = new LinkTable("rel_empresa_app__user_app", "empresa_app_id", "user_app_id");

    public EmpresaAppRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        EmpresaAppRowMapper empresaappMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.empresaappMapper = empresaappMapper;
    }

    @Override
    public Flux<EmpresaApp> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<EmpresaApp> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<EmpresaApp> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = EmpresaAppSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);

        String select = entityManager.createSelect(selectFrom, EmpresaApp.class, pageable, criteria);
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
    public Flux<EmpresaApp> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<EmpresaApp> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    @Override
    public Mono<EmpresaApp> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<EmpresaApp> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<EmpresaApp> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private EmpresaApp process(Row row, RowMetadata metadata) {
        EmpresaApp entity = empresaappMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends EmpresaApp> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends EmpresaApp> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity).flatMap(savedEntity -> updateRelations(savedEntity));
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update EmpresaApp with id = " + entity.getId());
                        }
                        return entity;
                    }
                )
                .then(updateRelations(entity));
        }
    }

    @Override
    public Mono<Integer> update(EmpresaApp entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }

    @Override
    public Mono<Void> deleteById(Long entityId) {
        return deleteRelations(entityId)
            .then(r2dbcEntityTemplate.delete(EmpresaApp.class).matching(query(where("id").is(entityId))).all().then());
    }

    protected <S extends EmpresaApp> Mono<S> updateRelations(S entity) {
        Mono<Void> result = entityManager
            .updateLinkTable(userAppLink, entity.getId(), entity.getUserApps().stream().map(UserApp::getId))
            .then();
        return result.thenReturn(entity);
    }

    protected Mono<Void> deleteRelations(Long entityId) {
        return entityManager.deleteFromLinkTable(userAppLink, entityId);
    }
}

class EmpresaAppSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("razao_social", table, columnPrefix + "_razao_social"));
        columns.add(Column.aliased("nome_fantasia", table, columnPrefix + "_nome_fantasia"));

        return columns;
    }
}
