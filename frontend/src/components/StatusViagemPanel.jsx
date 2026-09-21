import { useState } from 'react';
import { cadastrarStatusViagem } from '../api.js';

export default function StatusViagemPanel({ statusViagem, carregando, erro, aoCadastrar }) {
  const [descricao, setDescricao] = useState('');
  const [erroCampo, setErroCampo] = useState(null);
  const [enviando, setEnviando] = useState(false);
  const [falha, setFalha] = useState(null);

  async function enviar(evento) {
    evento.preventDefault();
    setFalha(null);

    if (!descricao.trim()) {
      setErroCampo('A descrição é obrigatória');
      return;
    }

    setEnviando(true);
    try {
      await cadastrarStatusViagem({ descricao });
      setDescricao('');
      setErroCampo(null);
      aoCadastrar();
    } catch (e) {
      const mensagem = e.erros?.descricao ?? e.message;
      setErroCampo(e.erros?.descricao ? mensagem : null);
      if (!e.erros?.descricao) setFalha(mensagem);
    } finally {
      setEnviando(false);
    }
  }

  return (
    <section className="cartao">
      <h2>Status de viagem</h2>

      <form onSubmit={enviar} noValidate className="linha linha-area">
        <div className="campo">
          <label htmlFor="descricao">Descrição *</label>
          <input
            id="descricao"
            type="text"
            maxLength={50}
            value={descricao}
            onChange={(evento) => {
              setDescricao(evento.target.value);
              setErroCampo(null);
            }}
            placeholder="Ex.: Aguardando documentação"
          />
          {erroCampo && <span className="erro">{erroCampo}</span>}
        </div>

        <button type="submit" disabled={enviando}>
          {enviando ? 'Salvando...' : 'Cadastrar status'}
        </button>
      </form>

      {falha && <p className="aviso falha">{falha}</p>}
      {carregando && <p className="aviso">Carregando status de viagem...</p>}
      {erro && <p className="aviso falha">{erro}</p>}

      {!carregando && !erro && (
        <ul className="lista-empregados">
          {statusViagem.map((status) => (
            <li key={status.id}>{status.descricao}</li>
          ))}
          {statusViagem.length === 0 && <li className="aviso">Nenhum status cadastrado.</li>}
        </ul>
      )}
    </section>
  );
}
