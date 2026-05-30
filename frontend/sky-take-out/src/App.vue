<template>
  <div id="app">
    <GlobalNavbar v-if="showNavbar" />
    <router-view />
    <GlobalMessageButton />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import GlobalNavbar from './views/components/GlobalNavbar.vue'
import GlobalMessageButton from './views/components/GlobalMessageButton.vue'

const route = useRoute()

// 这些页面自带导航栏，不需要全局白色导航
const hideNavbarRoutes = ['/home', '/posts', '/user/follow', '/user/care', '/user/chat', '/user/profile', '/login', '/register', '/forget-password']

const showNavbar = computed(() => {
  return !hideNavbarRoutes.some(path => route.path === path || route.path.startsWith(path + '/'))
})
</script>

<style>
#app {
  min-height: 100vh;
}
</style>