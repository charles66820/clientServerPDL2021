<template>
  <div>
    <h1>{{ $t("components.about.title") }}</h1>
    <p>{{ $t("components.about.content1") }}</p>
    <p>{{ $t("components.about.content2") }}</p>
    <p>{{ $t("components.about.content3") }}</p>
    <p>{{ $t("components.about.content4") }}</p>
    <p>{{ $t("components.about.content5") }}</p>
    <p>{{ $t("components.about.content6") }}</p>
  </div>
</template>

<script>
export default {
  name: "About",
  mounted() {
    document.title = this.$t("titles.about");
  }
};
</script>

<style scoped>
h1 {
  margin-left: 10px;
  margin-bottom: 2rem;
}

p {
  margin-left: 10px;
  margin-right: 50px;
  font-size: large;
  text-align: center;
}
</style>