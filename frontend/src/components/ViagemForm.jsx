import { useEffect, useState } from 'react';
import { cadastrarViagem, alterarViagem } from '../api.js';

const FORM_VAZIO = {
  destino: '',
  dataSaida: '',
  dataRetorno: '',
  motivo: '',
  meioTransporteId: '',
  empregadoId: '',
};

function formularioDaViagem(viagem) {
  return {
    destino: viagem.destino,
    dataSaida: viagem.dataSaida,
    dataRetorno: viagem.dataRetorno,
    motivo: viagem.motivo,
    meioTransporteId: String(viagem.meioTransporteId),
    empregadoId: String(viagem.empregadoId),
  };
}

/**
 * RNF-ALT-001: as validações de campos obrigatórios (RN-CAD-003) e de
 * consistência do período (RN-CAD-004) são aplicadas aqui e também no backend.
 */
function validar(form, exigirEmpregado) {
  const erros = {};

  if (!form.destino.trim()) erros.destino = 'O destino é obrigatório';
  if (!form.dataSaida) erros.dataSaida = 'A data de saída é obrigatória';
  if (!form.dataRetorno) erros.dataRetorno = 'A data de retorno é obrigatória';
  if (!form.motivo.trim()) erros.motivo = 'O motivo é obrigatório';
  if (!form.meioTransporteId) erros.meioTransporteId = 'O meio de transporte é obrigatório';
  if (exigirEmpregado && !form.empregadoId) erros.empregadoId = 'O empregado é obrigatório';

  if (form.dataSaida && form.dataRetorno && form.dataRetorno < form.dataSaida) {
    erros.dataRetorno = 'A data de retorno deve ser igual ou posterior à data de saída';
  }

  return erros;
}

export default function ViagemForm({ empregados, meiosTransporte, viagemEditando, aoSalvar, aoCancelarEdicao }) {
  const editando = Boolean(viagemEditando);
  const [form, setForm] = useState(editando ? formularioDaViagem(viagemEditando) : FORM_VAZIO);
  const [erros, setErros] = useState({});
  const [enviando, setEnviando] = useState(false);
  const [sucesso, setSucesso] = useState(null);
  const [falha, setFalha] = useState(null);

  useEffect(() => {
    setForm(editando ? formularioDaViagem(viagemEditando) : FORM_VAZIO);
    setErros({});
    setSucesso(null);
    setFalha(null);
  }, [viagemEditando]); // eslint-disable-line react-hooks/exhaustive-deps

  function alterarCampo(evento) {
    const { name, value } = evento.target;
    setForm((atual) => ({ ...atual, [name]: value }));
    setErros((atual) => ({ ...atual, [name]: undefined }));
    setSucesso(null);
  }

  async function enviar(evento) {
    evento.preventDefault();
    setFalha(null);
    setSucesso(null);

    const errosLocais = validar(form, !editando);
    if (Object.keys(errosLocais).length > 0) {
      setErros(errosLocais);
      return;
    }

    setEnviando(true);
    try {
      if (editando) {
        const { empregadoId, ...dados } = form; // RN-CAD-001: empregado é imutável após a criação
        const viagem = await alterarViagem(viagemEditando.id, {
          ...dados,
          meioTransporteId: Number(dados.meioTransporteId),
        });
        setSucesso(`Viagem nº ${viagem.numero} atualizada.`);
        aoSalvar();
      } else {
        const viagem = await cadastrarViagem({
          ...form,
          meioTransporteId: Number(form.meioTransporteId),
          empregadoId: Number(form.empregadoId),
        });
        setForm(FORM_VAZIO);
        setErros({});
        setSucesso(`Viagem nº ${viagem.numero} para ${viagem.destino} cadastrada como rascunho.`);
        aoSalvar();
      }
    } catch (e) {
      // O backend devolve os erros por campo; "periodoValido" refere-se à RN-CAD-004.
      const { periodoValido, ...porCampo } = e.erros ?? {};
      if (periodoValido) porCampo.dataRetorno = periodoValido;
      setErros(porCampo);
      if (Object.keys(porCampo).length === 0) setFalha(e.message);
    } finally {
      setEnviando(false);
    }
  }

  return (
    <section className="cartao">
      <h2>{editando ? `Editar viagem nº ${viagemEditando.numero}` : 'Cadastrar viagem'}</h2>

      <form onSubmit={enviar} noValidate>
        <div className="campo">
          <label htmlFor="destino">Destino *</label>
          <input
            id="destino"
            name="destino"
            type="text"
            maxLength={120}
            value={form.destino}
            onChange={alterarCampo}
            placeholder="Ex.: Curitiba - PR"
          />
          {erros.destino && <span className="erro">{erros.destino}</span>}
        </div>

        <div className="linha">
          <div className="campo">
            <label htmlFor="dataSaida">Data de saída *</label>
            <input
              id="dataSaida"
              name="dataSaida"
              type="date"
              value={form.dataSaida}
              onChange={alterarCampo}
            />
            {erros.dataSaida && <span className="erro">{erros.dataSaida}</span>}
          </div>

          <div className="campo">
            <label htmlFor="dataRetorno">Data de retorno *</label>
            <input
              id="dataRetorno"
              name="dataRetorno"
              type="date"
              value={form.dataRetorno}
              onChange={alterarCampo}
            />
            {erros.dataRetorno && <span className="erro">{erros.dataRetorno}</span>}
          </div>
        </div>

        <div className="campo">
          <label htmlFor="motivo">Motivo *</label>
          <textarea
            id="motivo"
            name="motivo"
            rows={3}
            maxLength={500}
            value={form.motivo}
            onChange={alterarCampo}
            placeholder="Ex.: Reunião com cliente"
          />
          {erros.motivo && <span className="erro">{erros.motivo}</span>}
        </div>

        <div className="linha">
          <div className="campo">
            <label htmlFor="meioTransporteId">Meio de transporte *</label>
            <select
              id="meioTransporteId"
              name="meioTransporteId"
              value={form.meioTransporteId}
              onChange={alterarCampo}
            >
              <option value="">Selecione...</option>
              {meiosTransporte.map((meio) => (
                <option key={meio.id} value={meio.id}>
                  {meio.descricao}
                </option>
              ))}
            </select>
            {erros.meioTransporteId && <span className="erro">{erros.meioTransporteId}</span>}
          </div>

          <div className="campo">
            <label htmlFor="empregadoId">Empregado *</label>
            {editando ? (
              <input
                id="empregadoId"
                type="text"
                disabled
                value={`${viagemEditando.empregadoNome} (${viagemEditando.empregadoMatricula})`}
              />
            ) : (
              <select
                id="empregadoId"
                name="empregadoId"
                value={form.empregadoId}
                onChange={alterarCampo}
              >
                <option value="">Selecione...</option>
                {empregados.map((empregado) => (
                  <option key={empregado.id} value={empregado.id}>
                    {empregado.nome} — {empregado.matricula} ({empregado.areaNome})
                  </option>
                ))}
              </select>
            )}
            {erros.empregadoId && <span className="erro">{erros.empregadoId}</span>}
          </div>
        </div>

        <div className="acoes-form">
          <button type="submit" disabled={enviando}>
            {enviando ? 'Salvando...' : editando ? 'Salvar alterações' : 'Cadastrar viagem'}
          </button>
          {editando && (
            <button type="button" className="botao-secundario" onClick={aoCancelarEdicao} disabled={enviando}>
              Cancelar
            </button>
          )}
        </div>

        {sucesso && <p className="aviso sucesso">{sucesso}</p>}
        {falha && <p className="aviso falha">{falha}</p>}
      </form>
    </section>
  );
}
