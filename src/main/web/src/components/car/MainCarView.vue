<template>
  <div>
    <esel-car-card-list></esel-car-card-list>
  </div>
</template>

<script>
  import CarCardList from './CarCardList.vue'

  export default {
    name: 'MainCarView',
    components: {
      'esel-car-card-list': CarCardList
    }
  }
</script>

<style scoped>

</style>
