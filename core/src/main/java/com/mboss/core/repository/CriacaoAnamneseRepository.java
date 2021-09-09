package com.mboss.core.repository;

import com.mboss.core.domain.CriacaoAnamnese;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the CriacaoAnamnese entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CriacaoAnamneseRepository extends R2dbcRepository<CriacaoAnamnese, Long>, CriacaoAnamneseRepositoryInternal {
    Flux<CriacaoAnamnese> findAllBy(Pageable pageable);

    @Query("SELECT * FROM criacao_anamnese entity WHERE entity.empresa_app_id = :id")
    Flux<CriacaoAnamnese> findByEmpresaApp(Long id);

    @Query("SELECT * FROM criacao_anamnese entity WHERE entity.empresa_app_id IS NULL")
    Flux<CriacaoAnamnese> findAllWhereEmpresaAppIsNull();

    @Query("SELECT * FROM criacao_anamnese entity WHERE entity.criacao_id = :id")
    Flux<CriacaoAnamnese> findByCriacao(Long id);

    @Query("SELECT * FROM criacao_anamnese entity WHERE entity.criacao_id IS NULL")
    Flux<CriacaoAnamnese> findAllWhereCriacaoIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<CriacaoAnamnese> findAll();

    @Override
    Mono<CriacaoAnamnese> findById(Long id);

    @Override
    <S extends CriacaoAnamnese> Mono<S> save(S entity);
}

interface CriacaoAnamneseRepositoryInternal {
    <S extends CriacaoAnamnese> Mono<S> insert(S entity);
    <S extends CriacaoAnamnese> Mono<S> save(S entity);
    Mono<Integer> update(CriacaoAnamnese entity);

    Flux<CriacaoAnamnese> findAll();
    Mono<CriacaoAnamnese> findById(Long id);
    Flux<CriacaoAnamnese> findAllBy(Pageable pageable);
    Flux<CriacaoAnamnese> findAllBy(Pageable pageable, Criteria criteria);
}
