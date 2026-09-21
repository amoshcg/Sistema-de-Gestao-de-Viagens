import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import ViagemForm from '../components/ViagemForm.jsx';
import ViagemFiltros from '../components/ViagemFiltros.jsx';
import ViagemList from '../components/ViagemList.jsx';
import {
  listarViagens,
  listarEmpregados,
  listarMeiosTransporte,
  listarTiposDespesa,
  listarStatusViagem,
} from '../api.js';

export default function ViagensPage() {
  const [viagens, setViagens] = useState([]);
  const [carregandoViagens, setCarregandoViagens] = useState(true);
  const [erroViagens, setErroViagens] = useState(null);

  const [empregados, setEmpregados] = useState([]);
  const [meiosTransporte, setMeiosTransporte] = useState([]);
  const [tiposDespesa, setTiposDespesa] = useState([]);
  const [statusViagem, setStatusViagem] = useState([]);

  const [viagemEditando, setViagemEditando] = useState(null);
  const filtrosAtuais = useRef({});

  const gestores = useMemo(
    () => empregados.filter((empregado) => empregado.cargoNome === 'Gestor'),
    [empregados]
  );

  const carregarViagens = useCallback(async () => {
    setCarregandoViagens(true);
    try {
      setViagens(await listarViagens(filtrosAtuais.current));
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
    listarTiposDespesa().then(setTiposDespesa).catch(() => setTiposDespesa([]));
    listarStatusViagem().then(setStatusViagem).catch(() => setStatusViagem([]));
  }, [carregarViagens]);

  async function aoSalvarViagem() {
    setViagemEditando(null);
    await carregarViagens();
  }

  async function aoPesquisar(filtros) {
    filtrosAtuais.current = filtros;
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

      <ViagemFiltros statusViagem={statusViagem} aoPesquisar={aoPesquisar} />

      <ViagemList
        viagens={viagens}
        gestores={gestores}
        tiposDespesa={tiposDespesa}
        carregando={carregandoViagens}
        erro={erroViagens}
        aoAlterar={carregarViagens}
        onEditar={setViagemEditando}
      />
    </>
  );
}
