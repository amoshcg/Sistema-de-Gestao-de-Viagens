package br.unioeste.sgv.viagem;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ViagemRepository extends JpaRepository<Viagem, Long> {

    /** RF-CON-002: listagem priorizando as viagens mais recentes. */
    List<Viagem> findAllByOrderByCriadoEmDescIdDesc();
}
