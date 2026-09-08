import { useEffect, useState } from 'react';
import { cadastrarEmpregado, alterarEmpregado } from '../api.js';

const FORM_VAZIO = { matricula: '', nome: '', areaId: '', cargoId: '' };

function formularioDoEmpregado(empregado) {
  return {
    matricula: empregado.matricula,
    nome: empregado.nome,
    areaId: String(empregado.areaId),
    cargoId: String(empregado.cargoId),
  };
}

function validar(form, exigirMatricula) {
  const erros = {};
  if (exigirMatricula && !form.matricula.trim()) erros.matricula = 'A matrícula é obrigatória';
  if (!form.nome.trim()) erros.nome = 'O nome é obrigatório';
  if (!form.areaId) erros.areaId = 'A área é obrigatória';
  if (!form.cargoId) erros.cargoId = 'O cargo é obrigatório';
  return erros;
}

export default function EmpregadoPanel({
  empregados,
  areas,
  cargos,
  carregando,
  erro,
  aoCadastrar,
  empregadoEditando,
  aoEditar,
  aoCancelarEdicao,
}) {
  const editando = Boolean(empregadoEditando);
  const [form, setForm] = useState(editando ? formularioDoEmpregado(empregadoEditando) : FORM_VAZIO);
  const [erros, setErros] = useState({});
  const [enviando, setEnviando] = useState(false);
  const [falha, setFalha] = useState(null);

  useEffect(() => {
    setForm(editando ? formularioDoEmpregado(empregadoEditando) : FORM_VAZIO);
    setErros({});
    setFalha(null);
  }, [empregadoEditando]); // eslint-disable-line react-hooks/exhaustive-deps

  function alterarCampo(evento) {
    const { name, value } = evento.target;
    setForm((atual) => ({ ...atual, [name]: value }));
    setErros((atual) => ({ ...atual, [name]: undefined }));
  }

  async function enviar(evento) {
    evento.preventDefault();
    setFalha(null);

    const errosLocais = validar(form, !editando);
    if (Object.keys(errosLocais).length > 0) {
      setErros(errosLocais);
      return;
    }

    setEnviando(true);
    try {
      if (editando) {
        const { matricula, ...dados } = form; // a matricula e imutavel apos o cadastro
        await alterarEmpregado(empregadoEditando.id, {
          ...dados,
          areaId: Number(dados.areaId),
          cargoId: Number(dados.cargoId),
        });
        aoCancelarEdicao();
      } else {
        await cadastrarEmpregado({
          ...form,
          areaId: Number(form.areaId),
          cargoId: Number(form.cargoId),
        });
        setForm(FORM_VAZIO);
        setErros({});
      }
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
      <h2>{editando ? `Editar empregado — ${empregadoEditando.matricula}` : 'Empregados'}</h2>

      <form onSubmit={enviar} noValidate className="linha linha-empregado">
        <div className="campo">
          <label htmlFor="matricula">Matrícula *</label>
          {editando ? (
            <input id="matricula" type="text" disabled value={form.matricula} />
          ) : (
            <input
              id="matricula"
              name="matricula"
              type="text"
              maxLength={20}
              value={form.matricula}
              onChange={alterarCampo}
              placeholder="Ex.: 1234-5"
            />
          )}
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

        <div className="campo">
          <label htmlFor="cargoId">Cargo *</label>
          <select id="cargoId" name="cargoId" value={form.cargoId} onChange={alterarCampo}>
            <option value="">Selecione...</option>
            {cargos.map((cargo) => (
              <option key={cargo.id} value={cargo.id}>
                {cargo.nome}
              </option>
            ))}
          </select>
          {erros.cargoId && <span className="erro">{erros.cargoId}</span>}
        </div>

        <div className="acoes-form">
          <button type="submit" disabled={enviando}>
            {enviando ? 'Salvando...' : editando ? 'Salvar alterações' : 'Cadastrar empregado'}
          </button>
          {editando && (
            <button type="button" className="botao-secundario" onClick={aoCancelarEdicao} disabled={enviando}>
              Cancelar
            </button>
          )}
        </div>
      </form>

      {falha && <p className="aviso falha">{falha}</p>}
      {carregando && <p className="aviso">Carregando empregados...</p>}
      {erro && <p className="aviso falha">{erro}</p>}

      {!carregando && !erro && (
        <ul className="lista-empregados">
          {empregados.map((empregado) => (
            <li key={empregado.id}>
              <strong>{empregado.nome}</strong> — {empregado.matricula} ({empregado.areaNome}, {empregado.cargoNome})
              <button type="button" className="botao-secundario" onClick={() => aoEditar(empregado)}>
                Editar
              </button>
            </li>
          ))}
          {empregados.length === 0 && <li className="aviso">Nenhum empregado cadastrado.</li>}
        </ul>
      )}
    </section>
  );
}
