import { useCallback, useEffect, useState } from 'react';
import AreaPanel from '../components/AreaPanel.jsx';
import { listarAreas } from '../api.js';

export default function AreasPage() {
  const [areas, setAreas] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);

  const carregarAreas = useCallback(async () => {
    setCarregando(true);
    try {
      setAreas(await listarAreas());
      setErro(null);
    } catch (e) {
      setErro(e.message);
    } finally {
      setCarregando(false);
    }
  }, []);

  useEffect(() => {
    carregarAreas();
  }, [carregarAreas]);

  return (
    <AreaPanel areas={areas} carregando={carregando} erro={erro} aoCadastrar={carregarAreas} />
  );
}
