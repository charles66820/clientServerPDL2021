<template>
  <div class="page-wrapper toggled main-panel-container">
    <nav class="sidePanel-wrapper left bg-white border-left shadow">
      <!-- Side panel toggler -->
      <button
        class="btn btn-sm text-body bg-white shadow-sm toggle-sidePanel"
        onclick="this.parentNode.parentNode.classList.toggle('toggled');"
        :title="$t('components.image.toggleAlgorithms')"
      ></button>
      <!-- Panel title -->
      <div class="sidePanel-header border-bottom">
        <span class="text-body mr-4">{{
          $t("components.image.algorithmsTitle")
        }}</span>
      </div>
      <!-- Panel content -->
      <div class="sidePanel-content" style="word-wrap: break-word">
        <ul
          v-if="!algosLoading"
          style="list-style-type: none; padding: 0; margin: 0"
        >
          <li v-for="algo in algos" :key="algo.name">
            <AlgorithmMenuItem
              :algo="algo"
              :imageId="parseInt($route.params.id)"
            />
          </li>
          <div
            class="alert alert-danger alert-dismissible fade show"
            v-if="algorithmsError"
            role="alert"
          >
            <strong>{{ $t("errors.title") }} :</strong>
            {{ getErrorMsg(algorithmsError) }}
          </div>
        </ul>
        <div
          v-else
          class="h-100 w-100 d-flex justify-content-center align-items-center"
        >
          <div
            class="spinner-border"
            style="width: 3rem; height: 3rem"
            role="status"
          >
            <span class="sr-only">{{ $t("loading") }}</span>
          </div>
        </div>
      </div>
    </nav>
    <div class="page-content" style="word-wrap: break-word">
      <div class="page-wrapper toggled">
        <nav class="sidePanel-wrapper bottom bg-white border-top shadow">
          <!-- Side panel toggler -->
          <button
            class="btn btn-sm text-body bg-white shadow-sm toggle-sidePanel"
            style="padding-top: 0px"
            onclick="this.parentNode.parentNode.classList.toggle('toggled');"
            :title="$t('components.image.toggleMetadata')"
          ></button>
          <!-- Panel title -->
          <div class="sidePanel-header border-bottom">
            <span class="text-body mr-4">{{
              $t("components.image.metadataTitle")
            }}</span>
          </div>
          <!-- Panel content -->
          <div
            class="sidePanel-content h-100 overflow-auto"
            style="word-wrap: break-word"
          >
            <div
              v-if="image_data == null"
              class="h-100 w-100 d-flex justify-content-center align-items-center"
            >
              <div
                class="spinner-border"
                style="width: 3rem; height: 3rem"
                role="status"
              >
                <span class="sr-only">{{ $t("loading") }}</span>
              </div>
            </div>
            <span v-else>
              <div
                class="alert alert-danger alert-dismissible fade show"
                v-if="imageDataError"
                role="alert"
              >
                <strong>{{ $t("errors.title") }} :</strong>
                {{ getErrorMsg(imageDataError) }}
              </div>
              <div class="row pb-4">
                <div class="col">
                  <h5 class="title_metadata">
                    {{ $t("components.image.metadata.title") }}
                  </h5>
                  <ul class="metadata" v-if="image_data != null">
                    <li>
                      {{ $t("components.image.metadata.id") }} :
                      {{ image_data.id }}
                    </li>
                    <li>
                      {{ $t("components.image.metadata.name") }} :
                      {{ image_data.name }}
                    </li>
                    <li>
                      {{ $t("components.image.metadata.type") }} :
                      {{ image_data.type }}
                    </li>
                    <li>
                      {{ $t("components.image.metadata.size") }} :
                      {{ image_data.size }}
                    </li>
                  </ul>
                </div>
                <div class="col"></div>
                <div class="col text-right">
                  <!-- Bin and download buttons -->
                  <p class="m-0">
                    <button
                      type="button"
                      class="btn btn-outline-dark mx-4 mt-4"
                      data-toggle="modal"
                      data-target="#modalDelete"
                      :title="$t('components.image.metadata.removeImage')"
                    >
                      &#128465;
                    </button>
                  </p>
                  <p class="m-0">
                    <button
                      type="button"
                      v-if="defaultImageBlob"
                      @click="downloadImage($event)"
                      role="original"
                      class="btn btn-outline-dark mx-4 mt-4"
                      :title="
                        $t('components.image.metadata.downloadOriginalImage')
                      "
                    >
                      {{
                        $t("components.image.metadata.downloadOriginalImage")
                      }}
                      📥
                    </button>
                  </p>
                  <p class="m-0">
                    <button
                      type="button"
                      v-if="processedImageBlob"
                      @click="downloadImage($event)"
                      role="processed"
                      class="btn btn-outline-dark mx-4 mt-4"
                      :title="
                        $t('components.image.metadata.downloadProcessedImage')
                      "
                    >
                      {{
                        $t("components.image.metadata.downloadProcessedImage")
                      }}
                      📥
                    </button>
                  </p>
                </div>
              </div>
            </span>
          </div>
        </nav>
        <div class="page-content" style="word-wrap: break-word">
          <div class="h-100" style="width: calc(100% - 16px)">
            <div
              class="alert alert-warning alert-dismissible fade show w-100 mx-2"
              v-if="warning"
              role="alert"
            >
              <strong>{{ $t("warnings.title") }} !</strong>
              {{ warning.message }}
              <button
                type="button"
                class="close"
                @click="warning = null"
                aria-label="Close"
              >
                <span aria-hidden="true">&times;</span>
              </button>
            </div>
            <div
              class="alert alert-danger alert-dismissible fade show w-100 mx-2"
              v-if="imageError"
              role="alert"
            >
              <strong>{{ $t("errors.title") }} :</strong>
              {{ getErrorMsg(imageError) }}
            </div>
            <div
              :class="
                processedImageBlob != null
                  ? 'imgContainer col-sm-6 col-12'
                  : 'imgContainer col-12'
              "
            >
              <!-- Image -->
              <img
                v-if="defaultImageBlob != null"
                :src="defaultImagePreview"
                @error="imagePreviewError($event, true)"
                :alt="$t('loading')"
              />
              <div
                v-else
                class="spinner-border"
                style="width: 3rem; height: 3rem"
                role="status"
              >
                <span class="sr-only">{{ $t("loading") }}</span>
              </div>
            </div>
            <div
              class="imgContainer col-sm-6 col-12"
              v-if="processedImageBlob != null"
            >
              <!-- Image -->
              <img
                v-if="processedImageBlob != null"
                :src="processedImagePreview"
                @error="imagePreviewError($event, false)"
                :alt="$t('loading')"
              />
              <div
                v-else
                class="spinner-border"
                style="width: 3rem; height: 3rem"
                role="status"
              >
                <span class="sr-only">{{ $t("loading") }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <ConfirmDeleteDialog :id="parseInt($route.params.id)" />
  </div>
</template>

<script>
import AlgorithmMenuItem from "@/components/AlgorithmMenuItem.vue";
import ConfirmDeleteDialog from "@/components/ConfirmDeleteDialog.vue";
import emitter from "tiny-emitter/instance";
import httpApi from "../http-api.js";
import { getErrorMsg } from "../i18n";

export default {
  name: "Image",
  data() {
    return {
      getErrorMsg,
      defaultImageBlob: null,
      processedImageBlob: null,
      defaultImagePreview: null,
      processedImagePreview: null,
      algosLoading: false,
      algos: [],
      image_data: null,
      imageError: null,
      imageDataError: null,
      algorithmsError: null,
      warning: null,
    };
  },
  components: {
    AlgorithmMenuItem,
    ConfirmDeleteDialog,
  },
  mounted() {
    document.title = this.$t("titles.image");
    emitter.on("updateImage", () => {
      this.defaultImageBlob = null;
      this.processedImageBlob = null;
      this.defaultImagePreview = null;
      this.processedImagePreview = null;
      this.image_data = null;
      this.imageError = null;
      this.imageDataError = null;
      this.warning = null;
      this.loadImage();
    });
    this.loadImage();

    this.$parent.$parent.addUpdateLangListener(this.loadAlgorithms);
    this.loadAlgorithms();
  },
  methods: {
    loadImage() {
      // Get default image
      httpApi
        .get_image(this.$route.params.id)
        .then((res) => {
          let reader = new window.FileReader();
          reader.readAsDataURL(res.data);
          reader.addEventListener("load", () => {
            this.defaultImageBlob = reader.result;
            this.defaultImagePreview = this.defaultImageBlob;
          });
        })
        .catch((err) => {
          if (err.response.status == 404) this.$router.push({ name: "Home" });
          this.imageError = err;
        });

      // Get image metadata
      httpApi
        .get_imageData(this.$route.params.id)
        .then((res) => {
          this.image_data = res.data;
          document.title =
            this.$t("titles.image") + " | " + this.image_data.name;
        })
        .catch((err) => (this.imageDataError = err));
    },
    loadAlgorithms() {
      // Get algos from backend
      this.algosLoading = true;
      httpApi
        .get_algos()
        .then((res) => {
          this.algos = res.data;
          this.algosLoading = false;
        })
        .catch((err) => {
          this.algorithmsError = err;
          this.algosLoading = false;
        });
    },
    imagePreviewError(e, isDefault) {
      console.info(e);
      if (this.image_data != null) {
        this.warning = new Error(
          this.$t("warnings.unsupportedImage") + ` : "${this.image_data.type}"`
        );
        if (isDefault)
          this.defaultImagePreview = require("../assets/iconmonstr-picture-1.svg");
        else
          this.processedImagePreview = require("../assets/iconmonstr-picture-1.svg");
      }
    },
    downloadImage(e) {
      let role = e.target.getAttribute("role");
      let a = document.createElement("a");
      a.href =
        role == "processed" ? this.processedImageBlob : this.defaultImageBlob;
      a.download = this.image_data.name;
      a.click();
    },
    showProcessedImage(blob) {
      let reader = new window.FileReader();
      reader.readAsDataURL(blob);
      reader.addEventListener("load", () => {
        this.processedImageBlob = reader.result;
        this.processedImagePreview = this.processedImageBlob;
      });
    },
  },
};
</script>

<style scoped>
.title_metadata {
  position: relative;
  text-decoration-line: underline;
  margin-left: 8px;
  margin-top: 8px;
}

.metadata {
  list-style: circle;
}
</style>

<style scoped>
/* For main image */
div.imgContainer {
  position: relative;
  display: inline-block;
  margin: 0 4px;
  height: 100%;
  user-select: none;
  z-index: 1;
}

@media (min-width: 576px) {
  div.imgContainer.col-sm-6 {
    -ms-flex: 0 0 50%;
    flex: 0 0 50%;
    max-width: calc(50% - 8px);
  }
}

div.imgContainer img,
div.imgContainer div.spinner-border {
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
}
</style>

<style scoped>
/* For panel */
.page-wrapper {
  position: relative;
  overflow: hidden;
  height: 100%;
  width: 100%;
}

.page-wrapper.main-panel-container {
  height: calc(100% - 56px);
}

.page-wrapper .page-content {
  display: inline-block;
  overflow-x: hidden;
  width: 100%;
  height: 100%;
  padding: 0px;
  z-index: 0;
}

.page-wrapper .sidePanel-wrapper {
  position: absolute;
  z-index: 100;
}

.sidePanel-wrapper.left,
.sidePanel-wrapper.right {
  width: 260px;
  height: calc(100% - 40px);
  top: 0;
}

.sidePanel-wrapper.top,
.sidePanel-wrapper.bottom {
  width: 100%;
  height: 38vh;
  left: 0;
}

.sidePanel-wrapper.left {
  left: -260px;
}

.sidePanel-wrapper.right {
  right: -260px;
}

.sidePanel-wrapper.top {
  top: -38vh;
}

.sidePanel-wrapper.bottom {
  bottom: -38vh;
}

.page-wrapper.toggled > .sidePanel-wrapper.left {
  left: 0px;
}

.page-wrapper.toggled > .sidePanel-wrapper.right {
  right: 0px;
}

.page-wrapper.toggled > .sidePanel-wrapper.top {
  top: 0px;
}

.page-wrapper.toggled > .sidePanel-wrapper.bottom {
  bottom: 0px;
}

@media screen and (min-width: 768px) {
  .page-wrapper.toggled > .sidePanel-wrapper.left + .page-content {
    padding-left: 260px;
  }

  .page-wrapper.toggled > .sidePanel-wrapper.right + .page-content {
    padding-right: 260px;
  }

  .page-wrapper.toggled > .sidePanel-wrapper.top + .page-content {
    padding-top: 38vh;
  }

  .page-wrapper.toggled > .sidePanel-wrapper.bottom + .page-content {
    padding-bottom: 38vh;
  }
}

.toggle-sidePanel {
  position: absolute;
  cursor: pointer;
  z-index: 1000;
}

.sidePanel-wrapper.left .toggle-sidePanel {
  border-radius: 0 4px 4px 0;
  border-right: 1px solid gray !important;
  border-top: 1px solid gray !important;
  border-bottom: 1px solid gray !important;
  width: 24px;
  top: 10px;
  right: -24px;
}

.sidePanel-wrapper.right .toggle-sidePanel {
  border-radius: 4px 0 0 4px;
  border-left: 1px solid gray !important;
  border-top: 1px solid gray !important;
  border-bottom: 1px solid gray !important;
  width: 24px;
  top: 10px;
  left: -24px;
}

.sidePanel-wrapper.top .toggle-sidePanel {
  border-radius: 0 0 4px 4px;
  border-left: 1px solid gray !important;
  border-right: 1px solid gray !important;
  border-bottom: 1px solid gray !important;
  height: 24px;
  left: 10px;
  bottom: -24px;
}

.sidePanel-wrapper.bottom .toggle-sidePanel {
  border-radius: 4px 4px 0 0;
  border-left: 1px solid gray !important;
  border-right: 1px solid gray !important;
  border-top: 1px solid gray !important;
  height: 24px;
  left: 10px;
  top: -24px;
}

.sidePanel-wrapper.left .toggle-sidePanel::after,
.sidePanel-wrapper.right .toggle-sidePanel::after {
  font-weight: bold;
  font-size: 1.4em;
}

.sidePanel-wrapper.top .toggle-sidePanel::after,
.sidePanel-wrapper.bottom .toggle-sidePanel::after {
  font-weight: bold;
  font-size: 1.8em;
}

.sidePanel-wrapper.left .toggle-sidePanel::after {
  content: ">";
}

.sidePanel-wrapper.right .toggle-sidePanel::after {
  content: "<";
}

.sidePanel-wrapper.top .toggle-sidePanel::after {
  content: "˅";
}

.sidePanel-wrapper.bottom .toggle-sidePanel::after {
  content: "˄";
}

.page-wrapper.toggled > .sidePanel-wrapper.left .toggle-sidePanel::after {
  content: "<";
}

.page-wrapper.toggled > .sidePanel-wrapper.right .toggle-sidePanel::after {
  content: ">";
}

.page-wrapper.toggled > .sidePanel-wrapper.top .toggle-sidePanel::after {
  content: "˄";
}

.page-wrapper.toggled > .sidePanel-wrapper.bottom .toggle-sidePanel::after {
  content: "˅";
}

.sidePanel-wrapper .sidePanel-header {
  padding: 10px 20px;
  align-items: center;
}

.sidePanel-wrapper .sidePanel-header > a,
.sidePanel-wrapper .sidePanel-header > span {
  font-weight: bold;
}

.sidePanel-content {
  overflow-y: auto;
  position: relative;
}

.sidePanel-wrapper.left .sidePanel-content,
.sidePanel-wrapper.right .sidePanel-content {
  height: 100%;
}

.sidePanel-wrapper.top .sidePanel-content,
.sidePanel-wrapper.bottom .sidePanel-content {
  width: 100%;
}

.page-wrapper .sidePanel-wrapper,
.page-wrapper .page-content {
  -webkit-transition: all 0.3s ease;
  -moz-transition: all 0.3s ease;
  -ms-transition: all 0.3s ease;
  -o-transition: all 0.3s ease;
  transition: all 0.3s ease;
}
</style>