package br.unioeste.sgv.empregado;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpregadoRepository extends JpaRepository<Empregado, Long> {

    boolean existsByMatricula(String matricula);

    List<Empregado> findAllByOrderByNomeAsc();
}
