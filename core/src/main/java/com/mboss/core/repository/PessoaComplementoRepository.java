package com.mboss.core.repository;

import com.mboss.core.domain.PessoaComplemento;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the PessoaComplemento entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PessoaComplementoRepository extends R2dbcRepository<PessoaComplemento, Long>, PessoaComplementoRepositoryInternal {
    @Query("SELECT * FROM pessoa_complemento entity WHERE entity.id not in (select pessoa_complemento_id from pessoa_fisca)")
    Flux<PessoaComplemento> findAllWherePessoaFiscaIsNull();

    @Query("SELECT * FROM pessoa_complemento entity WHERE entity.id not in (select pessoa_complemento_id from pessoa_juridica)")
    Flux<PessoaComplemento> findAllWherePessoaJuridicaIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<PessoaComplemento> findAll();

    @Override
    Mono<PessoaComplemento> findById(Long id);

    @Override
    <S extends PessoaComplemento> Mono<S> save(S entity);
}

interface PessoaComplementoRepositoryInternal {
    <S extends PessoaComplemento> Mono<S> insert(S entity);
    <S extends PessoaComplemento> Mono<S> save(S entity);
    Mono<Integer> update(PessoaComplemento entity);

    Flux<PessoaComplemento> findAll();
    Mono<PessoaComplemento> findById(Long id);
    Flux<PessoaComplemento> findAllBy(Pageable pageable);
    Flux<PessoaComplemento> findAllBy(Pageable pageable, Criteria criteria);
}
