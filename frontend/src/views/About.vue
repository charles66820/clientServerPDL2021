<template>
  <div>
    <h1>{{ t("components.about.title") }}</h1>
    <!-- TODO: about -->
  </div>
</template>

<script>
import { useI18n } from "vue-i18n";

export default {
  name: "About",
  data() {
    return {
      t: useI18n({ useScope: "global" }).t,
    };
  }
};
</script>
