import { useCallback, useEffect, useState } from 'react';
import ViagemForm from './components/ViagemForm.jsx';
import ViagemList from './components/ViagemList.jsx';
import { listarViagens } from './api.js';

export default function App() {
  const [viagens, setViagens] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);

  const carregar = useCallback(async () => {
    setCarregando(true);
    try {
      setViagens(await listarViagens());
      setErro(null);
    } catch (e) {
      setErro(e.message);
    } finally {
      setCarregando(false);
    }
  }, []);

  useEffect(() => {
    carregar();
  }, [carregar]);

  return (
    <div className="pagina">
      <header className="cabecalho">
        <h1>Sistema de Gestão de Viagens</h1>
        <p>Módulo de Planejamento de Viagens</p>
      </header>

      <main className="conteudo">
        <ViagemForm aoCadastrar={carregar} />
        <ViagemList viagens={viagens} carregando={carregando} erro={erro} />
      </main>
    </div>
  );
}
