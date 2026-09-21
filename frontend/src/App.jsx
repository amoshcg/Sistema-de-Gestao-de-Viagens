import { Navigate, Route, Routes } from 'react-router-dom';
import NavMenu from './components/NavMenu.jsx';
import ViagensPage from './pages/ViagensPage.jsx';
import EmpregadosPage from './pages/EmpregadosPage.jsx';
import AreasPage from './pages/AreasPage.jsx';
import CargosPage from './pages/CargosPage.jsx';
import MeiosTransportePage from './pages/MeiosTransportePage.jsx';
import StatusViagemPage from './pages/StatusViagemPage.jsx';
import TiposDespesaPage from './pages/TiposDespesaPage.jsx';

export default function App() {
  return (
    <div className="pagina">
      <header className="cabecalho">
        <h1>Sistema de Gestão de Viagens</h1>
        <p>Módulo de Planejamento de Viagens</p>
        <NavMenu />
      </header>

      <main className="conteudo">
        <Routes>
          <Route path="/" element={<Navigate to="/viagens" replace />} />
          <Route path="/viagens" element={<ViagensPage />} />
          <Route path="/empregados" element={<EmpregadosPage />} />
          <Route path="/areas" element={<AreasPage />} />
          <Route path="/cargos" element={<CargosPage />} />
          <Route path="/meios-transporte" element={<MeiosTransportePage />} />
          <Route path="/status-viagem" element={<StatusViagemPage />} />
          <Route path="/tipos-despesa" element={<TiposDespesaPage />} />
          <Route path="*" element={<Navigate to="/viagens" replace />} />
        </Routes>
      </main>
    </div>
  );
}
