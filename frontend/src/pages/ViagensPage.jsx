import { useCallback, useEffect, useState } from 'react';
import ViagemForm from '../components/ViagemForm.jsx';
import ViagemList from '../components/ViagemList.jsx';
import { listarViagens, listarEmpregados, listarMeiosTransporte } from '../api.js';

export default function ViagensPage() {
  const [viagens, setViagens] = useState([]);
  const [carregandoViagens, setCarregandoViagens] = useState(true);
  const [erroViagens, setErroViagens] = useState(null);

  const [empregados, setEmpregados] = useState([]);
  const [meiosTransporte, setMeiosTransporte] = useState([]);

  const [viagemEditando, setViagemEditando] = useState(null);

  const carregarViagens = useCallback(async () => {
    setCarregandoViagens(true);
    try {
      setViagens(await listarViagens());
      setErroViagens(null);
    } catch (e) {
      setErroViagens(e.message);
    } finally {
      setCarregandoViagens(false);
    }
  }, []);

  useEffect(() => {
    carregarViagens();
    listarEmpregados().then(setEmpregados).catch(() => setEmpregados([]));
    listarMeiosTransporte().then(setMeiosTransporte).catch(() => setMeiosTransporte([]));
  }, [carregarViagens]);

  async function aoSalvarViagem() {
    setViagemEditando(null);
    await carregarViagens();
  }

  return (
    <>
      <ViagemForm
        empregados={empregados}
        meiosTransporte={meiosTransporte}
        viagemEditando={viagemEditando}
        aoSalvar={aoSalvarViagem}
        aoCancelarEdicao={() => setViagemEditando(null)}
      />

      <ViagemList
        viagens={viagens}
        carregando={carregandoViagens}
        erro={erroViagens}
        aoAlterar={carregarViagens}
        onEditar={setViagemEditando}
      />
    </>
  );
}
