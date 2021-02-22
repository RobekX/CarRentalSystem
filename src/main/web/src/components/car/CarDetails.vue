<template>
  <v-dialog v-model="details" max-width="500px">
    <v-card>
      <v-card-title>
        Szczegóły
      </v-card-title>
      <v-card-media class="white--text mx-auto" height="240px" :src="'/renter/car/image?imageId='+car.description.imgId" style="max-width: 320px" center>
      </v-card-media>
      <v-card-text>
        <p>Model: {{car.description.model}}</p>
        <p>Silnik: {{car.description.engine}}</p>
        <p>Rok produkcji: {{car.description.productionYear}}</p>
        <p>Siedzeń: {{car.description.noOfSeats}}</p>
        <p>Zasięg: {{car.description.reach}} km</p>
        <p>Opis: {{car.description.info}}</p>
        <p>Cena: {{car.price*0.01}} zł</p>
        <p v-if="car.available!=null">Dostępny: {{availableWhen}}</p>
      </v-card-text>
      <v-card-actions>
        <v-btn color="orange" flat @click.stop="rent" :disabled="car.status!='FREE' || !isLoggedIn">Wynajmij</v-btn>
        <v-spacer></v-spacer>
        <v-btn color="orange" flat @click.stop="details=false">Zamknij</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
  import {EventBus} from '../../main'

  export default {
    name: 'CarDetails',
    data() {
      return {
        details: false,
        car: {name: '', desc: '', src: '', available: null, availableWhen: ''}
      }
    },
    methods: {
      checkAvailability() {
        EventBus.checkAvailability(this.car)
      },
      rent() {
        this.$router.push({name: 'RentACar', params: {id: this.car.id}})
      }
    },
    computed: {
      availableWhen() {
        console.log('Returning available when : ' + this.car.availableWhen)
        return this.car.availableWhen
      },
      isLoggedIn(){
        return EventBus.loggedIn;
      }
    },
    created() {
      EventBus.$on('openCarDetails', (car) => {
        console.log('Got openCarDetails event for ', car)
        this.details = true
        this.car = car
      })
    }
    // props: ['details']

  }
</script>

<style scoped>

</style>
