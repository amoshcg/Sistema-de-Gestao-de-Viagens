const BASE_URL = '/api';

async function tratarResposta(resposta, mensagemPadrao) {
  const corpo = await resposta.json().catch(() => null);
  if (!resposta.ok) {
    const erro = new Error(corpo?.mensagem ?? mensagemPadrao);
    erro.erros = corpo?.erros ?? {};
    throw erro;
  }
  return corpo;
}

export async function listarViagens() {
  const resposta = await fetch(`${BASE_URL}/viagens`);
  if (!resposta.ok) {
    throw new Error('Não foi possível carregar as viagens.');
  }
  return resposta.json();
}

export async function cadastrarViagem(viagem) {
  const resposta = await fetch(`${BASE_URL}/viagens`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(viagem),
  });
  return tratarResposta(resposta, 'Não foi possível cadastrar a viagem.');
}

export async function alterarViagem(id, viagem) {
  const resposta = await fetch(`${BASE_URL}/viagens/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(viagem),
  });
  return tratarResposta(resposta, 'Não foi possível alterar a viagem.');
}

export async function excluirViagem(id) {
  const resposta = await fetch(`${BASE_URL}/viagens/${id}`, { method: 'DELETE' });
  if (!resposta.ok) {
    const corpo = await resposta.json().catch(() => null);
    throw new Error(corpo?.mensagem ?? 'Não foi possível excluir a viagem.');
  }
}

export async function submeterViagem(id) {
  const resposta = await fetch(`${BASE_URL}/viagens/${id}/submissao`, { method: 'POST' });
  return tratarResposta(resposta, 'Não foi possível submeter a viagem para análise.');
}

export async function cancelarViagem(id) {
  const resposta = await fetch(`${BASE_URL}/viagens/${id}/cancelamento`, { method: 'POST' });
  return tratarResposta(resposta, 'Não foi possível cancelar a viagem.');
}

export async function aprovarViagem(id, dados) {
  const resposta = await fetch(`${BASE_URL}/viagens/${id}/aprovacao`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(dados),
  });
  return tratarResposta(resposta, 'Não foi possível aprovar a viagem.');
}

export async function rejeitarViagem(id, dados) {
  const resposta = await fetch(`${BASE_URL}/viagens/${id}/rejeicao`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(dados),
  });
  return tratarResposta(resposta, 'Não foi possível rejeitar a viagem.');
}

export async function solicitarAjusteViagem(id, dados) {
  const resposta = await fetch(`${BASE_URL}/viagens/${id}/ajuste`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(dados),
  });
  return tratarResposta(resposta, 'Não foi possível solicitar o ajuste da viagem.');
}

export async function buscarHistoricoViagem(id) {
  const resposta = await fetch(`${BASE_URL}/viagens/${id}/historico`);
  if (!resposta.ok) {
    throw new Error('Não foi possível carregar o histórico da viagem.');
  }
  return resposta.json();
}

export async function listarEmpregados() {
  const resposta = await fetch(`${BASE_URL}/empregados`);
  if (!resposta.ok) {
    throw new Error('Não foi possível carregar os empregados.');
  }
  return resposta.json();
}

export async function cadastrarEmpregado(empregado) {
  const resposta = await fetch(`${BASE_URL}/empregados`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(empregado),
  });
  return tratarResposta(resposta, 'Não foi possível cadastrar o empregado.');
}

export async function alterarEmpregado(id, empregado) {
  const resposta = await fetch(`${BASE_URL}/empregados/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(empregado),
  });
  return tratarResposta(resposta, 'Não foi possível alterar o empregado.');
}

export async function listarAreas() {
  const resposta = await fetch(`${BASE_URL}/areas`);
  if (!resposta.ok) {
    throw new Error('Não foi possível carregar as áreas.');
  }
  return resposta.json();
}

export async function cadastrarArea(area) {
  const resposta = await fetch(`${BASE_URL}/areas`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(area),
  });
  return tratarResposta(resposta, 'Não foi possível cadastrar a área.');
}

export async function listarMeiosTransporte() {
  const resposta = await fetch(`${BASE_URL}/meios-transporte`);
  if (!resposta.ok) {
    throw new Error('Não foi possível carregar os meios de transporte.');
  }
  return resposta.json();
}

export async function listarCargos() {
  const resposta = await fetch(`${BASE_URL}/cargos`);
  if (!resposta.ok) {
    throw new Error('Não foi possível carregar os cargos.');
  }
  return resposta.json();
}

export async function cadastrarCargo(cargo) {
  const resposta = await fetch(`${BASE_URL}/cargos`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(cargo),
  });
  return tratarResposta(resposta, 'Não foi possível cadastrar o cargo.');
}

export async function listarStatusViagem() {
  const resposta = await fetch(`${BASE_URL}/status-viagem`);
  if (!resposta.ok) {
    throw new Error('Não foi possível carregar os status de viagem.');
  }
  return resposta.json();
}

export async function cadastrarStatusViagem(status) {
  const resposta = await fetch(`${BASE_URL}/status-viagem`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(status),
  });
  return tratarResposta(resposta, 'Não foi possível cadastrar o status de viagem.');
}
