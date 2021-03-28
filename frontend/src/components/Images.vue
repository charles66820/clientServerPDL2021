<template>
  <div class="main-container">
    <div class="container">
      <h2>Image list</h2>
      <div>
        <div
          class="alert alert-warning alert-dismissible fade show"
          v-for="err in errors"
          :key="err.type"
          role="alert"
        >
          <strong>Error!</strong> {{ err.message }}
          <button
            type="button"
            class="close"
            data-dismiss="alert"
            aria-label="Close"
          >
            <span aria-hidden="true">&times;</span>
          </button>
        </div>
      </div>
      <p>Click on image for more action</p>
      <div v-if="images.length > 0" class="imageCarousel shadow my-4">
        <div class="imagesContainer imgContainer">
          <!-- title="Informations de l'image" -->
          <router-link
            v-if="selectedImage"
            class="imageTitle"
            :to="{ name: 'Image', params: { id: selectedImage.id } }"
            title="Show more image info"
            >{{ selectedImage.name }}</router-link
          >
          <img
            v-if="selectedImage"
            :src="'/images/' + selectedImage.id"
            @error="imageViewError($event, selectedImage)"
          />
        </div>
        <hr class="m-0" />
        <ul class="imageList">
          <li
            class="imgContainer"
            v-for="image in images"
            :key="image.id"
            :data-id="image.id"
            @click="selectImage($event)"
          >
            <img
              :src="'/images/' + image.id"
              @error="imageViewError($event, image)"
            />
          </li>
        </ul>
      </div>
    </div>
  </div>
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
      selectedImage: null,
      images: [],
      errors: [],
    };
  },
  methods: {
    selectImage(e) {
      this.selectedImage = this.images.find((i) => i.id == e.target.dataset.id);
    },
    imageViewError(e, image) {
      this.errors.push(
        new Error('Your browser cannot display : "' + image.type + '"')
      );
      e.target.src = require("../assets/iconmonstr-picture-1.svg");
      console.log(e);
    },
  },
  mounted() {
    httpApi
      .get_images()
      .then((res) => {
        this.images = res.data;
        if (this.images.length > 0) this.selectedImage = this.images[0];
      })
      .catch((err) => this.errors.push(err));
  },
};
</script>

<style scoped>
* {
  box-sizing: border-box;
}

.main-container {
  height: 100%;
  position: relative;
  overflow: auto;
}

.imageCarousel {
  width: 100%;
  height: 600px;
  position: relative;
  border-radius: 12px;
  overflow: hidden;
}

.imageCarousel > .imagesContainer {
  height: calc(100% - 124px);
}

.imageCarousel > .imageList {
  width: 100%;
  height: 120px;
  overflow-y: hidden;
  overflow-x: auto;
  white-space: nowrap;
  list-style-type: none;
  position: absolute;
  bottom: 0;
  left: 0;
  padding: 0;
  padding-bottom: 8px;
  margin: 0;
}

.imageCarousel > .imageList > * {
  width: 100px;
  height: 100px;
  display: inline-block;
  margin: 0 8px;
  padding: 0;
}

.imgContainer {
  position: relative;
  width: 100%;
  height: 100%;
  user-select: none;
}

.imgContainer * {
  max-width: 100%;
  max-height: 100%;
  height: auto;
  width: auto;
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  margin: auto;
  pointer-events: none;
}

.imageTitle {
  position: absolute;
  left: 8px;
  color: rgb(0, 0, 0);
  font-size: 1.5em;
  margin: 0;
  font-weight: bold;
  text-decoration: underline;
  text-decoration-color: rgb(0, 0, 0);
  -webkit-text-stroke: 0.8px rgb(255, 255, 255);
  pointer-events: initial;
}
</style>
