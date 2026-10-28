const appState = {
  currentUser: '',
  pendingNickname: '',
  connected: false,
  connecting: false,
  joined: false,
  actionPending: false,
  requestedPrivateHand: -1,
  displayedPrivateHand: -1,
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
const amountInput = document.getElementById('amountInput');
const gameStatus = document.getElementById('gameStatus');
const clientId = getClientId();

function getClientId() {
  const key = 'poker-online-client-id';
  let id = localStorage.getItem(key);
  if (!id) {
    id = typeof crypto.randomUUID === 'function'
      ? crypto.randomUUID()
      : `${Date.now()}-${Math.random().toString(36).slice(2)}`;
    localStorage.setItem(key, id);
  }
  return id;
}

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
    seat.classList.remove('empty', 'current-turn');

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
    const stack = document.createElement('span');
    stack.textContent = `$${player.stack}`;
    const state = document.createElement('span');
    state.textContent = player.folded ? 'Folded' : player.allIn ? 'All-in' : player.bet > 0 ? `Bet $${player.bet}` : 'In hand';
    meta.append(stack, state);
    if (i === tableState.currentActorSeat && tableState.handInProgress) {
      seat.classList.add('current-turn');
    }

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
    const meta = document.createElement('div');
    meta.className = 'meta';
    meta.textContent = `${message.nickname} • ${message.timestamp || 'now'}`;
    const text = document.createElement('div');
    text.className = 'text';
    text.textContent = message.message;
    item.append(meta, text);
    chatMessages.appendChild(item);
  });
  chatMessages.scrollTop = chatMessages.scrollHeight;
}

function renderTableState() {
  if (!appState.tableState) return;
  const table = appState.tableState;
  if (table.handInProgress && appState.displayedPrivateHand !== table.handNumber) {
    appState.privateCards = [];
    appState.displayedPrivateHand = table.handNumber;
    renderPrivateCards(appState.privateCards);
  }
  streetLabel.textContent = table.currentStreet || 'Preflop';
  potValue.textContent = `Pot: $${table.pot || 0}`;
  const myPlayer = table.players?.find(player => player.nickname.toLowerCase() === appState.currentUser.toLowerCase());
  appState.joined = Boolean(myPlayer);
  joinForm.querySelector('button[type="submit"]').disabled = appState.joined;
  nicknameInput.disabled = appState.joined;
  startHandBtn.disabled = !appState.joined || table.handInProgress
    || (table.players || []).filter(player => Number(player.stack) > 0).length < 2;
  startHandBtn.textContent = table.handInProgress ? 'Hand in progress' : 'Start next hand';
  gameStatus.textContent = table.handInProgress
    ? `${table.statusMessage || ''} ${table.currentActorSeat >= 0 ? `To call: $${Math.max(0, Number(table.currentBet) - Number(myPlayer?.bet || 0))} · ${table.turnSecondsRemaining}s` : ''}`.trim()
    : table.statusMessage || 'Waiting for players.';
  const myTurn = Boolean(myPlayer && table.handInProgress && myPlayer.seatIndex === table.currentActorSeat);
  const callRequired = myTurn && Number(table.currentBet) > Number(myPlayer.bet);
  document.querySelectorAll('[data-action]').forEach(button => {
    const action = button.dataset.action;
    const allowed = myTurn
    && (action === 'ALL_IN'
      || (action === 'FOLD' && callRequired)
        || (action === 'CHECK' && !callRequired)
        || (action === 'CALL' && callRequired)
        || (action === 'BET' && Number(table.currentBet) === 0)
        || (action === 'RAISE' && Number(table.currentBet) > 0));
    button.disabled = !allowed || appState.actionPending;
  });
  if (myTurn) {
    const minimumTarget = Number(table.currentBet) === 0
      ? Number(table.minRaise)
      : Number(table.currentBet) + Number(table.minRaise);
    amountInput.min = String(minimumTarget);
    amountInput.max = String(Number(myPlayer.bet) + Number(myPlayer.stack));
  }
  renderBoard(table.board || []);
  renderSeats(table);
  if (appState.joined && table.handInProgress && myPlayer.inHand
      && appState.requestedPrivateHand !== table.handNumber) {
    appState.requestedPrivateHand = table.handNumber;
    stompClient.send('/app/table.privateCards', {}, '{}');
  }
}

function handleTableMessage(payload) {
  const body = JSON.parse(payload.body);
  if (!body || typeof body !== 'object') return;

  const myPlayer = appState.tableState?.players?.find(
    player => player.nickname.toLowerCase() === appState.currentUser.toLowerCase()
  );
  if (!body.handInProgress || !myPlayer || body.currentActorSeat !== myPlayer.seatIndex) {
    appState.actionPending = false;
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
    appState.actionPending = false;
    renderTableState();
    window.alert(msg.message || 'Something went wrong');
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
  appState.currentUser = nickname;
  stompClient.send('/app/table.join', {}, JSON.stringify({ nickname, clientId }));
}

joinForm.addEventListener('submit', function (event) {
  event.preventDefault();
  const nickname = nicknameInput.value.trim();
  if (!nickname) {
    alert('Enter a nickname first');
    return;
  }

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
    if (!appState.joined || !appState.tableState || button.disabled) {
      return;
    }

    const action = button.dataset.action;
    const payload = {
      action,
      amount: Number(amountInput.value || 0)
    };

    appState.actionPending = true;
    document.querySelectorAll('[data-action]').forEach(actionButton => actionButton.disabled = true);
    stompClient.send('/app/action', {}, JSON.stringify(payload));
  });
});

startHandBtn.addEventListener('click', function () {
  if (!appState.currentUser) {
    window.alert('Join the table first');
    return;
  }
  stompClient.send('/app/table.startHand', {}, '{}');
});

renderPrivateCards(appState.privateCards);
renderBoard([]);
renderChat(appState.messages);
connectWebSocket();
