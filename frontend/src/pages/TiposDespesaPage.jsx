import { useEffect, useState } from 'react';
import TipoDespesaPanel from '../components/TipoDespesaPanel.jsx';
import { listarTiposDespesa } from '../api.js';

export default function TiposDespesaPage() {
  const [tiposDespesa, setTiposDespesa] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);

  useEffect(() => {
    listarTiposDespesa()
      .then(setTiposDespesa)
      .catch((e) => setErro(e.message))
      .finally(() => setCarregando(false));
  }, []);

  return <TipoDespesaPanel tiposDespesa={tiposDespesa} carregando={carregando} erro={erro} />;
}
