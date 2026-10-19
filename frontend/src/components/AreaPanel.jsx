import { useState } from 'react';
import { cadastrarArea } from '../api.js';

export default function AreaPanel({ areas, carregando, erro, aoCadastrar }) {
  const [nome, setNome] = useState('');
  const [erroCampo, setErroCampo] = useState(null);
  const [enviando, setEnviando] = useState(false);
  const [falha, setFalha] = useState(null);

  async function enviar(evento) {
    evento.preventDefault();
    setFalha(null);

    if (!nome.trim()) {
      setErroCampo('O nome é obrigatório');
      return;
    }

    setEnviando(true);
    try {
      await cadastrarArea({ nome });
      setNome('');
      setErroCampo(null);
      aoCadastrar();
    } catch (e) {
      const mensagem = e.erros?.nome ?? e.message;
      setErroCampo(e.erros?.nome ? mensagem : null);
      if (!e.erros?.nome) setFalha(mensagem);
    } finally {
      setEnviando(false);
    }
  }

  return (
    <section className="cartao">
      <h2>Áreas</h2>

      <form onSubmit={enviar} noValidate className="linha linha-area">
        <div className="campo">
          <label htmlFor="nome">Nome *</label>
          <input
            id="nome"
            type="text"
            maxLength={45}
            value={nome}
            onChange={(evento) => {
              setNome(evento.target.value);
              setErroCampo(null);
            }}
            placeholder="Ex.: Comercial"
          />
          {erroCampo && <span className="erro">{erroCampo}</span>}
        </div>

        <button type="submit" disabled={enviando}>
          {enviando ? 'Salvando...' : 'Cadastrar área'}
        </button>
      </form>

      {falha && <p className="aviso falha">{falha}</p>}
      {carregando && <p className="aviso">Carregando áreas...</p>}
      {erro && <p className="aviso falha">{erro}</p>}

      {!carregando && !erro && (
        <ul className="lista-empregados">
          {areas.map((area) => (
            <li key={area.id}>{area.nome}</li>
          ))}
          {areas.length === 0 && <li className="aviso">Nenhuma área cadastrada.</li>}
        </ul>
      )}
    </section>
  );
}
