/** Converte "2026-09-10" para "10/09/2026" sem depender de fuso horário. */
function formatarData(iso) {
  const [ano, mes, dia] = iso.split('-');
  return `${dia}/${mes}/${ano}`;
}

export default function ViagemList({ viagens, carregando, erro }) {
  return (
    <section className="cartao">
      <h2>Viagens cadastradas</h2>

      {carregando && <p className="aviso">Carregando viagens...</p>}
      {erro && <p className="aviso falha">{erro}</p>}

      {!carregando && !erro && viagens.length === 0 && (
        <p className="aviso">Nenhuma viagem cadastrada até o momento.</p>
      )}

      {!carregando && !erro && viagens.length > 0 && (
        <div className="tabela-rolagem">
          <table>
            <thead>
              <tr>
                <th>Destino</th>
                <th>Período</th>
                <th>Motivo</th>
                <th>Transporte</th>
                <th>Responsável</th>
                <th>Situação</th>
              </tr>
            </thead>
            <tbody>
              {viagens.map((viagem) => (
                <tr key={viagem.id}>
                  <td>{viagem.destino}</td>
                  <td className="nao-quebra">
                    {formatarData(viagem.dataSaida)} a {formatarData(viagem.dataRetorno)}
                  </td>
                  <td>{viagem.motivo}</td>
                  <td>{viagem.meioTransporteDescricao}</td>
                  <td>{viagem.responsavel}</td>
                  <td>
                    <span className={`situacao situacao-${viagem.situacao.toLowerCase()}`}>
                      {viagem.situacaoDescricao}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
}
