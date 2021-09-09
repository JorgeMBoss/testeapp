package com.mboss.core.repository;

import com.mboss.core.domain.PessoaMedico;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the PessoaMedico entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PessoaMedicoRepository extends R2dbcRepository<PessoaMedico, Long>, PessoaMedicoRepositoryInternal {
    // just to avoid having unambigous methods
    @Override
    Flux<PessoaMedico> findAll();

    @Override
    Mono<PessoaMedico> findById(Long id);

    @Override
    <S extends PessoaMedico> Mono<S> save(S entity);
}

interface PessoaMedicoRepositoryInternal {
    <S extends PessoaMedico> Mono<S> insert(S entity);
    <S extends PessoaMedico> Mono<S> save(S entity);
    Mono<Integer> update(PessoaMedico entity);

    Flux<PessoaMedico> findAll();
    Mono<PessoaMedico> findById(Long id);
    Flux<PessoaMedico> findAllBy(Pageable pageable);
    Flux<PessoaMedico> findAllBy(Pageable pageable, Criteria criteria);
}
