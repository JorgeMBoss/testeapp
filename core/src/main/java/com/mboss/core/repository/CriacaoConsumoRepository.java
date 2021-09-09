package com.mboss.core.repository;

import com.mboss.core.domain.CriacaoConsumo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the CriacaoConsumo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CriacaoConsumoRepository extends R2dbcRepository<CriacaoConsumo, Long>, CriacaoConsumoRepositoryInternal {
    Flux<CriacaoConsumo> findAllBy(Pageable pageable);

    @Query("SELECT * FROM criacao_consumo entity WHERE entity.empresa_app_id = :id")
    Flux<CriacaoConsumo> findByEmpresaApp(Long id);

    @Query("SELECT * FROM criacao_consumo entity WHERE entity.empresa_app_id IS NULL")
    Flux<CriacaoConsumo> findAllWhereEmpresaAppIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<CriacaoConsumo> findAll();

    @Override
    Mono<CriacaoConsumo> findById(Long id);

    @Override
    <S extends CriacaoConsumo> Mono<S> save(S entity);
}

interface CriacaoConsumoRepositoryInternal {
    <S extends CriacaoConsumo> Mono<S> insert(S entity);
    <S extends CriacaoConsumo> Mono<S> save(S entity);
    Mono<Integer> update(CriacaoConsumo entity);

    Flux<CriacaoConsumo> findAll();
    Mono<CriacaoConsumo> findById(Long id);
    Flux<CriacaoConsumo> findAllBy(Pageable pageable);
    Flux<CriacaoConsumo> findAllBy(Pageable pageable, Criteria criteria);
}
