var params = {
    "dicUrl": "http://dictionary.yandex.net/dicservice.json",
    "family": 1,
    "host": "http://translate.yandex.net",
    "sid": "69d09f23",
    "stoken": "b2b6343a"
};

params.langs = { dict: ["be-be", "be-ru", "bg-ru", "cs-ru", "de-en", "de-ru", "de-tr", "en-de", "en-en", "en-es", "en-fr", "en-it", "en-ru", "en-tr", "es-en", "es-ru", "fr-en", "fr-ru", "it-en", "it-ru", "pl-ru", "ru-be", "ru-bg", "ru-cs", "ru-de", "ru-en", "ru-es", "ru-fr", "ru-it", "ru-pl", "ru-ru", "ru-tr", "ru-uk", "tr-de", "tr-en", "tr-ru", "uk-ru", "uk-uk"], pdct: ["ar", "az", "be", "bg", "bs", "ca", "cs", "da", "de", "el", "en", "es", "et", "fi", "fr", "he", "hr", "hu", "hy", "id", "is", "it", "ka", "lt", "lv", "mk", "ms", "mt", "nl", "no", "pl", "pt", "ro", "ru", "sk", "sl", "sq", "sr", "sv", "tr", "uk", "vi"], splr: ["en", "kk", "ru", "uk"], trnsl: {"ar": "Арабский", "az": "Азербайджанский", "be": "Белорусский", "bg": "Болгарский", "bs": "Боснийский", "ca": "Каталанский", "cs": "Чешский", "da": "Датский", "de": "Немецкий", "el": "Греческий", "en": "Английский", "es": "Испанский", "et": "Эстонский", "fi": "Финский", "fr": "Французский", "he": "Иврит", "hr": "Хорватский", "hu": "Венгерский", "hy": "Армянский", "id": "Индонезийский", "is": "Исландский", "it": "Итальянский", "ka": "Грузинский", "lt": "Литовский", "lv": "Латышский", "mk": "Македонский", "ms": "Малайский", "mt": "Мальтийский", "nl": "Голландский", "no": "Норвежский", "pl": "Польский", "pt": "Португальский", "ro": "Румынский", "ru": "Русский", "sk": "Словацкий", "sl": "Словенский", "sq": "Албанский", "sr": "Сербский", "sv": "Шведский", "tr": "Турецкий", "uk": "Украинский", "vi": "Вьетнамский", "zh": "Китайский"} };
params.authUser = true;
params.ui = 'ru';
params.stoken = 'b2b6343a';
params.tts = { id: 'ttsFlash', hq: true, swf: '/v1.87/swf/player.swf', url: 'http://tts.voicetech.yandex.net/tts' };
params.clipboardSwfPath = '/v1.87/swf/clipboard.swf';

window.sendMetrikaGoal = function (goal, params) {
    try {
        window.yaCounter20844859.reachGoal(String(goal), Object(params));
    } catch (error) {
    }
};

var tr = new Tr(params);

$('.b-head-tabs__link').on('mouseenter', function (e) {
    var searchUrl = $(this).data('searchUrl'), originalUrl = $(this).data('originalUrl'), href = $(this).attr('href'), text = $('#srcText').val();
    text = (text.length > Tr.BLOCK_LEN) ? '' : text;
    if (text && searchUrl) {
        $(this).attr('href', searchUrl + text);
    } else if (!text && href !== originalUrl) {
        $(this).attr('href', originalUrl);
    }
});

$(document).on('click', '[data-metrika-goal]', function () {
    var goal = $.trim($(this).attr('data-metrika-goal'));
    if (goal) {
        window.sendMetrikaGoal(goal);
    }
});

$('form[name="tr-url"] select').YTSelect();