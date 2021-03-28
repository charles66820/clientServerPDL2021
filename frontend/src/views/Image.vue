<template>
  <div class="page-wrapper toggled main-panel-container">
    <nav class="sidePanel-wrapper right bg-white border-left shadow">
      <!-- Side panel toggler -->
      <div
        class="btn btn-sm text-body bg-white shadow-sm toggle-sidePanel"
        onclick="this.parentNode.parentNode.classList.toggle('toggled');"
        title="toggle"
      ></div>
      <!-- Panel title -->
      <div class="sidePanel-header border-bottom">
        <span class="text-body mr-4">Algorithms</span>
      </div>
      <!-- Panel content -->
      <div class="sidePanel-content" style="word-wrap: break-word">
        <ul style="list-style-type:none;padding: 0;margin: 0;">
          <li v-for="algo in algos" :key="algo.name">
            <AlgorithmMenuItem :algo="algo" :imageId="parseInt($route.params.id)" :parentComponent="currentComponent" />
          </li>
        </ul>
      </div>
    </nav>
    <div class="page-content" style="word-wrap: break-word">
      <div class="page-wrapper toggled">
        <nav class="sidePanel-wrapper bottom bg-white border-top shadow">
          <!-- Side panel toggler -->
          <div
            class="btn btn-sm text-body bg-white shadow-sm toggle-sidePanel"
            style="padding-top: 0px;"
            onclick="this.parentNode.parentNode.classList.toggle('toggled');"
            title="toggle"
          ></div>
          <!-- Panel title -->
          <div class="sidePanel-header border-bottom">
            <span class="text-body mr-4">Image info</span>
          </div>
          <!-- Panel content -->
          <div class="sidePanel-content" style="word-wrap: break-word">
            <!-- TODO: image info  -->
            <h5 class="title_metadata"> Metadata </h5>
            <ul class="metadata" v-if="image_data != null">
              <li> Id : {{ image_data.id }} </li>
              <li> Name : {{ image_data.name }} </li>
              <li> Type : {{ image_data.type }} </li>
              <li> Size : {{ image_data.size }} </li>
            </ul>
            <!-- Bin and delete request -->
            <button type="button" class="btn btn-outline-dark m-4" id="deleteBtn" data-toggle="modal" data-target="#modalDelete">
              &#128465;
            </button>
            <button type="button" v-if="defaultImageBlob" @click="downloadImage($event)" role="original" class="btn btn-outline-dark m-4" title="Download original image">Download original image 📥</button>
            <button type="button" v-if="processedImageBlob" @click="downloadImage($event)" role="processed" class="btn btn-outline-dark m-4" title="Download processed image">Download processed image 📥</button>
          </div>
        </nav>
        <div class="page-content" style="word-wrap: break-word">
          <div class="imgContainer">
            <!-- Image -->
            <img :src="imageBlob" @error="imagePreviewError($event)" />
          </div>
        </div>
      </div>
    </div>
    <ConfirmDeleteDialog :id="parseInt($route.params.id)"/>
  </div>
</template>

<script>
import AlgorithmMenuItem from "@/components/AlgorithmMenuItem.vue";
import httpApi from "../http-api.js";
import ConfirmDeleteDialog from "@/components/ConfirmDeleteDialog.vue";
export default {
  name: "Image",
  data() {
    return {
      defaultImageBlob: null,
      processedImageBlob: null,
      imageBlob: null,
      currentComponent: this,
      // NODE: example data replace with `algos: []`
      algos: [
        { name: "toto", title: "le Toto 0", args: [ { name: "value 1", title: "the value 1", type: "number", min: 0, max: 255, required: true} ] },
        { name: "increaseLuminosity", title: "increaseLuminosity for test", args: [ { name: "gain", title: "the value 1", type: "number", min: -500, max: 500, required: true} ] },
        { name: "toto2", title: "le Toto 2", args: [ { name: "value 1", title: "the value 1", type: "number", min: 0, max: 255, required: true} ] },
        { name: "toto3", title: "le Toto 3", args: [ { name: "value 1", title: "the value 1", type: "number", min: 0, max: 255, required: true}, { name: "value 2", title: "the value 2", type: "number", min: 0, max: 255, required: true}  ] },
        { name: "toto4", title: "le Toto 4", args: [ { name: "value 1", title: "the value 1", type: "number", min: 0, max: 255, required: false} ] },
        { name: "toto5", title: "le Toto 5", args: [] },
      ],
      image_data: null,
      errors: [],
    }
  },
  components: {
    AlgorithmMenuItem,
    ConfirmDeleteDialog,
  },
  mounted() {
    // Get default image
    httpApi.get_image(this.$route.params.id).then((res) => {
      let reader = new window.FileReader();
      reader.readAsDataURL(res.data);
      reader.addEventListener("load", () => {
        this.defaultImageBlob = reader.result;
        this.imageBlob = reader.result;
      });
    }).catch((err) => this.errors.push(err));

    // Get image metadata
    httpApi
      .get_imageData(this.$route.params.id)
      .then((res) => {
        this.image_data = res.data;
      })
      .catch((err) => this.errors.push(err));

    //get algos from backend
    httpApi
      .get_algos()
      .then((res) => {
        this.algos = res.algo;
      })
      .catch((err) => this.errors.push(err));
  },
  methods: {
    imagePreviewError(e) {
      this.errors.push(new Error('Your browser cannot display : "' + this.image_data.type + '"'))
      this.imageBlob = require("../assets/iconmonstr-picture-1.svg");
      console.log(e);
    },
    downloadImage(e) {
      let role = e.target.getAttribute("role");
      let a = document.createElement("a");
      a.href = role == "processed" ? this.processedImageBlob : this.defaultImageBlob;
      a.download = this.image_data.name;
      a.click();
    },
    showProcessedImage(blob) {
      let reader = new window.FileReader();
      reader.readAsDataURL(blob);
      reader.addEventListener("load", () => {
        this.processedImageBlob = reader.result;
        this.imageBlob = reader.result;
      });
    }
  }
};
</script>

<style scoped>
.title_metadata {
  position: relative;
  text-decoration-line: underline;
  margin-left: 4px;
}

.metadata {
  list-style: circle;
}

#deleteBtn {
  position: absolute;
  top: 2px;
  right: 2px;
}
</style>

<style scoped>
/* For main image */
div.imgContainer {
  position: relative;
  width: 100%;
  height: 100%;
  user-select: none;
  z-index: 1;
}

div.imgContainer * {
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