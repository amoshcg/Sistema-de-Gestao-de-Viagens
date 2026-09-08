import { NavLink } from 'react-router-dom';

const ITENS = [
  { rota: '/viagens', rotulo: 'Viagens' },
  { rota: '/empregados', rotulo: 'Empregados' },
  { rota: '/areas', rotulo: 'Áreas' },
  { rota: '/meios-transporte', rotulo: 'Meios de transporte' },
];

export default function NavMenu() {
  return (
    <nav className="menu">
      {ITENS.map((item) => (
        <NavLink
          key={item.rota}
          to={item.rota}
          className={({ isActive }) => `menu-item${isActive ? ' menu-item-ativo' : ''}`}
        >
          {item.rotulo}
        </NavLink>
      ))}
    </nav>
  );
}
