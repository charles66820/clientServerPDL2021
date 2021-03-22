<template>
  <div class="page-wrapper toggled main">
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
        <!-- TODO: algorithms list  -->
      </div>
    </nav>
    <div class="page-content" style="word-wrap: break-word">
      <div class="page-wrapper toggled">
        <nav class="sidePanel-wrapper bottom bg-white border-top shadow">
          <!-- Side panel toggler -->
          <div
            class="btn btn-sm text-body bg-white shadow-sm toggle-sidePanel"
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
          </div>
        </nav>
        <div class="page-content" style="word-wrap: break-word">
          <div class="imgContainer">
            <!-- TODO: image -->
            <img src="https://free4kwallpapers.com/uploads/originals/2020/09/04/neon-background--wallpaper.jpg" alt="Image">
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Image",
};
</script>

<style scoped>
div.imgContainer {
  position: relative;
  height: 100%;
  user-select: none;
  z-index: 1;
}

div.imgContainer * {
  max-width: 100%;
  max-height: 100%;
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  margin: auto;
}
</style>

<style scoped>
.page-wrapper {
  position: relative;
  overflow: hidden;
  height: 100%;
  width: 100%;
}

.page-wrapper.main {
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
  height: 100%;
  top: 0;
}

.sidePanel-wrapper.top,
.sidePanel-wrapper.bottom {
  width: 100%;
  height: 360px;
  left: 0;
}

.sidePanel-wrapper.left {
  left: -260px;
}

.sidePanel-wrapper.right {
  right: -260px;
}

.sidePanel-wrapper.top {
  top: -360px;
}

.sidePanel-wrapper.bottom {
  bottom: -360px;
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
    padding-top: 360px;
  }

  .page-wrapper.toggled > .sidePanel-wrapper.bottom + .page-content {
    padding-bottom: 360px;
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