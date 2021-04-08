<template>
  <div class="main-container pb-4">
    <div class="container pb-4">
      <h2 class="mt-4">
        {{ t("components.images.title") }}
      </h2>
      <div>
        <div
          class="alert alert-warning alert-dismissible fade show"
          v-if="warning"
          :key="warning.type"
          role="alert"
        >
          <strong>{{ t("warnings.title") }} !</strong> {{ warning.message }}
          <button
            type="button"
            class="close"
            data-dismiss="alert"
            aria-label="Close"
          >
            <span aria-hidden="true">&times;</span>
          </button>
        </div>
        <div
          class="alert alert-danger alert-dismissible fade show"
          v-for="err in errors"
          :key="err.type"
          role="alert"
        >
          <strong>{{ t("errors.title") }} :</strong> {{ getErrorMsg(err) }}
        </div>
      </div>
      <div v-if="images.length > 0" class="imageCarousel shadow my-4">
        <div class="imagesContainer imgContainer">
          <router-link
            v-if="selectedImage"
            class="imageTitle"
            :to="{ name: 'Image', params: { id: selectedImage.id } }"
            :title="t('components.images.actionLbl')"
            >{{ selectedImage.name }}
            <button class="btn btn-primary imageAction">
              {{ t("components.images.actionBtn") }}
            </button>
          </router-link>
          <img
            v-if="selectedImage"
            :src="selectedImage.blob"
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
            <img :src="image.blob" @error="imageViewError($event, image)" />
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
import emitter from "tiny-emitter/instance";
import { useI18n } from "vue-i18n";
import httpApi from "../http-api.js";

export default {
  name: "Images",
  props: {
    msg: String,
  },
  data() {
    return {
      t: useI18n({ useScope: "global" }).t,
      selectedImage: null,
      images: [],
      errors: [],
      warning: null,
    };
  },
  methods: {
    loadImages() {
      httpApi
        .get_images()
        .then((res) => {
          this.images = res.data;
          if (this.images.length > 0) this.selectedImage = this.images[0];
          for (let image of this.images) {
            httpApi.get_image(image.id).then((res) => {
              let reader = new window.FileReader();
              reader.readAsDataURL(res.data);
              reader.addEventListener(
                "load",
                () => (image.blob = reader.result)
              );
            });
          }
        })
        .catch((err) => this.errors.push(err));
    },
    selectImage(e) {
      this.selectedImage = this.images.find((i) => i.id == e.target.dataset.id);
    },
    imageViewError(e, image) {
      this.warning = new Error(
        this.t("warnings.unsupportedImage") + ` : "${image.type}"`
      );
      e.target.src = require("../assets/iconmonstr-picture-1.svg");
    },
    getErrorMsg(err) {
      return err.response != null &&
        err.response.headers["content-type"] == "text/plain"
        ? err.response.data
        : err.message;
    },
  },
  mounted() {
    emitter.on("updateImages", this.loadImages);
    this.loadImages();
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
  z-index: 1;
}

.imageAction {
  position: absolute;
  top: 8px;
  right: 8px;
  left: initial;
  bottom: initial;
  font-weight: initial;
  text-decoration: none;
  -webkit-text-stroke: 0;
  pointer-events: initial;
  z-index: 1;
}
</style>
