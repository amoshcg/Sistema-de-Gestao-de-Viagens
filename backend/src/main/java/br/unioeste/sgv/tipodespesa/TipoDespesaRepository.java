package br.unioeste.sgv.tipodespesa;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoDespesaRepository extends JpaRepository<TipoDespesa, Long> {

    List<TipoDespesa> findAllByOrderByNomeAsc();
}
