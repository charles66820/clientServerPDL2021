import { nextTick } from "vue"
import { createI18n } from "vue-i18n"
import axios from "axios";

const SUPPORT_LOCALES = ["en", "fr"]


import en from './locales/en.json'

const options = {
  globalInjection: true,
  legacy: false,
  locale: localStorage.lang? localStorage.lang : "en",
  fallbackLocale: "en",
  messages: {
    en
  }
};

const i18n = createI18n(options);

setI18nLanguage(i18n, options.locale);

async function setI18nLanguage(i18n, locale) {
  if (!SUPPORT_LOCALES.includes(locale)) return;

  // load locale messages
  if (!i18n.global.availableLocales.includes(locale))
    await loadLocaleMessages(i18n, locale);

  if (i18n.mode === "legacy")
    i18n.global.locale = locale;
  else i18n.global.locale.value = locale;

  localStorage.lang = locale;
  axios.defaults.headers.common["Accept-Language"] = locale;
  document.querySelector("html").setAttribute("lang", locale);
}

async function loadLocaleMessages(i18n, locale) {
  const messages = await import(
    `./locales/${locale}.json`
  );

  // set locale and locale message
  i18n.global.setLocaleMessage(locale, messages.default);

  return nextTick();
}

export {
  SUPPORT_LOCALES,
  i18n,
  setI18nLanguage
}