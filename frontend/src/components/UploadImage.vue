<template>
  <div>
    <div
      class="modal"
      id="uploadImageModal"
      tabindex="-1"
      aria-labelledby="upload an new image modal"
      aria-hidden="true"
    >
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title" id="staticBackdropLabel">
                Upload an image
              </h5>
              <button
                type="button"
                class="close"
                data-dismiss="modal"
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
                  <strong>Warning !</strong> {{ warning.message }}
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
                  v-if="error"
                  role="alert"
                >
                  <strong>Error :</strong> {{ getErrorMsg(error) }}
                </div>
                <div
                  class="imgContainer init"
                  @dragleave="dropAreaDragLeave($event)"
                  @dragover="dropAreaDragOver($event)"
                  @drop="dropAreaDrop($event)"
                  @click="dropAreaClicked()"
                >
                  <span>Drop an image here or click here to choose one.</span>
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
              </div>
              <div class="modal-footer">
                <button type="submit">Submit</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import httpApi from "../http-api.js";
export default {
  name: "App",
  data() {
    return {
      imageType: "",
      error: null,
      warning: null,
    };
  },
  methods: {
    imageSubmit(e) {
      e.preventDefault();

      this.error = null;

      let imageFiles = e.target["image"].files;
      let image = imageFiles && imageFiles.length > 0 ? imageFiles[0] : null;
      //console.log(image);
      // TODO: call POST /images
      httpApi
        .post_image(image)
        .then((res) => {
          imageFiles.push({ image: res });
        })
        .catch((err) => (this.error = err));
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
      e.target.classList.remove("dragover");
      e.target.classList.remove("init");
      e.preventDefault();
      let imgInput = document.querySelector(
        ".imgContainer > input[name=image]"
      );
      imgInput.files = e.dataTransfer.files;
      if (imgInput.files && imgInput.files[0]) {
        this.imageType = imgInput.files[0].type;
        renderFile(imgInput.files[0], document.querySelector("#imagePreview"));
      }
      e.stopPropagation();
    },
    imgChange(e) {
      if (e.target.files && e.target.files[0])
        renderFile(e.target.files[0], document.querySelector("#imagePreview"));
    },
    imagePreviewError() {
      this.warning = new Error(
        'Your browser cannot display : "' + this.imageType + '"'
      );
      document.querySelector(
        "#imagePreview"
      ).src = require("../assets/iconmonstr-picture-1.svg");
    },
    getErrorMsg(err) {
      return err.response.data.type == "text/plain"
        ? err.response.data
        : err.message;
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