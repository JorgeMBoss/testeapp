package com.mboss.core.repository;

import com.mboss.core.domain.PessoaFuncao;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the PessoaFuncao entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PessoaFuncaoRepository extends R2dbcRepository<PessoaFuncao, Long>, PessoaFuncaoRepositoryInternal {
    @Query("SELECT * FROM pessoa_funcao entity WHERE entity.id not in (select pessoa_funcao_id from pessoa_colaborador)")
    Flux<PessoaFuncao> findAllWherePessoaColaboradorIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<PessoaFuncao> findAll();

    @Override
    Mono<PessoaFuncao> findById(Long id);

    @Override
    <S extends PessoaFuncao> Mono<S> save(S entity);
}

interface PessoaFuncaoRepositoryInternal {
    <S extends PessoaFuncao> Mono<S> insert(S entity);
    <S extends PessoaFuncao> Mono<S> save(S entity);
    Mono<Integer> update(PessoaFuncao entity);

    Flux<PessoaFuncao> findAll();
    Mono<PessoaFuncao> findById(Long id);
    Flux<PessoaFuncao> findAllBy(Pageable pageable);
    Flux<PessoaFuncao> findAllBy(Pageable pageable, Criteria criteria);
}
