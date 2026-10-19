import { useCallback, useEffect, useState } from 'react';
import CargoPanel from '../components/CargoPanel.jsx';
import { listarCargos } from '../api.js';

export default function CargosPage() {
  const [cargos, setCargos] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);

  const carregarCargos = useCallback(async () => {
    setCarregando(true);
    try {
      setCargos(await listarCargos());
      setErro(null);
    } catch (e) {
      setErro(e.message);
    } finally {
      setCarregando(false);
    }
  }, []);

  useEffect(() => {
    carregarCargos();
  }, [carregarCargos]);

  return (
    <CargoPanel cargos={cargos} carregando={carregando} erro={erro} aoCadastrar={carregarCargos} />
  );
}
