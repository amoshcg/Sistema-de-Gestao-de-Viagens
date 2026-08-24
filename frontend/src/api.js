const BASE_URL = '/api';

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

  const corpo = await resposta.json().catch(() => null);

  if (!resposta.ok) {
    const erro = new Error(corpo?.mensagem ?? 'Não foi possível cadastrar a viagem.');
    erro.erros = corpo?.erros ?? {};
    throw erro;
  }

  return corpo;
}
