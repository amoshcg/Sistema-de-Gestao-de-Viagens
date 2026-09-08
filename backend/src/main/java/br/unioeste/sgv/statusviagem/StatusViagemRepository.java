package br.unioeste.sgv.statusviagem;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusViagemRepository extends JpaRepository<StatusViagem, Long> {

    boolean existsByDescricao(String descricao);

    Optional<StatusViagem> findByDescricaoIgnoreCase(String descricao);

    List<StatusViagem> findAllByOrderByIdAsc();
}
