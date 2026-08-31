import { useEffect, useState } from 'react';
import MeioTransportePanel from '../components/MeioTransportePanel.jsx';
import { listarMeiosTransporte } from '../api.js';

export default function MeiosTransportePage() {
  const [meiosTransporte, setMeiosTransporte] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);

  useEffect(() => {
    listarMeiosTransporte()
      .then(setMeiosTransporte)
      .catch((e) => setErro(e.message))
      .finally(() => setCarregando(false));
  }, []);

  return <MeioTransportePanel meiosTransporte={meiosTransporte} carregando={carregando} erro={erro} />;
}
