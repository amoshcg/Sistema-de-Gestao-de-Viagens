import { useEffect, useState } from 'react';
import { buscarIndicadoresDashboard } from '../api.js';

function formatarValor(valor) {
  return Number(valor ?? 0).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}

export default function DashboardPage() {
  const [indicadores, setIndicadores] = useState(null);
  const [carregando, setCarregando] = useState(true);
  const [falha, setFalha] = useState(null);

  useEffect(() => {
    async function carregar() {
      setCarregando(true);
      try {
        setIndicadores(await buscarIndicadoresDashboard());
        setFalha(null);
      } catch (e) {
        setFalha(e.message);
      } finally {
        setCarregando(false);
      }
    }
    carregar();
  }, []);

  if (carregando) {
    return <p className="aviso">Carregando indicadores...</p>;
  }

  if (falha) {
    return <p className="aviso falha">{falha}</p>;
  }

  return (
    <section className="cartao">
      <h2>Dashboard</h2>
      <div className="grade-indicadores">
        <article className="indicador">
          <span className="indicador-rotulo">Viagens cadastradas</span>
          <span className="indicador-valor">{indicadores.totalViagens}</span>
        </article>

        <article className="indicador indicador-aprovada">
          <span className="indicador-rotulo">Viagens aprovadas</span>
          <span className="indicador-valor">{indicadores.viagensAprovadas}</span>
        </article>

        <article className="indicador indicador-rejeitada">
          <span className="indicador-rotulo">Viagens rejeitadas</span>
          <span className="indicador-valor">{indicadores.viagensRejeitadas}</span>
        </article>

        <article className="indicador">
          <span className="indicador-rotulo">Valor total gasto</span>
          <span className="indicador-valor">{formatarValor(indicadores.valorTotalGasto)}</span>
        </article>

        <article className="indicador">
          <span className="indicador-rotulo">Custo médio por viagem</span>
          <span className="indicador-valor">{formatarValor(indicadores.custoMedioPorViagem)}</span>
        </article>

        <article className="indicador">
          <span className="indicador-rotulo">Destino mais visitado</span>
          <span className="indicador-valor indicador-valor-texto">
            {indicadores.destinoMaisVisitado ?? '—'}
          </span>
        </article>
      </div>
    </section>
  );
}
