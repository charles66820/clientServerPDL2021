import { createRouter, createWebHistory } from "vue-router"
import Home from "@/views/Home.vue"
import Image from "@/views/Image.vue"
import About from "@/views/About.vue"

const routes = [
  {
    path: "/",
    name: "Home",
    component: Home
  },
  {
    path: "/image/:id",
    name: "Image",
    component: Image
  },
  {
    path: "/about",
    name: "About",
    component: About
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
  linkActiveClass: "active",
  linkExactActiveClass: "exact-active",
});

export default router
