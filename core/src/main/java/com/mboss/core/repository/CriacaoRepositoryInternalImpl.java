package com.mboss.core.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.mboss.core.domain.Criacao;
import com.mboss.core.repository.rowmapper.CriacaoCorRowMapper;
import com.mboss.core.repository.rowmapper.CriacaoEspecieRowMapper;
import com.mboss.core.repository.rowmapper.CriacaoRacaRowMapper;
import com.mboss.core.repository.rowmapper.CriacaoRowMapper;
import com.mboss.core.repository.rowmapper.EmpresaAppRowMapper;
import com.mboss.core.repository.rowmapper.PessoaCriacaoRowMapper;
import com.mboss.core.service.EntityManager;
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
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the Criacao entity.
 */
@SuppressWarnings("unused")
class CriacaoRepositoryInternalImpl implements CriacaoRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CriacaoCorRowMapper criacaocorMapper;
    private final CriacaoRacaRowMapper criacaoracaMapper;
    private final CriacaoEspecieRowMapper criacaoespecieMapper;
    private final EmpresaAppRowMapper empresaappMapper;
    private final PessoaCriacaoRowMapper pessoacriacaoMapper;
    private final CriacaoRowMapper criacaoMapper;

    private static final Table entityTable = Table.aliased("criacao", EntityManager.ENTITY_ALIAS);
    private static final Table criacaoCorTable = Table.aliased("criacao_cor", "criacaoCor");
    private static final Table criacaoRacaTable = Table.aliased("criacao_raca", "criacaoRaca");
    private static final Table criacaoEspecieTable = Table.aliased("criacao_especie", "criacaoEspecie");
    private static final Table empresaAppTable = Table.aliased("empresa_app", "empresaApp");
    private static final Table pessoaCriacaoTable = Table.aliased("pessoa_criacao", "pessoaCriacao");

    public CriacaoRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CriacaoCorRowMapper criacaocorMapper,
        CriacaoRacaRowMapper criacaoracaMapper,
        CriacaoEspecieRowMapper criacaoespecieMapper,
        EmpresaAppRowMapper empresaappMapper,
        PessoaCriacaoRowMapper pessoacriacaoMapper,
        CriacaoRowMapper criacaoMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.criacaocorMapper = criacaocorMapper;
        this.criacaoracaMapper = criacaoracaMapper;
        this.criacaoespecieMapper = criacaoespecieMapper;
        this.empresaappMapper = empresaappMapper;
        this.pessoacriacaoMapper = pessoacriacaoMapper;
        this.criacaoMapper = criacaoMapper;
    }

    @Override
    public Flux<Criacao> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Criacao> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Criacao> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = CriacaoSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(CriacaoCorSqlHelper.getColumns(criacaoCorTable, "criacaoCor"));
        columns.addAll(CriacaoRacaSqlHelper.getColumns(criacaoRacaTable, "criacaoRaca"));
        columns.addAll(CriacaoEspecieSqlHelper.getColumns(criacaoEspecieTable, "criacaoEspecie"));
        columns.addAll(EmpresaAppSqlHelper.getColumns(empresaAppTable, "empresaApp"));
        columns.addAll(PessoaCriacaoSqlHelper.getColumns(pessoaCriacaoTable, "pessoaCriacao"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(criacaoCorTable)
            .on(Column.create("criacao_cor_id", entityTable))
            .equals(Column.create("id", criacaoCorTable))
            .leftOuterJoin(criacaoRacaTable)
            .on(Column.create("criacao_raca_id", entityTable))
            .equals(Column.create("id", criacaoRacaTable))
            .leftOuterJoin(criacaoEspecieTable)
            .on(Column.create("criacao_especie_id", entityTable))
            .equals(Column.create("id", criacaoEspecieTable))
            .leftOuterJoin(empresaAppTable)
            .on(Column.create("empresa_app_id", entityTable))
            .equals(Column.create("id", empresaAppTable))
            .leftOuterJoin(pessoaCriacaoTable)
            .on(Column.create("pessoa_criacao_id", entityTable))
            .equals(Column.create("id", pessoaCriacaoTable));

        String select = entityManager.createSelect(selectFrom, Criacao.class, pageable, criteria);
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
    public Flux<Criacao> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Criacao> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private Criacao process(Row row, RowMetadata metadata) {
        Criacao entity = criacaoMapper.apply(row, "e");
        entity.setCriacaoCor(criacaocorMapper.apply(row, "criacaoCor"));
        entity.setCriacaoRaca(criacaoracaMapper.apply(row, "criacaoRaca"));
        entity.setCriacaoEspecie(criacaoespecieMapper.apply(row, "criacaoEspecie"));
        entity.setEmpresaApp(empresaappMapper.apply(row, "empresaApp"));
        entity.setPessoaCriacao(pessoacriacaoMapper.apply(row, "pessoaCriacao"));
        return entity;
    }

    @Override
    public <S extends Criacao> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends Criacao> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update Criacao with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(Criacao entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class CriacaoSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("id_empresa", table, columnPrefix + "_id_empresa"));
        columns.add(Column.aliased("nome", table, columnPrefix + "_nome"));
        columns.add(Column.aliased("sexo", table, columnPrefix + "_sexo"));
        columns.add(Column.aliased("porte", table, columnPrefix + "_porte"));
        columns.add(Column.aliased("idade", table, columnPrefix + "_idade"));
        columns.add(Column.aliased("data_nascimento", table, columnPrefix + "_data_nascimento"));
        columns.add(Column.aliased("castrado", table, columnPrefix + "_castrado"));
        columns.add(Column.aliased("anotacao", table, columnPrefix + "_anotacao"));
        columns.add(Column.aliased("pedigree", table, columnPrefix + "_pedigree"));
        columns.add(Column.aliased("ativo", table, columnPrefix + "_ativo"));

        columns.add(Column.aliased("criacao_cor_id", table, columnPrefix + "_criacao_cor_id"));
        columns.add(Column.aliased("criacao_raca_id", table, columnPrefix + "_criacao_raca_id"));
        columns.add(Column.aliased("criacao_especie_id", table, columnPrefix + "_criacao_especie_id"));
        columns.add(Column.aliased("empresa_app_id", table, columnPrefix + "_empresa_app_id"));
        columns.add(Column.aliased("pessoa_criacao_id", table, columnPrefix + "_pessoa_criacao_id"));
        return columns;
    }
}
