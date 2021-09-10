package com.mboss.core.repository;

import com.mboss.core.domain.EmpresaApp;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the EmpresaApp entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EmpresaAppRepository extends R2dbcRepository<EmpresaApp, Long>, EmpresaAppRepositoryInternal {
    Flux<EmpresaApp> findAllBy(Pageable pageable);

    @Override
    Mono<EmpresaApp> findOneWithEagerRelationships(Long id);

    @Override
    Flux<EmpresaApp> findAllWithEagerRelationships();

    @Override
    Flux<EmpresaApp> findAllWithEagerRelationships(Pageable page);

    @Override
    Mono<Void> deleteById(Long id);

    @Query(
        "SELECT entity.* FROM empresa_app entity JOIN rel_empresa_app__user_app joinTable ON entity.id = joinTable.empresa_app_id WHERE joinTable.user_app_id = :id"
    )
    Flux<EmpresaApp> findByUserApp(Long id);

    // just to avoid having unambigous methods
    @Override
    Flux<EmpresaApp> findAll();

    @Override
    Mono<EmpresaApp> findById(Long id);

    @Override
    <S extends EmpresaApp> Mono<S> save(S entity);
}

interface EmpresaAppRepositoryInternal {
    <S extends EmpresaApp> Mono<S> insert(S entity);
    <S extends EmpresaApp> Mono<S> save(S entity);
    Mono<Integer> update(EmpresaApp entity);

    Flux<EmpresaApp> findAll();
    Mono<EmpresaApp> findById(Long id);
    Flux<EmpresaApp> findAllBy(Pageable pageable);
    Flux<EmpresaApp> findAllBy(Pageable pageable, Criteria criteria);

    Mono<EmpresaApp> findOneWithEagerRelationships(Long id);

    Flux<EmpresaApp> findAllWithEagerRelationships();

    Flux<EmpresaApp> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
