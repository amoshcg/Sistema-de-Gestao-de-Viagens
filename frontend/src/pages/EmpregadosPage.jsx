import { useCallback, useEffect, useState } from 'react';
import EmpregadoPanel from '../components/EmpregadoPanel.jsx';
import { listarEmpregados, listarAreas } from '../api.js';

export default function EmpregadosPage() {
  const [empregados, setEmpregados] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);

  const [areas, setAreas] = useState([]);

  const carregarEmpregados = useCallback(async () => {
    setCarregando(true);
    try {
      setEmpregados(await listarEmpregados());
      setErro(null);
    } catch (e) {
      setErro(e.message);
    } finally {
      setCarregando(false);
    }
  }, []);

  useEffect(() => {
    carregarEmpregados();
    listarAreas().then(setAreas).catch(() => setAreas([]));
  }, [carregarEmpregados]);

  return (
    <EmpregadoPanel
      empregados={empregados}
      areas={areas}
      carregando={carregando}
      erro={erro}
      aoCadastrar={carregarEmpregados}
    />
  );
}
