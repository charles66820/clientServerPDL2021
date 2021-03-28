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
                  class="imgContainer"
                  @dragleave="dropAreaDragLeave($event)"
                  @dragover="dropAreaDragOver($event)"
                  @drop="dropAreaDrop($event)"
                  @click="dropAreaClicked()"
                >
                  <span>Drop an image here or click here to choose one.</span>
                  <img id="imagePreview" :src="null" />
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
      loading: false,
      error: null,
    };
  },
  methods: {
    imageSubmit(e) {
      e.preventDefault();

      this.error = null;

      let imageFiles = e.target["image"].files;
      let image = imageFiles && imageFiles.length > 0 ? imageFiles[0] : null;
      console.log(image);
      // TODO: call POST /images
      httpApi
       .post_image(image)
       .then((res) => {
         // TO DO
       })
       .catch((err) => this.errors.push(err));
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
      e.preventDefault();
      let imgInput = document.querySelector(
        ".imgContainer > input[name=image]"
      );
      imgInput.files = e.dataTransfer.files;
      if (imgInput.files && imgInput.files[0])
        renderFile(imgInput.files[0], document.querySelector("#imagePreview"));
      e.stopPropagation();
    },
    imgChange(e) {
      if (e.target.files && e.target.files[0])
        renderFile(e.target.files[0], document.querySelector("#imagePreview"));
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
  border: dashed 4px rgba(0, 0, 0, 1);
  border-radius: 8px;
}

.imgContainer span {
  margin: 0;
  position: absolute;
  top: 50%;
  width: 100%;
  text-align: center;
}

#imagePreview {
  max-width: 100%;
  max-height: 100%;
  position: absolute;
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

@media screen and (max-width: 777px) {
  .imgContainer {
    width: 100%;
  }
}
</style>