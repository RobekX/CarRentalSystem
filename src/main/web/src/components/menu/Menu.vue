<template>
  <div>
    <esel-menu-buttons></esel-menu-buttons>

  </div>
</template>

<script>
  import MenuButtons from './MenuButtons.vue';
  import UserData from './UserData.vue';

  export default {
    name: "Menu",
    components: {
      'esel-menu-buttons': MenuButtons
    }
  }
</script>

<style scoped>
  .md-menu {
    margin: 24px;
  }
</style>
