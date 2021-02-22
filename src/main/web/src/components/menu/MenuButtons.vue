<template>
  <div>
    <md-tabs md-sync-route>
      <md-tab id="tab-home" md-label="Strona Główna" to="/"></md-tab>
      <md-tab id="tab-pages" md-label="Samochody" to="/cars/"></md-tab>
      <esel-user-data class="user-data"></esel-user-data>
    </md-tabs>
  </div>
</template>

<script>
  import UserData from './UserData.vue';

  export default {
    name: "MenuButtons",
    components: {
      'esel-user-data': UserData
    }
  }
</script>

<style scoped>
  .user-data {
    float: right;
  }
</style>
