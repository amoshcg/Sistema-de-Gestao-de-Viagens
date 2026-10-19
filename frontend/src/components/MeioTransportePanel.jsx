export default function MeioTransportePanel({ meiosTransporte, carregando, erro }) {
  return (
    <section className="cartao">
      <h2>Meios de transporte</h2>
      <p className="aviso">
        Lista de opções pré-definidas do sistema, usada no cadastro de viagens.
      </p>

      {carregando && <p className="aviso">Carregando meios de transporte...</p>}
      {erro && <p className="aviso falha">{erro}</p>}

      {!carregando && !erro && (
        <ul className="lista-empregados">
          {meiosTransporte.map((meio) => (
            <li key={meio.id}>{meio.descricao}</li>
          ))}
          {meiosTransporte.length === 0 && <li className="aviso">Nenhum meio de transporte cadastrado.</li>}
        </ul>
      )}
    </section>
  );
}
