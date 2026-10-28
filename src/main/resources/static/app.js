const translations = {
  ru: {
    pageTitle: 'Poker Night — Техасский холдем',
    subtitle: 'Техасский холдем · приватный стол',
    startHand: 'Начать раздачу',
    nextHand: 'Следующая раздача',
    handInProgress: 'Идёт раздача',
    matchFinished: 'Матч завершён',
    restartMatch: 'Сбросить матч',
    tableLabel: 'ДРУЗЬЯ ЗА СТОЛОМ',
    privateCardsLabel: 'ВАША РУКА',
    yourCards: 'Ваши карты',
    cardsHint: 'Карты видны только вам',
    seatLabel: 'ВАШЕ МЕСТО',
    joinTable: 'Сесть за стол',
    nicknamePlaceholder: 'Ваш никнейм',
    sitDown: 'Занять место',
    seatHint: 'До 8 игроков · один профиль браузера — одно место',
    yourMove: 'ВАШ ХОД',
    actions: 'Действия',
    check: 'Чек',
    call: 'Колл',
    bet: 'Ставка',
    raise: 'Рейз',
    fold: 'Фолд',
    allIn: 'Олл-ин',
    betAmount: 'Размер ставки',
    betHint: 'Укажите итоговую ставку на этой улице.',
    tableTalk: 'ЗА СТОЛОМ',
    chat: 'Чат',
    chatPlaceholder: 'Написать сообщение…',
    send: 'Отправить',
    footerNote: 'Играйте дружески. Фишки виртуальные.',
    emptySeat: 'Свободно',
    dealer: 'Дилер',
    folded: 'Сбросил',
    allInStatus: 'Олл-ин',
    betStatus: 'Ставка',
    inHand: 'В раздаче',
    busted: 'Без фишек',
    pot: 'Банк',
    readyToStart: 'Игроков достаточно — можно начинать раздачу.',
    preflop: 'Префлоп',
    flop: 'Флоп',
    turn: 'Тёрн',
    river: 'Ривер',
    showdown: 'Вскрытие',
    callAndTime: 'Колл: {amount} · {seconds} сек.',
    waitingForPlayers: 'Подключитесь к столу, чтобы начать игру.',
    waitingForOpponent: 'Нужно минимум два игрока с фишками.',
    matchComplete: 'Матч завершён — победитель: {name}! Сбросьте матч, чтобы сыграть снова.',
    winnerHand: '{name} забирает банк без вскрытия.',
    showdownWinner: '{name} победил на вскрытии.',
    playerTurn: 'Ход игрока: {name}.',
    raisedTurn: 'Повышение. Ход игрока: {name}.',
    disconnectedTurn: '{left} отключился и сбросил карты. Ход игрока: {name}.',
    timeoutFold: '{name} не успел и сбросил карты. ',
    timeoutCheck: '{name} не успел и сделал чек. ',
    matchRestarted: 'Матч сброшен. Можно начинать новую игру.',
    confirmRestart: 'Сбросить матч целиком? Все игроки за столом получат по 1 000 фишек.',
    restartDuringHand: 'Сначала дождитесь окончания текущей раздачи.',
    joinBeforeRestart: 'Чтобы сбросить матч, сначала займите место за столом.',
    joinBeforeStart: 'Сначала займите место за столом.',
    joinBeforeChat: 'Займите место за столом, чтобы писать в чат.',
    nicknameRequired: 'Введите никнейм.',
    connectionFailed: 'Не удалось подключиться к игровому серверу. Обновите страницу и попробуйте снова.',
    unknownError: 'Что-то пошло не так.',
    errors: {
      'Join the table before starting a hand.': 'Сначала займите место за столом.',
      'A hand is already in progress.': 'Раздача уже идёт.',
      'At least two players with chips are required to start.': 'Для начала нужны минимум два игрока с фишками.',
      'Join the table before restarting the match.': 'Чтобы сбросить матч, сначала займите место за столом.',
      'Finish the current hand before restarting the match.': 'Сначала дождитесь окончания текущей раздачи.',
      'There is no hand in progress.': 'Сейчас раздача не идёт.',
      'You are not playing this hand.': 'Вы не участвуете в этой раздаче.',
      'It is not your turn.': 'Сейчас ход другого игрока.',
      'You cannot act right now.': 'Сейчас вы не можете сделать ход.',
      'You can check instead of folding.': 'Можно сделать чек вместо сброса.',
      'You must call or fold; checking is not allowed.': 'Нужно уравнять ставку или сбросить карты.',
      'There is no bet to call; check instead.': 'Ставки нет — выберите чек.',
      'Use whole chip amounts.': 'Ставка должна быть целым числом.',
      'Enter a valid total bet for this street.': 'Введите корректную итоговую ставку на этой улице.',
      'A bet or raise must exceed the current bet.': 'Ставка должна быть выше текущей.',
      'Use Raise because there is already a bet.': 'Уже есть ставка — выберите рейз.',
      'Use Bet when there is no bet to raise.': 'Ставок пока нет — выберите ставку.',
      'A short all-in did not reopen raising.': 'Короткий олл-ин не открыл возможность рейза.',
      'There are no chips left to go all-in.': 'У вас не осталось фишек для олл-ина.',
      'Choose a poker action.': 'Выберите действие.',
      'Unknown poker action.': 'Неизвестное действие.',
      'This connection already has a seat.': 'Это подключение уже заняло место.',
      'This browser already has a seat under another nickname.': 'В этом браузере уже занято место под другим никнеймом.',
      'That nickname is already seated.': 'Этот никнейм уже за столом.',
      'Table is full': 'Стол заполнен.',
      'Bet exceeds your remaining chips.': 'Ставка превышает ваш стек.',
      'Enter a nickname first': 'Сначала введите никнейм.',
      'Nickname must be 20 characters or fewer': 'Никнейм должен содержать не более 20 символов.',
      'Browser session is missing. Refresh the page and try again.': 'Не найдена сессия браузера. Обновите страницу и попробуйте снова.'
    }
  },
  en: {
    pageTitle: 'Poker Night — Texas Hold’em',
    subtitle: 'Texas Hold’em · private table',
    startHand: 'Start hand',
    nextHand: 'Next hand',
    handInProgress: 'Hand in progress',
    matchFinished: 'Match finished',
    restartMatch: 'Reset match',
    tableLabel: 'FRIENDS AT THE TABLE',
    privateCardsLabel: 'YOUR HAND',
    yourCards: 'Your cards',
    cardsHint: 'Only you can see your cards',
    seatLabel: 'YOUR SEAT',
    joinTable: 'Join the table',
    nicknamePlaceholder: 'Your nickname',
    sitDown: 'Take a seat',
    seatHint: 'Up to 8 players · one browser profile per seat',
    yourMove: 'YOUR TURN',
    actions: 'Actions',
    check: 'Check',
    call: 'Call',
    bet: 'Bet',
    raise: 'Raise',
    fold: 'Fold',
    allIn: 'All-in',
    betAmount: 'Bet amount',
    betHint: 'Enter your total bet for this street.',
    tableTalk: 'AT THE TABLE',
    chat: 'Chat',
    chatPlaceholder: 'Write a message…',
    send: 'Send',
    footerNote: 'Play nice. Chips are virtual.',
    emptySeat: 'Open seat',
    dealer: 'Dealer',
    folded: 'Folded',
    allInStatus: 'All-in',
    betStatus: 'Bet',
    inHand: 'In hand',
    busted: 'Out of chips',
    pot: 'Pot',
    readyToStart: 'Enough players — you can start a hand.',
    preflop: 'Pre-flop',
    flop: 'Flop',
    turn: 'Turn',
    river: 'River',
    showdown: 'Showdown',
    callAndTime: 'To call: {amount} · {seconds}s',
    waitingForPlayers: 'Join the table to start playing.',
    waitingForOpponent: 'At least two players with chips are needed.',
    matchComplete: 'Match complete — {name} wins! Reset the match to play again.',
    winnerHand: '{name} wins the pot uncontested.',
    showdownWinner: '{name} wins at showdown.',
    playerTurn: '{name} to act.',
    raisedTurn: 'Raise. {name} to act.',
    disconnectedTurn: '{left} disconnected and folded. {name} to act.',
    timeoutFold: '{name} timed out and folded. ',
    timeoutCheck: '{name} timed out and checked. ',
    matchRestarted: 'Match reset. Ready to start a new game.',
    confirmRestart: 'Reset the entire match? Every seated player will receive 1,000 chips.',
    restartDuringHand: 'Wait for the current hand to finish first.',
    joinBeforeRestart: 'Take a seat before resetting the match.',
    joinBeforeStart: 'Take a seat at the table first.',
    joinBeforeChat: 'Join the table to chat.',
    nicknameRequired: 'Enter a nickname.',
    connectionFailed: 'Could not connect to the game server. Refresh and try again.',
    unknownError: 'Something went wrong.',
    errors: {
      'Join the table before starting a hand.': 'Take a seat at the table first.',
      'A hand is already in progress.': 'A hand is already in progress.',
      'At least two players with chips are required to start.': 'At least two players with chips are required.',
      'Join the table before restarting the match.': 'Take a seat before resetting the match.',
      'Finish the current hand before restarting the match.': 'Wait for the current hand to finish first.',
      'There is no hand in progress.': 'There is no hand in progress.',
      'You are not playing this hand.': 'You are not in this hand.',
      'It is not your turn.': 'It is not your turn.',
      'You cannot act right now.': 'You cannot act right now.',
      'You can check instead of folding.': 'You can check instead of folding.',
      'You must call or fold; checking is not allowed.': 'Call or fold; checking is not allowed.',
      'There is no bet to call; check instead.': 'There is no bet to call. Check instead.',
      'Use whole chip amounts.': 'Bet amounts must be whole numbers.',
      'Enter a valid total bet for this street.': 'Enter a valid total bet for this street.',
      'A bet or raise must exceed the current bet.': 'Your bet must exceed the current bet.',
      'Use Raise because there is already a bet.': 'Use Raise because there is already a bet.',
      'Use Bet when there is no bet to raise.': 'Use Bet when there is no bet to raise.',
      'A short all-in did not reopen raising.': 'A short all-in did not reopen raising.',
      'There are no chips left to go all-in.': 'You have no chips left to go all-in.',
      'Choose a poker action.': 'Choose a poker action.',
      'Unknown poker action.': 'Unknown poker action.',
      'This connection already has a seat.': 'This connection already has a seat.',
      'This browser already has a seat under another nickname.': 'This browser already has a seat under another nickname.',
      'That nickname is already seated.': 'That nickname is already seated.',
      'Table is full': 'The table is full.',
      'Bet exceeds your remaining chips.': 'Bet exceeds your remaining chips.',
      'Enter a nickname first': 'Enter a nickname first.',
      'Nickname must be 20 characters or fewer': 'Nickname must be 20 characters or fewer.',
      'Browser session is missing. Refresh the page and try again.': 'Browser session missing. Refresh and try again.'
    }
  }
};

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
  messages: [],
  language: localStorage.getItem('poker-online-language') === 'en' ? 'en' : 'ru'
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
const restartMatchBtn = document.getElementById('restartMatchBtn');
const languageBtn = document.getElementById('languageBtn');
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

function t(key, values = {}) {
  let text = translations[appState.language][key] || key;
  Object.entries(values).forEach(([name, value]) => {
    text = text.replace(`{${name}}`, value);
  });
  return text;
}

function applyLanguage() {
  document.documentElement.lang = appState.language;
  document.title = t('pageTitle');
  document.querySelectorAll('[data-i18n]').forEach(element => {
    element.textContent = t(element.dataset.i18n);
  });
  document.querySelectorAll('[data-i18n-placeholder]').forEach(element => {
    element.placeholder = t(element.dataset.i18nPlaceholder);
  });
  languageBtn.textContent = appState.language === 'ru' ? 'EN' : 'RU';
  languageBtn.setAttribute('aria-label', appState.language === 'ru' ? 'Switch to English' : 'Переключить на русский');
  if (appState.tableState) {
    renderTableState();
  }
  renderChat(appState.messages);
}

function cardDetails(cardValue) {
  if (!cardValue || cardValue.length < 2) return null;
  const rankCode = cardValue.slice(0, -1).toUpperCase();
  const suitCode = cardValue.slice(-1).toUpperCase();
  const rankMap = { '10': '10', T: '10', J: 'J', Q: 'Q', K: 'K', A: 'A' };
  const suitMap = { S: '♠', H: '♥', D: '♦', C: '♣' };
  const rank = rankMap[rankCode] || rankCode;
  const suit = suitMap[suitCode];
  if (!suit || !rank) return null;
  const rankNames = { J: 'Jack', Q: 'Queen', K: 'King', A: 'Ace' };
  const suitNames = { S: 'spades', H: 'hearts', D: 'diamonds', C: 'clubs' };
  const localRank = appState.language === 'ru'
    ? ({ J: 'валет', Q: 'дама', K: 'король', A: 'туз' }[rank] || rank)
    : (rankNames[rank] || rank);
  const localSuit = appState.language === 'ru'
    ? ({ S: 'пики', H: 'черви', D: 'бубны', C: 'трефы' }[suitCode])
    : suitNames[suitCode];
  return { rank, suit, red: suitCode === 'H' || suitCode === 'D', ariaLabel: `${localRank} ${localSuit}` };
}

function makeCardElement(cardValue, className = 'board-card') {
  const el = document.createElement('div');
  const card = cardDetails(cardValue);
  if (!card) {
    el.className = `${className} back`;
    el.textContent = '♠';
    return el;
  }
  el.className = `${className}${card.red ? ' red' : ''}`;
  el.setAttribute('aria-label', card.ariaLabel);
  const top = document.createElement('span');
  top.className = 'card-corner card-corner-top';
  const topRank = document.createElement('span');
  topRank.textContent = card.rank;
  const topSuit = document.createElement('span');
  topSuit.textContent = card.suit;
  top.append(topRank, topSuit);
  const center = document.createElement('span');
  center.className = 'card-center-suit';
  center.textContent = card.suit;
  const bottom = document.createElement('span');
  bottom.className = 'card-corner card-corner-bottom';
  const bottomRank = document.createElement('span');
  bottomRank.textContent = card.rank;
  const bottomSuit = document.createElement('span');
  bottomSuit.textContent = card.suit;
  bottom.append(bottomRank, bottomSuit);
  el.append(top, center, bottom);
  return el;
}

function renderBoard(board) {
  boardCards.replaceChildren();
  if (!board || board.length === 0) {
    for (let i = 0; i < 5; i++) {
      const empty = document.createElement('div');
      empty.className = 'board-card back';
      empty.textContent = '♠';
      boardCards.appendChild(empty);
    }
    return;
  }
  board.forEach(card => boardCards.appendChild(makeCardElement(card)));
  for (let i = board.length; i < 5; i++) {
    const empty = document.createElement('div');
    empty.className = 'board-card card-placeholder';
    boardCards.appendChild(empty);
  }
}

function renderPrivateCards(cards) {
  privateCards.replaceChildren();
  if (!cards || cards.length === 0) {
    const hint = document.createElement('div');
    hint.className = 'cards-empty-hint';
    hint.textContent = appState.joined
      ? (appState.language === 'ru' ? 'Карты появятся, когда начнётся раздача' : 'Your cards will appear when a hand starts')
      : (appState.language === 'ru' ? 'Займите место, чтобы получить карты' : 'Take a seat to receive your cards');
    privateCards.appendChild(hint);
    return;
  }
  cards.forEach(card => privateCards.appendChild(makeCardElement(card, 'private-card')));
}

function playerStatus(player) {
  if (player.stack <= 0) return t('busted');
  if (player.folded) return t('folded');
  if (player.allIn) return t('allInStatus');
  if (Number(player.bet) > 0) return `${t('betStatus')} · ${Number(player.bet).toLocaleString()}`;
  return player.inHand ? t('inHand') : '';
}

function renderSeats(tableState) {
  for (let i = 0; i < 8; i++) {
    const seat = document.getElementById(`seat-${i}`);
    seat.replaceChildren();
    seat.classList.remove('empty', 'current-turn', 'is-me', 'is-busted');

    const player = tableState?.players?.find(item => item.seatIndex === i);
    if (!player) {
      seat.classList.add('empty');
      seat.textContent = t('emptySeat');
      continue;
    }

    if (player.stack <= 0) seat.classList.add('is-busted');
    if (player.nickname.toLowerCase() === appState.currentUser.toLowerCase()) seat.classList.add('is-me');
    if (i === tableState.currentActorSeat && tableState.handInProgress) seat.classList.add('current-turn');

    const nameRow = document.createElement('div');
    nameRow.className = 'seat-name-row';
    const name = document.createElement('div');
    name.className = 'name';
    name.textContent = player.nickname;
    nameRow.appendChild(name);
    if (player.dealer) {
      const dealer = document.createElement('span');
      dealer.className = 'dealer-chip';
      dealer.textContent = 'D';
      dealer.title = t('dealer');
      nameRow.appendChild(dealer);
    }

    const stack = document.createElement('div');
    stack.className = 'stack-value';
    stack.textContent = `${Number(player.stack).toLocaleString()} ${appState.language === 'ru' ? 'фишек' : 'chips'}`;

    const state = document.createElement('div');
    state.className = 'player-status';
    state.textContent = playerStatus(player);

    const miniCards = document.createElement('div');
    miniCards.className = 'mini-cards';
    const holeCards = Array.isArray(player.holeCards) ? player.holeCards : [];
    if (holeCards.length) {
      holeCards.slice(0, 2).forEach(card => miniCards.appendChild(makeCardElement(card, 'card-mini')));
    } else if (tableState.handInProgress && player.inHand && !player.folded) {
      for (let count = 0; count < 2; count++) {
        const hidden = document.createElement('div');
        hidden.className = 'card-mini back';
        hidden.textContent = '♠';
        miniCards.appendChild(hidden);
      }
    }

    seat.append(nameRow, stack, state, miniCards);
  }
}

function renderChat(messages) {
  chatMessages.replaceChildren();
  messages.forEach(message => {
    const item = document.createElement('div');
    item.className = 'chat-message';
    const meta = document.createElement('div');
    meta.className = 'meta';
    meta.textContent = `${message.nickname} · ${message.timestamp || (appState.language === 'ru' ? 'сейчас' : 'now')}`;
    const text = document.createElement('div');
    text.className = 'text';
    text.textContent = message.message;
    item.append(meta, text);
    chatMessages.appendChild(item);
  });
  chatMessages.scrollTop = chatMessages.scrollHeight;
}

function streetName(street) {
  const streetKeys = { PREFLOP: 'preflop', FLOP: 'flop', TURN: 'turn', RIVER: 'river', SHOWDOWN: 'showdown' };
  return t(streetKeys[street] || 'preflop');
}

function localizeStatus(message, table) {
  if (table.matchOver && table.matchWinner) return t('matchComplete', { name: table.matchWinner });
  if (!message) return table.players?.length ? t('waitingForOpponent') : t('waitingForPlayers');
  if (message === 'Join with at least one other player to start a hand.'
      || message === 'The match has been restarted. Waiting for players.') {
    const activeStacks = (table.players || []).filter(player => Number(player.stack) > 0).length;
    if (activeStacks >= 2) return t('readyToStart');
    return table.players?.length ? t('waitingForOpponent') : t('waitingForPlayers');
  }
  let match = message.match(/^(.+) timed out and (folded|checked)\. (.*)$/);
  if (match) {
    const prefix = match[2] === 'folded' ? t('timeoutFold', { name: match[1] }) : t('timeoutCheck', { name: match[1] });
    return prefix + localizeStatus(match[3], table);
  }
  match = message.match(/^(.+) disconnected and folded\. (.+) to act\.$/);
  if (match) return t('disconnectedTurn', { left: match[1], name: match[2] });
  match = message.match(/^Bet raised\. (.+) to act\.$/);
  if (match) return t('raisedTurn', { name: match[1] });
  match = message.match(/^(?:Pre-flop|FLOP|TURN|RIVER): (.+) to act\.$/);
  if (match) return t('playerTurn', { name: match[1] });
  match = message.match(/^(.+) to act\.$/);
  if (match) return t('playerTurn', { name: match[1] });
  match = message.match(/^(.+) wins uncontested\.$/);
  if (match) return t('winnerHand', { name: match[1] });
  match = message.match(/^(.+) showdown\.$/);
  if (match) return t('showdownWinner', { name: match[1] });
  return message;
}

function translateError(message) {
  const dictionary = translations[appState.language].errors;
  if (dictionary[message]) return dictionary[message];
  const minimumRaise = message.match(/^Minimum raise is (.+)\.$/);
  if (minimumRaise) {
    return appState.language === 'ru'
      ? `Минимальный рейз — ${minimumRaise[1]}.`
      : `Minimum raise is ${minimumRaise[1]}.`;
  }
  return message || t('unknownError');
}

function renderTableState() {
  if (!appState.tableState) return;
  const table = appState.tableState;
  if (table.handInProgress && appState.displayedPrivateHand !== table.handNumber) {
    appState.privateCards = [];
    appState.displayedPrivateHand = table.handNumber;
    renderPrivateCards(appState.privateCards);
  }
  const myPlayer = table.players?.find(player => player.nickname.toLowerCase() === appState.currentUser.toLowerCase());
  appState.joined = Boolean(myPlayer);
  joinForm.querySelector('button[type="submit"]').disabled = appState.joined;
  nicknameInput.disabled = appState.joined;
  streetLabel.textContent = streetName(table.currentStreet);
  potValue.textContent = `${t('pot')} · ${Number(table.pot || 0).toLocaleString()}`;

  const activeStacks = (table.players || []).filter(player => Number(player.stack) > 0).length;
  startHandBtn.disabled = !appState.joined || table.handInProgress || activeStacks < 2;
  restartMatchBtn.disabled = !appState.joined || table.handInProgress;
  startHandBtn.textContent = table.handInProgress
    ? t('handInProgress')
    : (table.matchOver ? t('matchFinished') : (table.handNumber > 0 ? t('nextHand') : t('startHand')));

  const myTurn = Boolean(myPlayer && table.handInProgress && myPlayer.seatIndex === table.currentActorSeat);
  let status = localizeStatus(table.statusMessage, table);
  if (myTurn) {
    const amountToCall = Math.max(0, Number(table.currentBet) - Number(myPlayer?.bet || 0));
    const countdown = t('callAndTime', {
      amount: amountToCall.toLocaleString(),
      seconds: table.turnSecondsRemaining
    });
    status = `${status} ${countdown}`.trim();
  }
  gameStatus.textContent = status;

  const callRequired = myTurn && Number(table.currentBet) > Number(myPlayer.bet);
  document.querySelectorAll('[data-action]').forEach(button => {
    const action = button.dataset.action;
    const allowed = myTurn && (
      action === 'ALL_IN'
      || action === 'FOLD'
      || (action === 'CHECK' && !callRequired)
      || (action === 'CALL' && callRequired)
      || (action === 'BET' && Number(table.currentBet) === 0)
      || (action === 'RAISE' && Number(table.currentBet) > 0)
    );
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
  if (!table.handInProgress && table.currentStreet !== 'SHOWDOWN') {
    appState.privateCards = [];
    renderPrivateCards([]);
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
    if (!appState.tableState?.handInProgress || msg.handNumber !== appState.tableState.handNumber) return;
    appState.privateCards = msg.cards || [];
    renderPrivateCards(appState.privateCards);
    return;
  }

  if (msg.type === 'error') {
    appState.actionPending = false;
    renderTableState();
    window.alert(translateError(msg.message));
  }
}

function connectWebSocket() {
  if (appState.connected || appState.connecting) return;

  appState.connecting = true;
  stompClient.connect({}, function () {
    appState.connecting = false;
    appState.connected = true;
    stompClient.subscribe('/topic/table', handleTableMessage);
    stompClient.subscribe('/topic/chat', handleChatMessage);
    stompClient.subscribe('/user/queue/private', handlePrivateMessage);
    if (appState.pendingNickname) sendJoinRequest(appState.pendingNickname);
  }, function () {
    appState.connecting = false;
    appState.connected = false;
    window.alert(t('connectionFailed'));
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
    window.alert(t('nicknameRequired'));
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
  if (!appState.joined) {
    window.alert(t('joinBeforeChat'));
    return;
  }
  const message = chatInput.value.trim();
  if (!message) return;
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
    if (!appState.joined || !appState.tableState || button.disabled) return;
    const payload = { action: button.dataset.action, amount: Number(amountInput.value || 0) };
    appState.actionPending = true;
    document.querySelectorAll('[data-action]').forEach(actionButton => actionButton.disabled = true);
    stompClient.send('/app/action', {}, JSON.stringify(payload));
  });
});

startHandBtn.addEventListener('click', function () {
  if (!appState.currentUser) {
    window.alert(t('joinBeforeStart'));
    return;
  }
  stompClient.send('/app/table.startHand', {}, '{}');
});

restartMatchBtn.addEventListener('click', function () {
  if (!appState.joined || restartMatchBtn.disabled) return;
  if (!window.confirm(t('confirmRestart'))) return;
  stompClient.send('/app/table.restartMatch', {}, '{}');
});

languageBtn.addEventListener('click', function () {
  appState.language = appState.language === 'ru' ? 'en' : 'ru';
  localStorage.setItem('poker-online-language', appState.language);
  applyLanguage();
});

applyLanguage();
renderPrivateCards(appState.privateCards);
renderBoard([]);
renderChat(appState.messages);
connectWebSocket();
