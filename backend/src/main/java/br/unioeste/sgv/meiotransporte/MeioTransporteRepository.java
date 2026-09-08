package br.unioeste.sgv.meiotransporte;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeioTransporteRepository extends JpaRepository<MeioTransporte, Long> {

    List<MeioTransporte> findAllByOrderByDescricaoAsc();
}
