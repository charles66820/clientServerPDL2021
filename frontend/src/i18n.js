import { nextTick } from "vue"
import { createI18n } from "vue-i18n"
import axios from "axios";

const SUPPORT_LOCALES = ["en", "fr"]

let mode;
let i18n;

async function initI18n(options) {
  i18n = createI18n(options);
  await setI18nLanguage(options.locale);
  mode = i18n.mode;
  return i18n;
}

async function setI18nLanguage(locale) {
  if (!SUPPORT_LOCALES.includes(locale)) return;

  // load locale messages
  if (!i18n.global.availableLocales.includes(locale))
    await loadLocaleMessages(locale);

  if (i18n.mode === "legacy")
    i18n.global.locale = locale;
  else i18n.global.locale.value = locale;

  localStorage.lang = locale;
  axios.defaults.headers.common["Accept-Language"] = locale;
  document.querySelector("html").setAttribute("lang", locale);
}

async function loadLocaleMessages(locale) {
  const messages = await import(
    `./locales/${locale}.json`
  );

  // set locale and locale message
  i18n.global.setLocaleMessage(locale, messages.default);

  return nextTick();
}

export {
  SUPPORT_LOCALES,
  mode,
  initI18n,
  setI18nLanguage
}