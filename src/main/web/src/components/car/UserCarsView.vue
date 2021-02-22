<template>
  <div>
    <esel-car-card-list :user="true"></esel-car-card-list>
  </div>
</template>

<script>
  import CarCardList from './CarCardList.vue'

  export default {
    name: 'UserCarsView',
    components: {
      'esel-car-card-list': CarCardList
    }
  }
</script>

<style scoped>

</style>
