package br.unioeste.sgv.common;

/** Operacao incompativel com o estado atual do recurso (ex.: RN-ALT-001, RN-SUB-001, matricula duplicada). */
public class ConflitoException extends RuntimeException {

    public ConflitoException(String mensagem) {
        super(mensagem);
    }
}
