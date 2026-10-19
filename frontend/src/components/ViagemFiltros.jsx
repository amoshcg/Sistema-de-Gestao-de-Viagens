import { useState } from 'react';

const FILTROS_VAZIOS = { destino: '', dataInicio: '', dataFim: '', situacao: '' };

export default function ViagemFiltros({ destinos, statusViagem, aoPesquisar }) {
  const [filtros, setFiltros] = useState(FILTROS_VAZIOS);

  function atualizarCampo(campo, valor) {
    setFiltros((atual) => ({ ...atual, [campo]: valor }));
  }

  function pesquisar(evento) {
    evento.preventDefault();
    aoPesquisar(filtros);
  }

  function limpar() {
    setFiltros(FILTROS_VAZIOS);
    aoPesquisar(FILTROS_VAZIOS);
  }

  return (
    <section className="cartao">
      <h2>Pesquisar viagens</h2>
      <form onSubmit={pesquisar} noValidate className="linha linha-filtros">
        <div className="campo">
          <label htmlFor="filtro-destino">Destino</label>
          <select
            id="filtro-destino"
            value={filtros.destino}
            onChange={(evento) => atualizarCampo('destino', evento.target.value)}
          >
            <option value="">Todos</option>
            {destinos.map((destino) => (
              <option key={destino} value={destino}>
                {destino}
              </option>
            ))}
          </select>
        </div>

        <div className="campo">
          <label htmlFor="filtro-data-inicio">Período — de</label>
          <input
            id="filtro-data-inicio"
            type="date"
            value={filtros.dataInicio}
            onChange={(evento) => atualizarCampo('dataInicio', evento.target.value)}
          />
        </div>

        <div className="campo">
          <label htmlFor="filtro-data-fim">Período — até</label>
          <input
            id="filtro-data-fim"
            type="date"
            value={filtros.dataFim}
            onChange={(evento) => atualizarCampo('dataFim', evento.target.value)}
          />
        </div>

        <div className="campo">
          <label htmlFor="filtro-situacao">Situação</label>
          <select
            id="filtro-situacao"
            value={filtros.situacao}
            onChange={(evento) => atualizarCampo('situacao', evento.target.value)}
          >
            <option value="">Todas</option>
            {statusViagem.map((status) => (
              <option key={status.id} value={status.descricao}>
                {status.descricao}
              </option>
            ))}
          </select>
        </div>

        <div className="acoes-form">
          <button type="submit">Pesquisar</button>
          <button type="button" className="botao-secundario" onClick={limpar}>
            Limpar
          </button>
        </div>
      </form>
    </section>
  );
}
