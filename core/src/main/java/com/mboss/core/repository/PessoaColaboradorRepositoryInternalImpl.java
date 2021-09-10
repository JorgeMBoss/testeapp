package com.mboss.core.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.mboss.core.domain.PessoaColaborador;
import com.mboss.core.repository.rowmapper.PessoaColaboradorRowMapper;
import com.mboss.core.repository.rowmapper.PessoaFuncaoRowMapper;
import com.mboss.core.repository.rowmapper.PessoaRowMapper;
import com.mboss.core.service.EntityManager;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.LocalDate;
import java.time.ZonedDateTime;
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
 * Spring Data SQL reactive custom repository implementation for the PessoaColaborador entity.
 */
@SuppressWarnings("unused")
class PessoaColaboradorRepositoryInternalImpl implements PessoaColaboradorRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final PessoaFuncaoRowMapper pessoafuncaoMapper;
    private final PessoaRowMapper pessoaMapper;
    private final PessoaColaboradorRowMapper pessoacolaboradorMapper;

    private static final Table entityTable = Table.aliased("pessoa_colaborador", EntityManager.ENTITY_ALIAS);
    private static final Table pessoaFuncaoTable = Table.aliased("pessoa_funcao", "pessoaFuncao");
    private static final Table pessoaTable = Table.aliased("pessoa", "pessoa");

    public PessoaColaboradorRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        PessoaFuncaoRowMapper pessoafuncaoMapper,
        PessoaRowMapper pessoaMapper,
        PessoaColaboradorRowMapper pessoacolaboradorMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.pessoafuncaoMapper = pessoafuncaoMapper;
        this.pessoaMapper = pessoaMapper;
        this.pessoacolaboradorMapper = pessoacolaboradorMapper;
    }

    @Override
    public Flux<PessoaColaborador> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<PessoaColaborador> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<PessoaColaborador> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = PessoaColaboradorSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(PessoaFuncaoSqlHelper.getColumns(pessoaFuncaoTable, "pessoaFuncao"));
        columns.addAll(PessoaSqlHelper.getColumns(pessoaTable, "pessoa"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(pessoaFuncaoTable)
            .on(Column.create("pessoa_funcao_id", entityTable))
            .equals(Column.create("id", pessoaFuncaoTable))
            .leftOuterJoin(pessoaTable)
            .on(Column.create("pessoa_id", entityTable))
            .equals(Column.create("id", pessoaTable));

        String select = entityManager.createSelect(selectFrom, PessoaColaborador.class, pageable, criteria);
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
    public Flux<PessoaColaborador> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<PessoaColaborador> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private PessoaColaborador process(Row row, RowMetadata metadata) {
        PessoaColaborador entity = pessoacolaboradorMapper.apply(row, "e");
        entity.setPessoaFuncao(pessoafuncaoMapper.apply(row, "pessoaFuncao"));
        entity.setPessoa(pessoaMapper.apply(row, "pessoa"));
        return entity;
    }

    @Override
    public <S extends PessoaColaborador> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends PessoaColaborador> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update PessoaColaborador with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(PessoaColaborador entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class PessoaColaboradorSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("data_adimissao", table, columnPrefix + "_data_adimissao"));
        columns.add(Column.aliased("data_saida", table, columnPrefix + "_data_saida"));
        columns.add(Column.aliased("carga_horaria", table, columnPrefix + "_carga_horaria"));
        columns.add(Column.aliased("primeiro_horario", table, columnPrefix + "_primeiro_horario"));
        columns.add(Column.aliased("segundo_horario", table, columnPrefix + "_segundo_horario"));
        columns.add(Column.aliased("salario", table, columnPrefix + "_salario"));
        columns.add(Column.aliased("comissao", table, columnPrefix + "_comissao"));
        columns.add(Column.aliased("desconto_maximo", table, columnPrefix + "_desconto_maximo"));

        columns.add(Column.aliased("pessoa_funcao_id", table, columnPrefix + "_pessoa_funcao_id"));
        columns.add(Column.aliased("pessoa_id", table, columnPrefix + "_pessoa_id"));
        return columns;
    }
}
