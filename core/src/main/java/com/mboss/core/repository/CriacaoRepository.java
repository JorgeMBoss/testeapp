package com.mboss.core.repository;

import com.mboss.core.domain.Criacao;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Criacao entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CriacaoRepository extends R2dbcRepository<Criacao, Long>, CriacaoRepositoryInternal {
    Flux<Criacao> findAllBy(Pageable pageable);

    @Query("SELECT * FROM criacao entity WHERE entity.criacao_cor_id = :id")
    Flux<Criacao> findByCriacaoCor(Long id);

    @Query("SELECT * FROM criacao entity WHERE entity.criacao_cor_id IS NULL")
    Flux<Criacao> findAllWhereCriacaoCorIsNull();

    @Query("SELECT * FROM criacao entity WHERE entity.criacao_raca_id = :id")
    Flux<Criacao> findByCriacaoRaca(Long id);

    @Query("SELECT * FROM criacao entity WHERE entity.criacao_raca_id IS NULL")
    Flux<Criacao> findAllWhereCriacaoRacaIsNull();

    @Query("SELECT * FROM criacao entity WHERE entity.criacao_especie_id = :id")
    Flux<Criacao> findByCriacaoEspecie(Long id);

    @Query("SELECT * FROM criacao entity WHERE entity.criacao_especie_id IS NULL")
    Flux<Criacao> findAllWhereCriacaoEspecieIsNull();

    @Query("SELECT * FROM criacao entity WHERE entity.pessoa_criacao_id = :id")
    Flux<Criacao> findByPessoaCriacao(Long id);

    @Query("SELECT * FROM criacao entity WHERE entity.pessoa_criacao_id IS NULL")
    Flux<Criacao> findAllWherePessoaCriacaoIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Criacao> findAll();

    @Override
    Mono<Criacao> findById(Long id);

    @Override
    <S extends Criacao> Mono<S> save(S entity);
}

interface CriacaoRepositoryInternal {
    <S extends Criacao> Mono<S> insert(S entity);
    <S extends Criacao> Mono<S> save(S entity);
    Mono<Integer> update(Criacao entity);

    Flux<Criacao> findAll();
    Mono<Criacao> findById(Long id);
    Flux<Criacao> findAllBy(Pageable pageable);
    Flux<Criacao> findAllBy(Pageable pageable, Criteria criteria);
}
