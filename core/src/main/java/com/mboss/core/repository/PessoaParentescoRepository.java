package com.mboss.core.repository;

import com.mboss.core.domain.PessoaParentesco;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the PessoaParentesco entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PessoaParentescoRepository extends R2dbcRepository<PessoaParentesco, Long>, PessoaParentescoRepositoryInternal {
    @Query("SELECT * FROM pessoa_parentesco entity WHERE entity.pessoa_fisca_id = :id")
    Flux<PessoaParentesco> findByPessoaFisca(Long id);

    @Query("SELECT * FROM pessoa_parentesco entity WHERE entity.pessoa_fisca_id IS NULL")
    Flux<PessoaParentesco> findAllWherePessoaFiscaIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<PessoaParentesco> findAll();

    @Override
    Mono<PessoaParentesco> findById(Long id);

    @Override
    <S extends PessoaParentesco> Mono<S> save(S entity);
}

interface PessoaParentescoRepositoryInternal {
    <S extends PessoaParentesco> Mono<S> insert(S entity);
    <S extends PessoaParentesco> Mono<S> save(S entity);
    Mono<Integer> update(PessoaParentesco entity);

    Flux<PessoaParentesco> findAll();
    Mono<PessoaParentesco> findById(Long id);
    Flux<PessoaParentesco> findAllBy(Pageable pageable);
    Flux<PessoaParentesco> findAllBy(Pageable pageable, Criteria criteria);
}
