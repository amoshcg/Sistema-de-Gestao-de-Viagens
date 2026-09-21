import { useState } from 'react';

export default function AcaoGestorForm({ gestores, ocupado, aoAprovar, aoRejeitar, aoAjustar }) {
  const [gestorId, setGestorId] = useState('');
  const [justificativa, setJustificativa] = useState('');
  const [erro, setErro] = useState(null);

  function exigirGestor() {
    if (!gestorId) {
      setErro('Selecione o gestor responsável pela análise');
      return false;
    }
    setErro(null);
    return true;
  }

  function exigirJustificativa() {
    if (!gestorId) {
      setErro('Selecione o gestor responsável pela análise');
      return false;
    }
    if (!justificativa.trim()) {
      setErro('A justificativa é obrigatória');
      return false;
    }
    setErro(null);
    return true;
  }

  async function aprovar() {
    if (!exigirGestor()) return;
    await aoAprovar(Number(gestorId));
  }

  async function rejeitar() {
    if (!exigirJustificativa()) return;
    await aoRejeitar(Number(gestorId), justificativa.trim());
  }

  async function ajustar() {
    if (!exigirJustificativa()) return;
    await aoAjustar(Number(gestorId), justificativa.trim());
  }

  return (
    <div className="acao-gestor">
      <select value={gestorId} onChange={(evento) => setGestorId(evento.target.value)} disabled={ocupado}>
        <option value="">Gestor responsável...</option>
        {gestores.map((gestor) => (
          <option key={gestor.id} value={gestor.id}>
            {gestor.nome}
          </option>
        ))}
      </select>

      <textarea
        rows={2}
        maxLength={500}
        placeholder="Justificativa (obrigatória para rejeitar ou solicitar ajuste)"
        value={justificativa}
        onChange={(evento) => setJustificativa(evento.target.value)}
        disabled={ocupado}
      />

      <div className="acoes-tabela">
        <button type="button" onClick={aprovar} disabled={ocupado}>
          Aprovar
        </button>
        <button type="button" className="botao-secundario" onClick={ajustar} disabled={ocupado}>
          Solicitar ajuste
        </button>
        <button type="button" className="botao-perigo" onClick={rejeitar} disabled={ocupado}>
          Rejeitar
        </button>
      </div>

      {erro && <span className="erro">{erro}</span>}
    </div>
  );
}
