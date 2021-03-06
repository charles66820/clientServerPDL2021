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

function getErrorMsg(err) {
  let errorMessage;

  if (err == null) return i18n.global.t("errors.notAnError");
  if (err.response == undefined) return err.message;

  let res = err.response;

  let defaultErrorMessage = res.statusText ? res.statusText : err.message;

  switch (res.headers["content-type"]) {
    case "text/plain":
      errorMessage = res.data;
      break;
    case "application/json":
    case "application/json;charset=UTF-8":
      switch (res.data.type) {
        case "BadParamsException": {
          if (res.data.incompatibleSelectedImage)
            errorMessage = i18n.global.t("errors.BadParamsException.incompatibleSelectedImageContent") + " !";
          else if (res.data.badParams && res.data.badParams.length > 0) {
            errorMessage = ((res.data.badParams.length > 1) ? i18n.global.t("errors.BadParamsException.badParamsContentPlurals") : i18n.global.t("errors.BadParamsException.badParamsContent")) + " :";
            for (const param of res.data.badParams) {
              if (param.type == "number") {
                errorMessage += "<br> - " + i18n.global.t("errors.BadParamsException.numberParam", { title: param.title, min: param.min, max: param.max });
                if (param.value != undefined && param.value != "null" && param.value != "") errorMessage += " " + i18n.global.t("errors.BadParamsException.butCannotBe", { value: param.value });
              } else if (param.type == "select") {
                errorMessage += "<br> - " + i18n.global.t("errors.BadParamsException.selectParam", { title: param.title }) + " ";
                let expectedValues = param.expectedValue;
                if (!expectedValues || expectedValues.length == 0) {
                  errorMessage += i18n.global.t("errors.nothing");
                } else {
                  for (let i = 0; i < expectedValues.length - 1; i++) {
                    if (i != 0) errorMessage += ", ";
                    errorMessage += expectedValues[i].title;
                  }
                  if (expectedValues.length > 1) errorMessage += " " + i18n.global.t("errors.or") + " ";
                  errorMessage += expectedValues[expectedValues.length - 1].title + " !";
                }
                if (param.value != undefined && param.value != "null" && param.value != "") errorMessage += " " + i18n.global.t("errors.BadParamsException.butCannotBe", { value: param.value });
              } else errorMessage += "&#10; - " + i18n.global.t("errors.BadParamsException.otherParam", { title: param.title, value: param.value });
            }
          } else {
            errorMessage = i18n.global.t("errors.BadParamsException.unknownErrorContent", { msg: res.data.message });
          }
          break;
        }
        case "ImageConversionException":
          errorMessage = i18n.global.t("errors.ImageConversionException.content") + " !";
          break;
        case "UnknownAlgorithmException":
          if (res.data.title) {
            errorMessage = i18n.global.t("errors.UnknownAlgorithmException.canNotBeExecutedContent", { title: res.data.title }) + " !";
            break;
          }
          errorMessage = i18n.global.t("errors.UnknownAlgorithmException.content") + " !";
          break;
        case "UnsupportedMediaTypeException": {
          errorMessage = i18n.global.t("errors.UnsupportedMediaTypeException.content") + " ";
          let acceptedTypes = res.data.acceptedTypes;
          if (!acceptedTypes || acceptedTypes.length == 0) {
            errorMessage += i18n.global.t("errors.nothing") + " !";
            break;
          }
          for (let i = 0; i < acceptedTypes.length - 1; i++) {
            if (i != 0) errorMessage += ", ";
            errorMessage += acceptedTypes[i];
          }
          if (acceptedTypes.length > 1) errorMessage += " " + i18n.global.t("errors.or") + " ";
          errorMessage += acceptedTypes[acceptedTypes.length - 1] + " !";
          break;
        }
        case "BadImageFileException":
          errorMessage = i18n.global.t("errors.BadImageFileException.content") + " !";
          break;
        case "UnknownException":
          errorMessage = i18n.global.t("errors.UnknownException.content", { msg: res.data.message });
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

function humanFileSize(bytes) {
  const units = [
    i18n.global.t("units.B"),
    i18n.global.t("units.KB"),
    i18n.global.t("units.MB"),
    i18n.global.t("units.GB"),
    i18n.global.t("units.TB"),
    i18n.global.t("units.PB"),
    i18n.global.t("units.EB"),
    i18n.global.t("units.ZB"),
    i18n.global.t("units.YB")
  ];
  if (Math.abs(bytes) < 1000) return bytes + " " + units[0];

  let u = -1;
  do {
    bytes /= 1000;
    ++u;
  } while (Math.round(Math.abs(bytes) * 10 ** 1) / 10 ** 1 >= 1000 && u < units.length - 2);

  return bytes.toFixed(1) + " " + units[u + 1];
}

axios.interceptors.response.use(res => res,
  err => {
    if (err.request.responseType === 'blob' &&
      err.response.data instanceof Blob &&
      err.response.data.type &&
      err.response.data.type.toLowerCase().indexOf('json') != -1
    ) {
      return new Promise((resolve, reject) => {
        let reader = new FileReader();
        reader.addEventListener("load", () => {
          err.response.data = JSON.parse(reader.result);
          resolve(Promise.reject(err));
        });
        reader.addEventListener("error", () => reject(err));
        reader.readAsText(err.response.data);
      });
    }
    return Promise.reject(err);
  }
);

export {
  SUPPORT_LOCALES,
  mode,
  initI18n,
  setI18nLanguage,
  getErrorMsg,
  humanFileSize
}