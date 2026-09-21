package br.unioeste.sgv.viagem;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

/** RF#6: monta os filtros opcionais e combinaveis da pesquisa de viagens. */
final class ViagemSpecifications {

    private ViagemSpecifications() {
    }

    /**
     * Filtra por destino (contem, sem diferenciar maiusculas/minusculas), periodo (viagens
     * cujo periodo tem alguma intersecao com [dataInicio, dataFim]) e situacao. Qualquer
     * filtro nulo simplesmente nao vira predicado.
     */
    static Specification<Viagem> pesquisar(String destino, LocalDate dataInicio, LocalDate dataFim, String situacao) {
        return (root, query, cb) -> {
            List<Predicate> predicados = new ArrayList<>();
            if (destino != null) {
                predicados.add(cb.like(cb.lower(root.get("destino")), "%" + destino.toLowerCase() + "%"));
            }
            if (dataInicio != null) {
                predicados.add(cb.greaterThanOrEqualTo(root.get("dataRetorno"), dataInicio));
            }
            if (dataFim != null) {
                predicados.add(cb.lessThanOrEqualTo(root.get("dataSaida"), dataFim));
            }
            if (situacao != null) {
                predicados.add(cb.equal(cb.lower(root.get("statusViagem").get("descricao")), situacao.toLowerCase()));
            }
            return cb.and(predicados.toArray(new Predicate[0]));
        };
    }
}
