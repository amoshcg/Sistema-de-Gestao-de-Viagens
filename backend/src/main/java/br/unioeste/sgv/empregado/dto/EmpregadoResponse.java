package br.unioeste.sgv.empregado.dto;

import br.unioeste.sgv.empregado.Empregado;

public record EmpregadoResponse(
        Long id,
        String matricula,
        String nome,
        Long areaId,
        String areaNome,
        Long cargoId,
        String cargoNome
) {

    public static EmpregadoResponse de(Empregado empregado) {
        return new EmpregadoResponse(
                empregado.getId(),
                empregado.getMatricula(),
                empregado.getNome(),
                empregado.getArea().getId(),
                empregado.getArea().getNome(),
                empregado.getCargo().getId(),
                empregado.getCargo().getNome()
        );
    }
}
