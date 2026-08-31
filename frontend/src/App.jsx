import { Navigate, Route, Routes } from 'react-router-dom';
import NavMenu from './components/NavMenu.jsx';
import ViagensPage from './pages/ViagensPage.jsx';
import EmpregadosPage from './pages/EmpregadosPage.jsx';
import AreasPage from './pages/AreasPage.jsx';
import MeiosTransportePage from './pages/MeiosTransportePage.jsx';

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
          <Route path="/meios-transporte" element={<MeiosTransportePage />} />
          <Route path="*" element={<Navigate to="/viagens" replace />} />
        </Routes>
      </main>
    </div>
  );
}
