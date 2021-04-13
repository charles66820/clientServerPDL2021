import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { initI18n } from './i18n'

import 'bootstrap'
import 'bootstrap/dist/css/bootstrap.min.css'

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

(async () => createApp(App).use(await initI18n(options)).use(router).mount('#app'))();
