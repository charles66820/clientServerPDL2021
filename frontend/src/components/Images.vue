<template>
  <section>
    <!-- TODO: carousel -->
    <h2>Image list</h2>
    <ul class="imageList">
      <li v-for="image in images" :key="image.id">
        <a :href="'/image/' + image.id" title="Informations de l'image">
        <span class="imageTitle">{{ image.name }}</span>
        </a>
        <img :src="'/images/' + image.id" />
      </li>
    </ul>
    <ul>
      <li v-for="err in errors" :key="err.type">
        {{ err.message }}
      </li>
    </ul>
  </section>
</template>

<script>
import httpApi from "../http-api.js";
export default {
  name: "Images",
  props: {
    msg: String,
  },
  data() {
    return {
      images: [],
      errors: [],
    };
  },
  methods: {},
  mounted: function () {
    httpApi
      .get_images()
      .then((res) => {
        this.images = res.data;
      })
      .catch((err) => this.errors.push(err));
  },
};
</script>

<style scoped>
/* TODO: carousel css */
ul.imageList {
  list-style-type: none;
  display: flex;
  flex-wrap: wrap;
  margin: -4px;
}
.imageList li {
  position: relative;
  height: 40vh;
  margin: 4px;
}
.imageList li img {
  height: 100%;
}
.imageTitle {
  position: absolute;
  left: 8px;
  color: rgb(0, 0, 0);
  font-size: 1.5em;
  margin: 0;
  font-weight: bold;
  text-decoration: underline;
  -webkit-text-stroke: 0.8px rgb(255, 255, 255);
}
@media (max-aspect-ratio: 1/1) {
  .imageList li {
    max-height: 30vh;
    overflow: auto;
  }
}
@media (max-height: 480px) {
  .imageList li {
    height: 80vh;
  }
}
</style>
