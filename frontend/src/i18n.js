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

async function getErrorMsg(err) {
  let errorMessage;

  if (err == null) return "Not an error";
  if (err.response == undefined) return err.message;

  let res = err.response;

  let defaultErrorMessage = res.statusText ? res.statusText : err.message;

  switch (res.headers["content-type"]) {
    case "text/plain":
      errorMessage = res.data;
      break;
    case "application/json":
    case "application/json;charset=UTF-8":
      switch (res.data.type) { // TODO: after debug
        case "BadParamsException":
          errorMessage = "Bad params";
          break;
        case "ImageConversionException":
          errorMessage = "Error on image conversion";
          break;
        case "UnknownAlgorithmException":
          errorMessage = "Unknown algorithm";
          break;
        case "UnsupportedMediaTypeException":
          errorMessage = "Unsupported image type ! Image will be image/jpeg or image/tiff";
          break;
        case "BadImageFileException":
          errorMessage = "Bad image file send !";
          break;
        case "UnknownException":
          errorMessage = "Unknown error (" + res.data.message + ")";
          break;
        default:
          errorMessage = res.data.message ? res.data.message : defaultErrorMessage;
      }
      break;
    default:
      errorMessage = defaultErrorMessage;
  }

  return errorMessage;
}

export {
  SUPPORT_LOCALES,
  mode,
  initI18n,
  setI18nLanguage,
  getErrorMsg
}