package br.unioeste.sgv.area;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AreaRepository extends JpaRepository<Area, Long> {

    boolean existsByNome(String nome);

    List<Area> findAllByOrderByNomeAsc();
}
