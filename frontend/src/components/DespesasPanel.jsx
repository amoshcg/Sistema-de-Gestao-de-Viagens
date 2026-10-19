import { useEffect, useState } from 'react';
import { listarDespesasDaViagem, buscarResumoFinanceiro, buscarCustosViagem, cadastrarDespesa } from '../api.js';

function formatarData(iso) {
  const [ano, mes, dia] = iso.split('-');
  return `${dia}/${mes}/${ano}`;
}

function formatarValor(valor) {
  return Number(valor).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}

const hoje = () => new Date().toISOString().slice(0, 10);

export default function DespesasPanel({ viagemId, tiposDespesa }) {
  const [despesas, setDespesas] = useState([]);
  const [valorTotal, setValorTotal] = useState(0);
  const [custos, setCustos] = useState(null);
  const [carregando, setCarregando] = useState(true);
  const [falha, setFalha] = useState(null);

  const [dataDespesa, setDataDespesa] = useState(hoje());
  const [tipoDespesaId, setTipoDespesaId] = useState('');
  const [descricao, setDescricao] = useState('');
  const [valor, setValor] = useState('');
  const [errosCampo, setErrosCampo] = useState({});
  const [enviando, setEnviando] = useState(false);

  async function carregar() {
    setCarregando(true);
    try {
      const [resumo, custosViagem] = await Promise.all([
        buscarResumoFinanceiro(viagemId),
        buscarCustosViagem(viagemId),
      ]);
      setDespesas(resumo.despesas);
      setValorTotal(resumo.valorTotal);
      setCustos(custosViagem);
      setFalha(null);
    } catch (e) {
      setFalha(e.message);
    } finally {
      setCarregando(false);
    }
  }

  useEffect(() => {
    carregar();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [viagemId]);

  async function enviar(evento) {
    evento.preventDefault();
    setFalha(null);
    setErrosCampo({});

    if (!tipoDespesaId) {
      setErrosCampo({ tipoDespesaId: 'Selecione o tipo de despesa' });
      return;
    }

    setEnviando(true);
    try {
      await cadastrarDespesa(viagemId, {
        dataDespesa,
        descricao,
        valor: valor === '' ? null : Number(valor),
        tipoDespesaId: Number(tipoDespesaId),
      });
      setDescricao('');
      setValor('');
      setDataDespesa(hoje());
      await carregar();
    } catch (e) {
      if (e.erros && Object.keys(e.erros).length > 0) {
        setErrosCampo(e.erros);
      } else {
        setFalha(e.message);
      }
    } finally {
      setEnviando(false);
    }
  }

  return (
    <div className="despesas-viagem">
      <h3>Despesas da viagem</h3>

      <form onSubmit={enviar} noValidate className="linha linha-area">
        <div className="campo">
          <label htmlFor={`data-despesa-${viagemId}`}>Data *</label>
          <input
            id={`data-despesa-${viagemId}`}
            type="date"
            max={hoje()}
            value={dataDespesa}
            onChange={(evento) => setDataDespesa(evento.target.value)}
            disabled={enviando}
          />
          {errosCampo.dataDespesa && <span className="erro">{errosCampo.dataDespesa}</span>}
        </div>

        <div className="campo">
          <label htmlFor={`tipo-despesa-${viagemId}`}>Tipo *</label>
          <select
            id={`tipo-despesa-${viagemId}`}
            value={tipoDespesaId}
            onChange={(evento) => setTipoDespesaId(evento.target.value)}
            disabled={enviando}
          >
            <option value="">Selecione...</option>
            {tiposDespesa.map((tipo) => (
              <option key={tipo.id} value={tipo.id}>
                {tipo.nome}
              </option>
            ))}
          </select>
          {errosCampo.tipoDespesaId && <span className="erro">{errosCampo.tipoDespesaId}</span>}
        </div>

        <div className="campo">
          <label htmlFor={`descricao-despesa-${viagemId}`}>Descrição *</label>
          <input
            id={`descricao-despesa-${viagemId}`}
            type="text"
            maxLength={255}
            value={descricao}
            onChange={(evento) => setDescricao(evento.target.value)}
            placeholder="Ex.: Hotel Centro"
            disabled={enviando}
          />
          {errosCampo.descricao && <span className="erro">{errosCampo.descricao}</span>}
        </div>

        <div className="campo">
          <label htmlFor={`valor-despesa-${viagemId}`}>Valor (R$) *</label>
          <input
            id={`valor-despesa-${viagemId}`}
            type="number"
            min="0.01"
            step="0.01"
            value={valor}
            onChange={(evento) => setValor(evento.target.value)}
            placeholder="0,00"
            disabled={enviando}
          />
          {errosCampo.valor && <span className="erro">{errosCampo.valor}</span>}
        </div>

        <button type="submit" disabled={enviando}>
          {enviando ? 'Salvando...' : 'Registrar despesa'}
        </button>
      </form>

      {falha && <p className="aviso falha">{falha}</p>}
      {carregando && <p className="aviso">Carregando despesas...</p>}

      {!carregando && !falha && (
        <>
          {despesas.length === 0 && <p className="aviso">Nenhuma despesa lançada até o momento.</p>}
          {despesas.length > 0 && (
            <ul>
              {despesas.map((despesa) => (
                <li key={despesa.id}>
                  {formatarData(despesa.dataDespesa)} — <strong>{despesa.tipoDespesaNome}</strong>:{' '}
                  {despesa.descricao} ({formatarValor(despesa.valor)})
                </li>
              ))}
            </ul>
          )}
          <p className="resumo-financeiro">
            <strong>Total gasto: {formatarValor(valorTotal)}</strong>
          </p>

          {custos && (
            <div className="custos-viagem">
              <h4>Custos por categoria</h4>
              <ul>
                <li>Deslocamento: {formatarValor(custos.custoDeslocamento)}</li>
                <li>Hospedagem: {formatarValor(custos.custoHospedagem)}</li>
                <li>Táxi: {formatarValor(custos.custoTaxi)}</li>
              </ul>
              <p className="resumo-financeiro">
                <strong>Custo total: {formatarValor(custos.custoTotal)}</strong>
              </p>
            </div>
          )}
        </>
      )}
    </div>
  );
}
