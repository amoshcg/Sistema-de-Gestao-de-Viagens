import { useState } from 'react';
import { cadastrarEmpregado } from '../api.js';

const FORM_VAZIO = { matricula: '', nome: '', areaId: '' };

function validar(form) {
  const erros = {};
  if (!form.matricula.trim()) erros.matricula = 'A matrícula é obrigatória';
  if (!form.nome.trim()) erros.nome = 'O nome é obrigatório';
  if (!form.areaId) erros.areaId = 'A área é obrigatória';
  return erros;
}

export default function EmpregadoPanel({ empregados, areas, carregando, erro, aoCadastrar }) {
  const [form, setForm] = useState(FORM_VAZIO);
  const [erros, setErros] = useState({});
  const [enviando, setEnviando] = useState(false);
  const [falha, setFalha] = useState(null);

  function alterarCampo(evento) {
    const { name, value } = evento.target;
    setForm((atual) => ({ ...atual, [name]: value }));
    setErros((atual) => ({ ...atual, [name]: undefined }));
  }

  async function enviar(evento) {
    evento.preventDefault();
    setFalha(null);

    const errosLocais = validar(form);
    if (Object.keys(errosLocais).length > 0) {
      setErros(errosLocais);
      return;
    }

    setEnviando(true);
    try {
      await cadastrarEmpregado({ ...form, areaId: Number(form.areaId) });
      setForm(FORM_VAZIO);
      setErros({});
      aoCadastrar();
    } catch (e) {
      setErros(e.erros ?? {});
      if (!e.erros || Object.keys(e.erros).length === 0) setFalha(e.message);
    } finally {
      setEnviando(false);
    }
  }

  return (
    <section className="cartao">
      <h2>Empregados</h2>

      <form onSubmit={enviar} noValidate className="linha linha-empregado">
        <div className="campo">
          <label htmlFor="matricula">Matrícula *</label>
          <input
            id="matricula"
            name="matricula"
            type="text"
            maxLength={20}
            value={form.matricula}
            onChange={alterarCampo}
            placeholder="Ex.: E001"
          />
          {erros.matricula && <span className="erro">{erros.matricula}</span>}
        </div>

        <div className="campo">
          <label htmlFor="nome">Nome *</label>
          <input
            id="nome"
            name="nome"
            type="text"
            maxLength={120}
            value={form.nome}
            onChange={alterarCampo}
            placeholder="Ex.: Maria Oliveira"
          />
          {erros.nome && <span className="erro">{erros.nome}</span>}
        </div>

        <div className="campo">
          <label htmlFor="areaId">Área *</label>
          <select id="areaId" name="areaId" value={form.areaId} onChange={alterarCampo}>
            <option value="">Selecione...</option>
            {areas.map((area) => (
              <option key={area.id} value={area.id}>
                {area.nome}
              </option>
            ))}
          </select>
          {erros.areaId && <span className="erro">{erros.areaId}</span>}
        </div>

        <button type="submit" disabled={enviando}>
          {enviando ? 'Salvando...' : 'Cadastrar empregado'}
        </button>
      </form>

      {falha && <p className="aviso falha">{falha}</p>}
      {carregando && <p className="aviso">Carregando empregados...</p>}
      {erro && <p className="aviso falha">{erro}</p>}

      {!carregando && !erro && (
        <ul className="lista-empregados">
          {empregados.map((empregado) => (
            <li key={empregado.id}>
              <strong>{empregado.nome}</strong> — {empregado.matricula} ({empregado.areaNome})
            </li>
          ))}
          {empregados.length === 0 && <li className="aviso">Nenhum empregado cadastrado.</li>}
        </ul>
      )}
    </section>
  );
}
