import { useState } from 'react';
import { cadastrarCargo } from '../api.js';

export default function CargoPanel({ cargos, carregando, erro, aoCadastrar }) {
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
      await cadastrarCargo({ nome });
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
      <h2>Cargos</h2>

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
            placeholder="Ex.: Gestor"
          />
          {erroCampo && <span className="erro">{erroCampo}</span>}
        </div>

        <button type="submit" disabled={enviando}>
          {enviando ? 'Salvando...' : 'Cadastrar cargo'}
        </button>
      </form>

      {falha && <p className="aviso falha">{falha}</p>}
      {carregando && <p className="aviso">Carregando cargos...</p>}
      {erro && <p className="aviso falha">{erro}</p>}

      {!carregando && !erro && (
        <ul className="lista-empregados">
          {cargos.map((cargo) => (
            <li key={cargo.id}>{cargo.nome}</li>
          ))}
          {cargos.length === 0 && <li className="aviso">Nenhum cargo cadastrado.</li>}
        </ul>
      )}
    </section>
  );
}
