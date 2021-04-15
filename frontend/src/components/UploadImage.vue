<template>
  <div>
    <div
      class="modal"
      id="uploadImageModal"
      tabindex="-1"
      aria-labelledby="uploadImageLabel"
      aria-hidden="true"
      v-on:click.capture="modalClosed"
    >
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title" id="uploadImageLabel">
                {{ t("components.uploadImage.title") }}
              </h5>
              <button
                type="button"
                class="close"
                data-dismiss="modal"
                @click="modalClosed"
                aria-label="Close"
              >
                <span aria-hidden="true">&times;</span>
              </button>
            </div>
            <form method="post" @submit="imageSubmit($event)">
              <div class="modal-body">
                <div
                  class="alert alert-warning alert-dismissible fade show"
                  v-if="warning"
                  role="alert"
                >
                  <strong>{{ t("warnings.title") }} !</strong>
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
                  class="alert alert-danger alert-dismissible fade show"
                  v-if="error"
                  role="alert"
                >
                  <strong>{{ t("errors.title") }} :</strong>
                  {{ getErrorMsg(error) }}
                </div>
                <div
                  class="imgContainer init"
                  @dragleave="dropAreaDragLeave($event)"
                  @dragover="dropAreaDragOver($event)"
                  @drop="dropAreaDrop($event)"
                  @click="dropAreaClicked()"
                >
                  <span>{{ t("components.uploadImage.content") }}</span>
                  <img
                    id="imagePreview"
                    :src="null"
                    @error="imagePreviewError($event)"
                  />
                  <input
                    type="file"
                    name="image"
                    @change="imgChange($event)"
                    style="visibility: hidden"
                  />
                </div>
                <div class="progress" v-if="uploadProgress != null">
                  <div
                    class="progress-bar progress-bar-striped progress-bar-animated"
                    role="progressbar"
                    :style="`width: ${uploadProgress}%`"
                    :aria-valuenow="uploadProgress"
                    aria-valuemin="0"
                    aria-valuemax="100"
                  >
                    {{ uploadProgress.toFixed(0) }}%
                  </div>
                </div>
              </div>
              <div class="modal-footer">
                <button
                  type="submit"
                  class="btn btn-primary"
                  :disabled="loading"
                >
                  {{ t("components.uploadImage.submit") }}
                  <span
                    v-if="loading"
                    class="spinner-border spinner-border-sm"
                    role="status"
                    aria-hidden="true"
                  ></span>
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { useI18n } from "vue-i18n";
import emitter from "tiny-emitter/instance";
import httpApi from "../http-api.js";
import { getErrorMsg } from "../i18n";

export default {
  name: "App",
  data() {
    return {
      t: useI18n({ useScope: "global" }).t,
      getErrorMsg,
      loading: false,
      uploadProgress: null,
      imageType: "",
      error: null,
      warning: null,
    };
  },
  methods: {
    imageSubmit(e) {
      e.preventDefault();

      this.loading = true;
      this.uploadProgress = 0;
      this.error = null;

      let imageFiles = e.target["image"].files;
      let image = imageFiles && imageFiles.length > 0 ? imageFiles[0] : null;
      httpApi
        .post_image(image, (e) => {
          this.uploadProgress = (e.loaded * 100) / e.total;
        })
        .then((res) => {
          this.loading = false;
          this.uploadProgress = null;
          // Close modal
          document.querySelector("#btnUploadImageModal").click();

          // Reset image form
          e.target.reset();
          let imagePreview = document.querySelector("#imagePreview");
          imagePreview.parentElement.classList.add("init");
          imagePreview.removeAttribute("src");

          // Update or redirect the component
          if (this.$route.name == "Image") {
            this.$router
              .push({ name: "Image", params: { id: res.data.id } })
              .then(() => {
                emitter.emit("updateImage");
              });
          } else if (this.$route.name == "Home") {
            emitter.emit("updateImages");
          } else this.$router.push({ name: "Home" });
        })
        .catch((err) => {
          this.loading = false;
          this.uploadProgress = null;
          this.error = err;
          // Reset image form
          e.target.reset();
          let imagePreview = document.querySelector("#imagePreview");
          imagePreview.parentElement.classList.add("init");
          imagePreview.removeAttribute("src");
        });
    },
    dropAreaDragLeave(e) {
      e.target.classList.remove("dragover");
    },
    dropAreaDragOver(e) {
      e.preventDefault();
      e.target.classList.add("dragover");
      return false;
    },
    dropAreaClicked() {
      document.querySelector(".imgContainer > input[name=image]").click();
    },
    dropAreaDrop(e) {
      // Clear drop zone css
      e.target.classList.remove("dragover");
      e.target.classList.remove("init");
      e.preventDefault();

      // Add image to image input
      let imgInput = document.querySelector(
        ".imgContainer > input[name=image]"
      );
      imgInput.files = e.dataTransfer.files;
      if (imgInput.files && imgInput.files[0]) {
        // Add image to image preview
        this.imageType = imgInput.files[0].type;
        renderFile(imgInput.files[0], document.querySelector("#imagePreview"));
      }
      e.stopPropagation();
    },
    imgChange(e) {
      if (e.target.files && e.target.files[0]) {
        this.imageType = e.target.files[0].type;
        e.target.parentElement.classList.remove("init");
        renderFile(e.target.files[0], document.querySelector("#imagePreview"));
      }
    },
    imagePreviewError() {
      if (this.imageType != "") {
        this.warning = new Error(
          this.t("warnings.unsupportedImage") + ` : "${this.imageType}"`
        );
        document.querySelector(
          "#imagePreview"
        ).src = require("../assets/iconmonstr-picture-1.svg");
      }
    },
    modalClosed() {
      this.error = null;
    },
  },
};
function renderFile(file, elem) {
  let reader = new FileReader();
  reader.addEventListener("load", () => (elem.src = reader.result));

  reader.readAsDataURL(file);
}
</script>

<style scoped>
.imgContainer {
  height: 400px;
  width: 400px;
  position: relative;
  user-select: none;
  border: dashed 4px rgba(0, 0, 0, 0);
  border-radius: 8px;
  margin: auto;
}

.imgContainer.init {
  border: dashed 4px rgba(0, 0, 0, 1);
}

.imgContainer span {
  margin: 0;
  position: absolute;
  pointer-events: none;
  top: 50%;
  width: 100%;
  text-align: center;
  visibility: hidden;
}

.imgContainer.init span {
  visibility: visible;
}

#imagePreview {
  max-width: 100%;
  max-height: 100%;
  position: absolute;
  pointer-events: none;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  margin: auto;
}

.dragover {
  border: solid 4px rgba(0, 120, 255, 1);
  background-color: rgba(150, 210, 255, 0.5);
}

.imgContainer.dragover span {
  visibility: visible;
}

@media screen and (max-width: 777px) {
  .imgContainer {
    width: 100%;
  }
}
</style>