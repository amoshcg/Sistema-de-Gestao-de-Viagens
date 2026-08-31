import { useState } from 'react';
import { excluirViagem, submeterViagem } from '../api.js';

/** Converte "2026-09-10" para "10/09/2026" sem depender de fuso horário. */
function formatarData(iso) {
  const [ano, mes, dia] = iso.split('-');
  return `${dia}/${mes}/${ano}`;
}

export default function ViagemList({ viagens, carregando, erro, aoAlterar, onEditar }) {
  const [processando, setProcessando] = useState(null);
  const [falha, setFalha] = useState(null);

  async function excluir(viagem) {
    if (!window.confirm(`Excluir definitivamente a viagem nº ${viagem.numero} para ${viagem.destino}?`)) {
      return;
    }
    setFalha(null);
    setProcessando(viagem.id);
    try {
      await excluirViagem(viagem.id);
      aoAlterar();
    } catch (e) {
      setFalha(e.message);
    } finally {
      setProcessando(null);
    }
  }

  async function submeter(viagem) {
    setFalha(null);
    setProcessando(viagem.id);
    try {
      await submeterViagem(viagem.id);
      aoAlterar();
    } catch (e) {
      setFalha(e.message);
    } finally {
      setProcessando(null);
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
                <th>Ações</th>
              </tr>
            </thead>
            <tbody>
              {viagens.map((viagem) => {
                const podeAlterar = viagem.situacao === 'RASCUNHO';
                const ocupado = processando === viagem.id;
                return (
                  <tr key={viagem.id}>
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
                      <span className={`situacao situacao-${viagem.situacao.toLowerCase()}`}>
                        {viagem.situacaoDescricao}
                      </span>
                    </td>
                    <td className="nao-quebra">
                      {podeAlterar ? (
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
                            Submeter
                          </button>
                          <button
                            type="button"
                            className="botao-perigo"
                            onClick={() => excluir(viagem)}
                            disabled={ocupado}
                          >
                            Excluir
                          </button>
                        </div>
                      ) : (
                        <span className="aviso">—</span>
                      )}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
}
