<template>
  <div class="main">
    <header>
      <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <a class="navbar-brand" href="/">{{ $t("title") }}</a>
        <button
          class="navbar-toggler"
          type="button"
          data-toggle="collapse"
          data-target="#mainNavbarToggler"
          aria-controls="mainNavbarToggler"
          aria-expanded="false"
          :title="$t('navigations.other.toggleButton')"
          :aria-label="$t('navigations.other.toggleButton')"
        >
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="mainNavbarToggler">
          <ul class="navbar-nav mr-auto mt-2 mt-lg-0">
            <li class="nav-item">
              <router-link class="nav-link" :to="{ name: 'Home' }"
                >{{ $t("navigations.home") }}
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" :to="{ name: 'About' }">{{
                $t("navigations.about")
              }}</router-link>
            </li>
            <li class="nav-item">
              <button
                class="btn btn-sm nav-link"
                id="btnUploadImageModal"
                type="button"
                data-toggle="modal"
                data-target="#uploadImageModal"
              >
                {{ $t("navigations.uploadImage") }}
              </button>
            </li>
          </ul>
          <form class="form-inline my-2 my-lg-0">
            <label class="text-white pr-2"
              >{{ $t("navigations.other.language") }} :</label
            >
            <select
              class="form-control mr-sm-2"
              v-model="currentLocale"
              @change="langChange()"
              aria-label="Lang"
            >
              <option
                v-for="optionLocale in supportLocales"
                :key="optionLocale"
                :value="optionLocale"
              >
                {{ $t("langs." + optionLocale) }}
              </option>
            </select>
          </form>
        </div>
      </nav>
    </header>
    <router-view class="pageContent" />
    <UploadImage />
  </div>
</template>

<script>
import UploadImage from "@/components/UploadImage.vue";
import { ref } from "vue";
import { useI18n } from "vue-i18n";
import { mode, setI18nLanguage, SUPPORT_LOCALES } from "./i18n";

export default {
  name: "App",
  components: {
    UploadImage,
  },
  data() {
    return {
      locale: null,
      currentLocale: null,
      supportLocales: SUPPORT_LOCALES,
      updateLangListeners: [],
    };
  },
  created() {
    const { locale } = useI18n({ useScope: "global" });
    this.locale = locale;
    this.currentLocale = ref(mode === "legacy" ? locale : locale.value);
  },
  methods: {
    async langChange() {
      await setI18nLanguage(this.currentLocale);
      // Update
      for (const updateLangListener of this.updateLangListeners)
        updateLangListener();
    },
    addUpdateLangListener(listener) {
      const index = this.updateLangListeners.indexOf(listener);
      if (index == -1) this.updateLangListeners.push(listener);
    },
    removeUpdateLangListener(listener) {
      const index = this.updateLangListeners.indexOf(listener);
      if (index > -1) this.updateLangListeners.splice(index, 1);
    },
  },
};
</script>

<style>
body {
  top: 0;
  bottom: 0;
  position: absolute;
  height: 100%;
  width: 100%;
  margin: 0;
  padding: 0;
}

.main,
#app {
  background-color: #eee;
  overflow: hidden;
  position: relative;
  height: 100%;
  width: 100%;
}

header {
  position: relative;
}

.pageContent {
  position: relative;
  height: 100%;
}
</style>
