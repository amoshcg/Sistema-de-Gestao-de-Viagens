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

    @Query("select coalesce(sum(d.valor), 0) from Despesa d "
            + "where d.viagem.id = :viagemId and d.tipoDespesa.nome in :nomesTipo")
    BigDecimal somarValorPorViagemETipos(@Param("viagemId") Long viagemId, @Param("nomesTipo") List<String> nomesTipo);

    /** Indicador gerencial (secao 7): valor total gasto com viagens, em toda a base. */
    @Query("select coalesce(sum(d.valor), 0) from Despesa d")
    BigDecimal somarValorTotal();

    /** Quantidade de viagens distintas com ao menos uma despesa lancada, usada no custo medio. */
    @Query("select count(distinct d.viagem.id) from Despesa d")
    long contarViagensComDespesa();
}
