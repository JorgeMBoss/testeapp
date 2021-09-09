package com.mboss.core.repository;

import com.mboss.core.domain.PessoaFisca;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the PessoaFisca entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PessoaFiscaRepository extends R2dbcRepository<PessoaFisca, Long>, PessoaFiscaRepositoryInternal {
    @Query("SELECT * FROM pessoa_fisca entity WHERE entity.pessoa_complemento_id = :id")
    Flux<PessoaFisca> findByPessoaComplemento(Long id);

    @Query("SELECT * FROM pessoa_fisca entity WHERE entity.pessoa_complemento_id IS NULL")
    Flux<PessoaFisca> findAllWherePessoaComplementoIsNull();

    @Query("SELECT * FROM pessoa_fisca entity WHERE entity.pessoa_id = :id")
    Flux<PessoaFisca> findByPessoa(Long id);

    @Query("SELECT * FROM pessoa_fisca entity WHERE entity.pessoa_id IS NULL")
    Flux<PessoaFisca> findAllWherePessoaIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<PessoaFisca> findAll();

    @Override
    Mono<PessoaFisca> findById(Long id);

    @Override
    <S extends PessoaFisca> Mono<S> save(S entity);
}

interface PessoaFiscaRepositoryInternal {
    <S extends PessoaFisca> Mono<S> insert(S entity);
    <S extends PessoaFisca> Mono<S> save(S entity);
    Mono<Integer> update(PessoaFisca entity);

    Flux<PessoaFisca> findAll();
    Mono<PessoaFisca> findById(Long id);
    Flux<PessoaFisca> findAllBy(Pageable pageable);
    Flux<PessoaFisca> findAllBy(Pageable pageable, Criteria criteria);
}
