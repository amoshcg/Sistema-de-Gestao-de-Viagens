package br.unioeste.sgv.despesa;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DespesaRepository extends JpaRepository<Despesa, Long> {

    List<Despesa> findAllByViagemIdOrderByDataDespesaAscIdAsc(Long viagemId);

    @Query("select coalesce(sum(d.valor), 0) from Despesa d where d.viagem.id = :viagemId")
    BigDecimal somarValorPorViagem(@Param("viagemId") Long viagemId);
}
