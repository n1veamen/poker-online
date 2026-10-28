const appState = {
  currentUser: '',
  pendingNickname: '',
  connected: false,
  connecting: false,
  tableState: null,
  privateCards: [],
  messages: []
};

const socket = new SockJS('/ws');
const stompClient = Stomp.over(socket);

const joinForm = document.getElementById('joinForm');
const nicknameInput = document.getElementById('nicknameInput');
const chatForm = document.getElementById('chatForm');
const chatInput = document.getElementById('chatInput');
const chatMessages = document.getElementById('chatMessages');
const boardCards = document.getElementById('boardCards');
const streetLabel = document.getElementById('streetLabel');
const potValue = document.getElementById('potValue');
const privateCards = document.getElementById('privateCards');
const startHandBtn = document.getElementById('startHandBtn');
const advanceStreetBtn = document.getElementById('advanceStreetBtn');
const amountInput = document.getElementById('amountInput');

function cardText(card) {
  if (!card) return '??';
  const valueMap = {
    '2': '2', '3': '3', '4': '4', '5': '5', '6': '6', '7': '7', '8': '8', '9': '9', '10': 'T',
    '11': 'J', '12': 'Q', '13': 'K', '14': 'A'
  };

  const rank = valueMap[card.rank] || card.rank || 'A';
  const suit = card.suit || 'S';
  const isRed = ['H', 'D'].includes(suit);
  return { label: `${rank}${suit}`, isRed };
}

function makeCardElement(card, className = 'board-card') {
  const el = document.createElement('div');
  const cardData = typeof card === 'string' ? parseCard(card) : card;
  const text = cardData ? cardData.label : '??';
  el.className = className + (cardData && cardData.isRed ? ' red' : '');
  el.textContent = text;
  return el;
}

function parseCard(cardValue) {
  if (!cardValue || cardValue.length < 2) {
    return null;
  }

  const value = cardValue.slice(0, -1);
  const suit = cardValue.slice(-1).toUpperCase();
  return {
    label: cardValue,
    rank: value,
    suit,
    isRed: ['H', 'D'].includes(suit)
  };
}

function renderBoard(board) {
  boardCards.innerHTML = '';
  if (!board || board.length === 0) {
    const empty = document.createElement('div');
    empty.className = 'board-card back';
    empty.textContent = '◼';
    boardCards.appendChild(empty);
    boardCards.appendChild(empty.cloneNode(true));
    boardCards.appendChild(empty.cloneNode(true));
    boardCards.appendChild(empty.cloneNode(true));
    boardCards.appendChild(empty.cloneNode(true));
    return;
  }

  board.forEach(card => {
    const element = makeCardElement(card, 'board-card');
    boardCards.appendChild(element);
  });
}

function renderPrivateCards(cards) {
  privateCards.innerHTML = '';
  if (!cards || cards.length === 0) {
    const placeholder = document.createElement('div');
    placeholder.className = 'private-card back';
    placeholder.textContent = '??';
    privateCards.appendChild(placeholder);
    privateCards.appendChild(placeholder.cloneNode(true));
    return;
  }

  cards.forEach(card => {
    const element = makeCardElement(card, 'private-card');
    privateCards.appendChild(element);
  });
}

function renderSeats(tableState) {
  for (let i = 0; i < 8; i++) {
    const seat = document.getElementById(`seat-${i}`);
    seat.innerHTML = '';
    seat.classList.remove('empty');

    const player = tableState?.players?.find(item => item.seatIndex === i);
    if (!player) {
      seat.classList.add('empty');
      seat.textContent = 'Empty';
      continue;
    }

    const name = document.createElement('div');
    name.className = 'name';
    name.textContent = player.nickname;

    const meta = document.createElement('div');
    meta.className = 'meta';
    meta.innerHTML = `<span>$${player.stack}</span><span>${player.folded ? 'Folded' : player.allIn ? 'All-in' : 'Live'}</span>`;

    const miniCards = document.createElement('div');
    miniCards.className = 'mini-cards';
    const holeCards = Array.isArray(player.holeCards) ? player.holeCards : [];
    holeCards.slice(0, 2).forEach(card => {
      const mini = document.createElement('div');
      const parsed = parseCard(card);
      mini.className = 'card-mini' + (parsed && parsed.isRed ? ' red' : '');
      mini.textContent = parsed ? parsed.label : '??';
      miniCards.appendChild(mini);
    });

    if (holeCards.length === 0) {
      const hidden = document.createElement('div');
      hidden.className = 'card-mini back';
      hidden.textContent = '🂠';
      miniCards.appendChild(hidden);
      miniCards.appendChild(hidden.cloneNode(true));
    }

    seat.appendChild(name);
    seat.appendChild(meta);
    seat.appendChild(miniCards);
  }
}

function renderChat(messages) {
  chatMessages.innerHTML = '';
  messages.forEach(message => {
    const item = document.createElement('div');
    item.className = 'chat-message';
    item.innerHTML = `
      <div class="meta">${message.nickname} • ${message.timestamp || 'now'}</div>
      <div class="text">${message.message}</div>
    `;
    chatMessages.appendChild(item);
  });
  chatMessages.scrollTop = chatMessages.scrollHeight;
}

function renderTableState() {
  if (!appState.tableState) return;
  const table = appState.tableState;
  streetLabel.textContent = table.currentStreet || 'Preflop';
  potValue.textContent = `Pot: $${table.pot || 0}`;
  renderBoard(table.board || []);
  renderSeats(table);
}

function handleTableMessage(payload) {
  const body = JSON.parse(payload.body);
  if (!body || typeof body !== 'object') return;

  if (body.type === 'your_cards') {
    appState.privateCards = Array.isArray(body.cards) ? body.cards : [];
    renderPrivateCards(appState.privateCards);
    return;
  }

  if (body.type === 'action_taken' || body.type === 'hand_started' || body.type === 'street_changed' || body.type === 'player_joined') {
    return;
  }

  appState.tableState = body;
  renderTableState();
}

function handleChatMessage(payload) {
  const message = JSON.parse(payload.body);
  appState.messages.push(message);
  renderChat(appState.messages);
}

function handlePrivateMessage(payload) {
  const msg = JSON.parse(payload.body);
  if (msg.type === 'your_cards') {
    appState.privateCards = msg.cards || [];
    renderPrivateCards(appState.privateCards);
    return;
  }

  if (msg.type === 'error') {
    alert(msg.message || 'Something went wrong');
  }
}

function connectWebSocket() {
  if (appState.connected || appState.connecting) {
    return;
  }

  appState.connecting = true;
  stompClient.connect({}, function () {
    appState.connecting = false;
    appState.connected = true;
    stompClient.subscribe('/topic/table', handleTableMessage);
    stompClient.subscribe('/topic/chat', handleChatMessage);
    stompClient.subscribe('/user/queue/private', handlePrivateMessage);
    if (appState.pendingNickname) {
      sendJoinRequest(appState.pendingNickname);
    }
  }, function () {
    appState.connecting = false;
    appState.connected = false;
    alert('WebSocket connection failed. Refresh and try again.');
  });
}

function sendJoinRequest(nickname) {
  if (!appState.connected) {
    appState.pendingNickname = nickname;
    return;
  }

  appState.pendingNickname = '';
  stompClient.send('/app/table.join', {}, JSON.stringify({ nickname }));
}

joinForm.addEventListener('submit', function (event) {
  event.preventDefault();
  const nickname = nicknameInput.value.trim();
  if (!nickname) {
    alert('Enter a nickname first');
    return;
  }

  appState.currentUser = nickname;
  if (appState.connected) {
    sendJoinRequest(nickname);
  } else {
    appState.pendingNickname = nickname;
    connectWebSocket();
  }
  nicknameInput.value = '';
});

chatForm.addEventListener('submit', function (event) {
  event.preventDefault();
  if (!appState.currentUser) {
    alert('Join the table before chatting');
    return;
  }

  const message = chatInput.value.trim();
  if (!message) {
    return;
  }

  const payload = {
    nickname: appState.currentUser,
    message,
    timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
  };

  stompClient.send('/app/chat', {}, JSON.stringify(payload));
  chatInput.value = '';
});

document.querySelectorAll('[data-action]').forEach(button => {
  button.addEventListener('click', function () {
    if (!appState.currentUser) {
      alert('Join the table first');
      return;
    }

    const action = button.dataset.action;
    const payload = {
      nickname: appState.currentUser,
      action,
      amount: Number(amountInput.value || 0)
    };

    stompClient.send('/app/action', {}, JSON.stringify(payload));
  });
});

startHandBtn.addEventListener('click', function () {
  if (!appState.currentUser) {
    alert('Join the table first');
    return;
  }
  stompClient.send('/app/table.startHand', {}, '{}');
});

advanceStreetBtn.addEventListener('click', function () {
  if (!appState.currentUser) {
    alert('Join the table first');
    return;
  }
  stompClient.send('/app/table.advanceStreet', {}, '{}');
});

renderPrivateCards(appState.privateCards);
renderBoard([]);
renderChat(appState.messages);
connectWebSocket();
