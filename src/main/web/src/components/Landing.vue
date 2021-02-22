<template>
  <div>
    <div style="margin: 10px">
      <h3 style="text-align: center">Wypożyczalnie "RobertCarpol"</h3>
    </div>
    <v-carousel>
      <v-carousel-item v-for="(item,i) in items" :src="item.src" :key="i"></v-carousel-item>
    </v-carousel>
    <br>
    <div class="blockquote" style="background-color: aliceblue; padding: 5px; margin: 5px">
      System do wypożyczania samochodów
    </div>
  </div>
</template>

<script>
  export default {
    name: 'Landing',
    data() {
      return {
        items: [{src: require('../assets/1.jpeg')}, {src: require('../assets/2.jpg')}, {src: require('../assets/3.jpeg')}, {src: require('../assets/4.jpeg')}]
      }
    }
  }
</script>

<style scoped>

</style>
