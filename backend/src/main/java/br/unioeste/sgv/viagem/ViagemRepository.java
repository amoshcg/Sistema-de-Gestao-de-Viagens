package br.unioeste.sgv.viagem;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * RF#6 (Consultas e pesquisas): os filtros opcionais e combinaveis (destino, periodo,
 * situacao) sao montados dinamicamente via Specification em ViagemSpecifications, em vez de
 * um "@Query" com "(:param is null or ...)" -- essa forma faz o Postgres tentar inferir o
 * tipo de cada parametro a partir do texto da consulta preparada, e um filtro ausente (bind
 * nulo usado so num "is null" isolado, ou repassado para lower()/cast()) nao da contexto
 * suficiente, falhando com "could not determine data type of parameter" ou "cannot cast
 * type bytea to ..." assim que a consulta passa a ser preparada no servidor. Com
 * Specification, um filtro ausente simplesmente nao vira predicado nem parametro.
 */
public interface ViagemRepository extends JpaRepository<Viagem, Long>, JpaSpecificationExecutor<Viagem> {

    /** RF#6: destinos distintos ja cadastrados, para alimentar o dropdown de pesquisa. */
    @Query("select distinct v.destino from Viagem v order by v.destino")
    List<String> listarDestinosDistintos();
}
