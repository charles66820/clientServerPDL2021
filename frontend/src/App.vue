<template>
  <div class="main">
    <header>
      <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <a class="navbar-brand" href="#">Image manager ?</a>
        <button
          class="navbar-toggler"
          type="button"
          data-toggle="collapse"
          data-target="#mainNavbarToggler"
          aria-controls="mainNavbarToggler"
          aria-expanded="false"
          aria-label="Toggle navigation"
        >
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="mainNavbarToggler">
          <ul class="navbar-nav mr-auto mt-2 mt-lg-0">
            <li class="nav-item">
              <router-link class="nav-link" to="/">Home</router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/about">About</router-link>
            </li>
            <li class="nav-item">
              <button
                class="btn btn-sm nav-link"
                type="button"
                data-toggle="modal"
                data-target="#uploadImageModal"
              >
                Upload new image
              </button>
            </li>
          </ul>
        </div>
      </nav>
    </header>
    <router-view class="pageContent" />
    <UploadImage />
  </div>
</template>

<script>
import UploadImage from "@/components/UploadImage.vue";

export default {
  name: "App",
  components: {
    UploadImage,
  },
};
</script>

<style>
body {
  top: 0;
  bottom: 0;
  position: absolute;
  height: 100%;
  width: 100%;
  margin: 0;
  padding: 0;
}

.main,
#app {
  background-color: #eee;
  position: relative;
  height: 100%;
  width: 100%;
}

header {
  position: relative;
}

.pageContent {
  position: relative;
}
</style>
