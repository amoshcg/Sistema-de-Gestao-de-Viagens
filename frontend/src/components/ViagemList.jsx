import { Fragment, useState } from 'react';
import AcaoGestorForm from './AcaoGestorForm.jsx';
import DespesasPanel from './DespesasPanel.jsx';
import {
  excluirViagem,
  submeterViagem,
  cancelarViagem,
  aprovarViagem,
  rejeitarViagem,
  solicitarAjusteViagem,
  buscarHistoricoViagem,
} from '../api.js';

/** Converte "2026-09-10" para "10/09/2026" sem depender de fuso horário. */
function formatarData(iso) {
  const [ano, mes, dia] = iso.split('-');
  return `${dia}/${mes}/${ano}`;
}

function formatarDataHora(iso) {
  const data = new Date(iso);
  return data.toLocaleString('pt-BR');
}

function formatarValor(valor) {
  return Number(valor ?? 0).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}

/** Transforma a descrição do status (ex.: "Ajuste solicitado") num nome de classe CSS válido. */
function classeSituacao(descricao) {
  const semAcentos = descricao.normalize('NFD').replace(/[̀-ͯ]/g, '');
  return semAcentos
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, '-')
    .replace(/(^-|-$)/g, '');
}

export default function ViagemList({ viagens, gestores, tiposDespesa, carregando, erro, aoAlterar, onEditar }) {
  const [processando, setProcessando] = useState(null);
  const [falha, setFalha] = useState(null);
  const [historicoAberto, setHistoricoAberto] = useState(null);
  const [historico, setHistorico] = useState([]);
  const [carregandoHistorico, setCarregandoHistorico] = useState(false);
  const [despesasAbertas, setDespesasAbertas] = useState(null);

  async function executar(viagem, acao) {
    setFalha(null);
    setProcessando(viagem.id);
    try {
      await acao();
      aoAlterar();
    } catch (e) {
      setFalha(e.message);
    } finally {
      setProcessando(null);
    }
  }

  async function excluir(viagem) {
    if (!window.confirm(`Excluir definitivamente a viagem nº ${viagem.numero} para ${viagem.destino}?`)) {
      return;
    }
    await executar(viagem, () => excluirViagem(viagem.id));
  }

  async function submeter(viagem) {
    await executar(viagem, () => submeterViagem(viagem.id));
  }

  async function cancelar(viagem) {
    if (!window.confirm(`Cancelar a viagem nº ${viagem.numero} para ${viagem.destino}?`)) {
      return;
    }
    await executar(viagem, () => cancelarViagem(viagem.id));
  }

  function alternarDespesas(viagem) {
    setDespesasAbertas((atual) => (atual === viagem.id ? null : viagem.id));
  }

  async function alternarHistorico(viagem) {
    if (historicoAberto === viagem.id) {
      setHistoricoAberto(null);
      return;
    }
    setHistoricoAberto(viagem.id);
    setCarregandoHistorico(true);
    try {
      setHistorico(await buscarHistoricoViagem(viagem.id));
    } catch (e) {
      setFalha(e.message);
    } finally {
      setCarregandoHistorico(false);
    }
  }

  return (
    <section className="cartao">
      <h2>Viagens cadastradas</h2>

      {carregando && <p className="aviso">Carregando viagens...</p>}
      {erro && <p className="aviso falha">{erro}</p>}
      {falha && <p className="aviso falha">{falha}</p>}

      {!carregando && !erro && viagens.length === 0 && (
        <p className="aviso">Nenhuma viagem cadastrada até o momento.</p>
      )}

      {!carregando && !erro && viagens.length > 0 && (
        <div className="tabela-rolagem">
          <table>
            <thead>
              <tr>
                <th>Nº</th>
                <th>Destino</th>
                <th>Período</th>
                <th>Motivo</th>
                <th>Transporte</th>
                <th>Empregado</th>
                <th>Situação</th>
                <th>Total gasto</th>
                <th>Ações</th>
              </tr>
            </thead>
            <tbody>
              {viagens.map((viagem) => {
                const editavel = viagem.situacaoDescricao === 'Rascunho' || viagem.situacaoDescricao === 'Ajuste solicitado';
                const solicitada = viagem.situacaoDescricao === 'Solicitada';
                const aprovada = viagem.situacaoDescricao === 'Aprovada';
                const ocupado = processando === viagem.id;
                return (
                  <Fragment key={viagem.id}>
                    <tr>
                      <td className="nao-quebra">{viagem.numero}</td>
                      <td>{viagem.destino}</td>
                      <td className="nao-quebra">
                        {formatarData(viagem.dataSaida)} a {formatarData(viagem.dataRetorno)}
                      </td>
                      <td>{viagem.motivo}</td>
                      <td>{viagem.meioTransporteDescricao}</td>
                      <td>
                        {viagem.empregadoNome}
                        <br />
                        <small>{viagem.empregadoMatricula} — {viagem.empregadoAreaNome}</small>
                      </td>
                      <td>
                        <span className={`situacao situacao-${classeSituacao(viagem.situacaoDescricao)}`}>
                          {viagem.situacaoDescricao}
                        </span>
                      </td>
                      <td className="nao-quebra">
                        {viagem.valorGasto == null ? '—' : formatarValor(viagem.valorGasto)}
                      </td>
                      <td className="nao-quebra">
                        {editavel && (
                          <div className="acoes-tabela">
                            <button type="button" onClick={() => onEditar(viagem)} disabled={ocupado}>
                              Editar
                            </button>
                            <button
                              type="button"
                              className="botao-secundario"
                              onClick={() => submeter(viagem)}
                              disabled={ocupado}
                            >
                              {viagem.situacaoDescricao === 'Ajuste solicitado' ? 'Reenviar' : 'Submeter'}
                            </button>
                            <button
                              type="button"
                              className="botao-perigo"
                              onClick={() => cancelar(viagem)}
                              disabled={ocupado}
                            >
                              Cancelar
                            </button>
                            {viagem.situacaoDescricao === 'Rascunho' && (
                              <button
                                type="button"
                                className="botao-perigo"
                                onClick={() => excluir(viagem)}
                                disabled={ocupado}
                              >
                                Excluir
                              </button>
                            )}
                          </div>
                        )}
                        {!editavel && !solicitada && !aprovada && <span className="aviso">—</span>}
                        {aprovada && (
                          <button
                            type="button"
                            className="botao-secundario"
                            onClick={() => alternarDespesas(viagem)}
                          >
                            Despesas
                          </button>
                        )}
                        <button
                          type="button"
                          className="botao-secundario"
                          onClick={() => alternarHistorico(viagem)}
                        >
                          Histórico
                        </button>
                      </td>
                    </tr>
                    {solicitada && (
                      <tr>
                        <td colSpan={9}>
                          <AcaoGestorForm
                            gestores={gestores}
                            ocupado={ocupado}
                            aoAprovar={(gestorId) =>
                              executar(viagem, () => aprovarViagem(viagem.id, { gestorId }))
                            }
                            aoRejeitar={(gestorId, justificativa) =>
                              executar(viagem, () => rejeitarViagem(viagem.id, { gestorId, justificativa }))
                            }
                            aoAjustar={(gestorId, justificativa) =>
                              executar(viagem, () => solicitarAjusteViagem(viagem.id, { gestorId, justificativa }))
                            }
                          />
                        </td>
                      </tr>
                    )}
                    {despesasAbertas === viagem.id && (
                      <tr>
                        <td colSpan={9}>
                          <DespesasPanel viagemId={viagem.id} tiposDespesa={tiposDespesa} />
                        </td>
                      </tr>
                    )}
                    {historicoAberto === viagem.id && (
                      <tr>
                        <td colSpan={9}>
                          <div className="historico-viagem">
                            {carregandoHistorico && <p className="aviso">Carregando histórico...</p>}
                            {!carregandoHistorico && historico.length === 0 && (
                              <p className="aviso">Sem histórico registrado.</p>
                            )}
                            {!carregandoHistorico && historico.length > 0 && (
                              <ul>
                                {historico.map((item) => (
                                  <li key={item.id}>
                                    <strong>{item.situacaoDescricao}</strong> em {formatarDataHora(item.dataMudanca)}
                                    {' '}por {item.responsavelNome}
                                    {item.justificativa && <> — "{item.justificativa}"</>}
                                  </li>
                                ))}
                              </ul>
                            )}
                          </div>
                        </td>
                      </tr>
                    )}
                  </Fragment>
                );
              })}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
}
