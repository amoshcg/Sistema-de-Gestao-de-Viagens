package br.unioeste.sgv.common;

/** Recurso solicitado nao existe na base (ex.: viagem ou empregado com id inexistente). */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
