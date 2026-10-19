package br.unioeste.sgv.cargo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CargoRepository extends JpaRepository<Cargo, Long> {

    boolean existsByNome(String nome);

    List<Cargo> findAllByOrderByNomeAsc();
}
