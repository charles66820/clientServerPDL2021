<template>
  <div class="main">
    <header>
      <nav>
        <router-link to="/">Home</router-link> |
        <router-link to="/about">About</router-link>
      </nav>
    </header>
    <router-view />
  </div>
</template>

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
  height: 62px;
  width: 100%;
  position: relative;
  z-index: 2;
  background-color: #2a2a2a;
  box-shadow: 0 5px 15px 0 rgb(0 0 0 / 50%);
}
</style>
