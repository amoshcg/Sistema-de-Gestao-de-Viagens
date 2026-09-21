export default function TipoDespesaPanel({ tiposDespesa, carregando, erro }) {
  return (
    <section className="cartao">
      <h2>Tipos de despesa</h2>
      <p className="aviso">
        Lista de opções pré-definidas do sistema, usada no registro de despesas de viagens aprovadas.
      </p>

      {carregando && <p className="aviso">Carregando tipos de despesa...</p>}
      {erro && <p className="aviso falha">{erro}</p>}

      {!carregando && !erro && (
        <ul className="lista-empregados">
          {tiposDespesa.map((tipo) => (
            <li key={tipo.id}>{tipo.nome}</li>
          ))}
          {tiposDespesa.length === 0 && <li className="aviso">Nenhum tipo de despesa cadastrado.</li>}
        </ul>
      )}
    </section>
  );
}
