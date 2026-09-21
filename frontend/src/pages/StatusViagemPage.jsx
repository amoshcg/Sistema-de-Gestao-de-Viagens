import { useCallback, useEffect, useState } from 'react';
import StatusViagemPanel from '../components/StatusViagemPanel.jsx';
import { listarStatusViagem } from '../api.js';

export default function StatusViagemPage() {
  const [statusViagem, setStatusViagem] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);

  const carregarStatusViagem = useCallback(async () => {
    setCarregando(true);
    try {
      setStatusViagem(await listarStatusViagem());
      setErro(null);
    } catch (e) {
      setErro(e.message);
    } finally {
      setCarregando(false);
    }
  }, []);

  useEffect(() => {
    carregarStatusViagem();
  }, [carregarStatusViagem]);

  return (
    <StatusViagemPanel
      statusViagem={statusViagem}
      carregando={carregando}
      erro={erro}
      aoCadastrar={carregarStatusViagem}
    />
  );
}
