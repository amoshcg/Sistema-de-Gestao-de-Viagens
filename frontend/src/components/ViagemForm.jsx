import { useState } from 'react';
import { cadastrarViagem } from '../api.js';

const MEIOS_TRANSPORTE = [
  { valor: 'AEREO', rotulo: 'Aéreo' },
  { valor: 'RODOVIARIO', rotulo: 'Rodoviário' },
  { valor: 'VEICULO_PROPRIO', rotulo: 'Veículo próprio' },
];

const FORM_VAZIO = {
  destino: '',
  dataSaida: '',
  dataRetorno: '',
  motivo: '',
  meioTransporte: '',
  responsavel: '',
};

/**
 * RNF-ALT-001: as validações de campos obrigatórios (RN-CAD-003) e de
 * consistência do período (RN-CAD-004) são aplicadas aqui e também no backend.
 */
function validar(form) {
  const erros = {};

  if (!form.destino.trim()) erros.destino = 'O destino é obrigatório';
  if (!form.dataSaida) erros.dataSaida = 'A data de saída é obrigatória';
  if (!form.dataRetorno) erros.dataRetorno = 'A data de retorno é obrigatória';
  if (!form.motivo.trim()) erros.motivo = 'O motivo é obrigatório';
  if (!form.meioTransporte) erros.meioTransporte = 'O meio de transporte é obrigatório';
  if (!form.responsavel.trim()) erros.responsavel = 'O responsável é obrigatório';

  if (form.dataSaida && form.dataRetorno && form.dataRetorno < form.dataSaida) {
    erros.dataRetorno = 'A data de retorno deve ser igual ou posterior à data de saída';
  }

  return erros;
}

export default function ViagemForm({ aoCadastrar }) {
  const [form, setForm] = useState(FORM_VAZIO);
  const [erros, setErros] = useState({});
  const [enviando, setEnviando] = useState(false);
  const [sucesso, setSucesso] = useState(null);
  const [falha, setFalha] = useState(null);

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

    const errosLocais = validar(form);
    if (Object.keys(errosLocais).length > 0) {
      setErros(errosLocais);
      return;
    }

    setEnviando(true);
    try {
      const viagem = await cadastrarViagem(form);
      setForm(FORM_VAZIO);
      setErros({});
      setSucesso(`Viagem para ${viagem.destino} cadastrada como rascunho.`);
      aoCadastrar();
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
      <h2>Cadastrar viagem</h2>

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
            <label htmlFor="meioTransporte">Meio de transporte *</label>
            <select
              id="meioTransporte"
              name="meioTransporte"
              value={form.meioTransporte}
              onChange={alterarCampo}
            >
              <option value="">Selecione...</option>
              {MEIOS_TRANSPORTE.map((meio) => (
                <option key={meio.valor} value={meio.valor}>
                  {meio.rotulo}
                </option>
              ))}
            </select>
            {erros.meioTransporte && <span className="erro">{erros.meioTransporte}</span>}
          </div>

          <div className="campo">
            <label htmlFor="responsavel">Responsável *</label>
            <input
              id="responsavel"
              name="responsavel"
              type="text"
              maxLength={120}
              value={form.responsavel}
              onChange={alterarCampo}
              placeholder="Ex.: Carlos Penteado"
            />
            {erros.responsavel && <span className="erro">{erros.responsavel}</span>}
          </div>
        </div>

        <button type="submit" disabled={enviando}>
          {enviando ? 'Salvando...' : 'Cadastrar viagem'}
        </button>

        {sucesso && <p className="aviso sucesso">{sucesso}</p>}
        {falha && <p className="aviso falha">{falha}</p>}
      </form>
    </section>
  );
}
