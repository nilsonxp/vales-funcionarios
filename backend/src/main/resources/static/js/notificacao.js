// Conexão WebSocket
let stompClient = null;
let conectado = false;
let tentativasReconexao = 0;
const MAX_TENTATIVAS = 5;

// Conectar ao WebSocket
function conectarWebSocket(token) {
    if (conectado) return;

    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    // Adicionar o token JWT ao handshake
    const headers = {
        'Authorization': 'Bearer ' + token
    };

    stompClient.connect(headers, function(frame) {
        console.log('Conectado ao WebSocket: ' + frame);
        conectado = true;
        tentativasReconexao = 0;

        // Inscrição para notificações específicas do usuário
        stompClient.subscribe('/user/queue/notificacoes', function(mensagem) {
            processarNotificacao(JSON.parse(mensagem.body));
        });

        // Inscrição para status de conexão
        stompClient.subscribe('/user/queue/status', function(mensagem) {
            console.log('Status: ' + mensagem.body);
        });

        // Enviar mensagem de teste para confirmar a conexão
        stompClient.send("/app/conectar", {}, JSON.stringify({}));

    }, function(error) {
        console.error('Erro na conexão WebSocket:', error);
        conectado = false;
        tentarReconexao(token);
    });
}

// Tentar reconexão com backoff exponencial
function tentarReconexao(token) {
    if (tentativasReconexao >= MAX_TENTATIVAS) {
        console.error('Número máximo de tentativas de reconexão atingido.');
        return;
    }

    const tempoEspera = Math.pow(2, tentativasReconexao) * 1000; // Backoff exponencial
    console.log(`Tentando reconectar em ${tempoEspera/1000} segundos...`);

    setTimeout(() => {
        tentativasReconexao++;
        conectarWebSocket(token);
    }, tempoEspera);
}

// Desconectar do WebSocket
function desconectarWebSocket() {
    if (stompClient !== null) {
        stompClient.disconnect();
        conectado = false;
        console.log('Desconectado do WebSocket');
    }
}

// Processar notificações recebidas
function processarNotificacao(mensagem) {
    console.log('Nova mensagem recebida:', mensagem);

    switch(mensagem.tipo) {
        case 'NOTIFICACAO':
            exibirNotificacao(mensagem.conteudo);
            atualizarContadorNotificacoes(1);
            break;

        case 'NOTIFICACAO_LIDA':
            atualizarContadorNotificacoes(-1);
            removerNotificacaoDaLista(mensagem.conteudo.id);
            break;

        case 'TODAS_NOTIFICACOES_LIDAS':
            atualizarContadorNotificacoes(0, true);
            limparListaNotificacoes();
            break;

        default:
            console.log('Tipo de mensagem desconhecido:', mensagem.tipo);
    }
}

// Exibir notificação na interface
function exibirNotificacao(notificacao) {
    // Adicionar a notificação à lista na interface
    const listaNotificacoes = document.getElementById('lista-notificacoes');
    if (listaNotificacoes) {
        const item = document.createElement('div');
        item.className = 'notificacao-item';
        item.dataset.id = notificacao.id;

        item.innerHTML = `
            <div class="notificacao-conteudo">
                <p>${notificacao.mensagem}</p>
                <small>${new Date(notificacao.dataCriacao).toLocaleString()}</small>
            </div>
            <button class="btn-marcar-lida" onclick="marcarComoLida(${notificacao.id})">Marcar como lida</button>
        `;

        listaNotificacoes.prepend(item);
    }

    // Exibir notificação temporária
    exibirNotificacaoTemporaria(notificacao.mensagem);
}

// Exibe uma notificação temporária tipo "toast"
function exibirNotificacaoTemporaria(mensagem) {
    const toast = document.createElement('div');
    toast.className = 'toast-notificacao';
    toast.innerHTML = `<p>${mensagem}</p>`;

    document.body.appendChild(toast);

    // Mostrar com animação
    setTimeout(() => {
        toast.classList.add('visivel');
    }, 10);

    // Ocultar após alguns segundos
    setTimeout(() => {
        toast.classList.remove('visivel');
        setTimeout(() => {
            document.body.removeChild(toast);
        }, 300);
    }, 5000);
}

// Atualizar contador de notificações não lidas
function atualizarContadorNotificacoes(delta, resetar = false) {
    const contador = document.getElementById('contador-notificacoes');
    if (contador) {
        if (resetar) {
            contador.textContent = '0';
        } else {
            let valor = parseInt(contador.textContent || '0');
            valor += delta;
            contador.textContent = Math.max(0, valor).toString();
        }
    }
}

// Remover notificação da lista quando marcada como lida
function removerNotificacaoDaLista(id) {
    const item = document.querySelector(`.notificacao-item[data-id="${id}"]`);
    if (item) {
        item.remove();
    }
}

// Limpar toda a lista de notificações
function limparListaNotificacoes() {
    const lista = document.getElementById('lista-notificacoes');
    if (lista) {
        lista.innerHTML = '';
    }
}

// Marcar notificação como lida (para ser chamado a partir da interface)
function marcarComoLida(id) {
    fetch(`/api/notificacoes/${id}/ler`, {
        method: 'PATCH',
        headers: {
            'Authorization': 'Bearer ' + getToken(),
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Erro ao marcar notificação como lida');
            }
            return response.json();
        })
        .catch(error => {
            console.error('Erro:', error);
        });
}

// Marcar todas notificações como lidas
function marcarTodasComoLidas() {
    fetch('/api/notificacoes/ler-todas', {
        method: 'PATCH',
        headers: {
            'Authorization': 'Bearer ' + getToken(),
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Erro ao marcar todas notificações como lidas');
            }
            return response.json();
        })
        .catch(error => {
            console.error('Erro:', error);
        });
}

// Obter token JWT do armazenamento local
function getToken() {
    return localStorage.getItem('token') || '';
}

// Inicializar quando o documento estiver pronto
document.addEventListener('DOMContentLoaded', function() {
    const token = getToken();
    if (token) {
        conectarWebSocket(token);
        carregarNotificacoesNaoLidas();
    }
});

// Carregar notificações não lidas do servidor
function carregarNotificacoesNaoLidas() {
    fetch('/api/notificacoes/nao-lidas', {
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + getToken(),
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Erro ao carregar notificações');
            }
            return response.json();
        })
        .then(notificacoes => {
            const lista = document.getElementById('lista-notificacoes');
            if (lista) {
                lista.innerHTML = '';
                notificacoes.forEach(notificacao => {
                    const item = document.createElement('div');
                    item.className = 'notificacao-item';
                    item.dataset.id = notificacao.id;

                    item.innerHTML = `
                    <div class="notificacao-conteudo">
                        <p>${notificacao.mensagem}</p>
                        <small>${new Date(notificacao.dataCriacao).toLocaleString()}</small>
                    </div>
                    <button class="btn-marcar-lida" onclick="marcarComoLida(${notificacao.id})">Marcar como lida</button>
                `;

                    lista.appendChild(item);
                });

                // Atualizar contador
                atualizarContadorNotificacoes(notificacoes.length, true);
            }
        })
        .catch(error => {
            console.error('Erro:', error);
        });
}