package br.unioeste.sgv.viagem;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ViagemStatusHistoricoRepository extends JpaRepository<ViagemStatusHistorico, Long> {

    List<ViagemStatusHistorico> findAllByViagemIdOrderByDataMudancaAscIdAsc(Long viagemId);
}
