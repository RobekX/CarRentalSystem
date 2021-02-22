<template>
  <div>
    <v-container fill-height v-if="loading">
      <v-layout row wrap align-center>
        <v-flex class="text-xs-center">
          <v-progress-circular indeterminate color="amber" center></v-progress-circular>
        </v-flex>
      </v-layout>
    </v-container>
    <v-alert :value="arrEmpty" type="info">
      {{infoMessage}}
    </v-alert>
    <v-container fluid grid-list-x2>
      <v-layout row v-if="!user">
        <v-flex align-content-end>
          <v-checkbox label="Tylko dostępne" v-model="onlyFree"></v-checkbox>
        </v-flex>
      </v-layout>
      <v-layout row wrap>
        <v-flex v-for='c in filteredCars' :key='c.id' xs12 sm6 offset-sm3 style="min-width: 240px">
          <v-card class="container" hover raised>
            <v-card-media class="white--text mx-auto" height="240px"
                          :src="'/renter/car/image?imageId='+c.description.imgId" style="max-width: 240px" center>
              <!--:src="require('../../assets/'+c.description.imgSrc)"-->
            </v-card-media>
            <v-card-title>
              <div>
                <span>{{ c.name }}</span><br>
              </div>
            </v-card-title>
            <v-card-actions>
              <v-tooltip bottom>
                <v-btn slot="activator" flat color="orange" :disabled="c.status!=='FREE' || !isLoggedIn" @click.stop="rentACar(c)" v-if="!user">Wynajmij
                </v-btn>
                <span>{{(!isLoggedIn)? 'Tylko dla zalogowanych' : (c.status!=='FREE')? 'Samochód zajęty' : 'Wynajmij'  }}</span>
              </v-tooltip>

              <v-spacer></v-spacer>
              <v-btn flat color="orange" @click.stop="getCarDetails(c)" v-if="!user">Szczegóły</v-btn>
              <v-btn flat color="orange" @click.stop="returnCar(c)" v-if="user">Zwróć</v-btn>
            </v-card-actions>
          </v-card>
        </v-flex>
      </v-layout>
    </v-container>
    <esel-car-details :details="details"></esel-car-details>
  </div>
</template>

<script>
  import CarDetails from './CarDetails.vue'
  import {EventBus} from '../../main'

  export default {
    name: 'CarCardList',
    data() {
      return {
        cars: [],
        gridCardClassArray: [
          {'xs8': this.$vuetify.breakpoint.xs},
          {'sm4': this.$vuetify.breakpoint.sm},
          {'md2': this.$vuetify.breakpoint.md},
          {'xl2': this.$vuetify.breakpoint.xl}
        ],
        details: false,
        onlyFree: true,
        loading: false,
        arrEmpty: false,
        infoMessage: ''
      }
    },
    props: ['user'],
    methods: {
      getCarDetails(car) {
        console.log('Getting details for car ', car)
        EventBus.$emit('openCarDetails', car)
      },
      rentACar(car) {
        this.$router.push({name: 'RentACar', params: {id: car.id}})
      },
      returnCar(car) {
        if (confirm('Oddać samochód?')) {
          this.$http.post('/renter/user/returnCar?carId=' + car.id)
            .then((response) => {
              const index = this.cars.indexOf(car)
              if (index > -1) {
                console.log("Removing car from list")
                this.cars.splice(index, 1)
                EventBus.snackbarSuccessMessage='Samochód został oddany'
                EventBus.snackbarSuccess=true
              }
            }, (err) => {
              console.log(err.message)
              EventBus.snackbarErrorMessage="Nie udało się wypożyczyć samochodu. "+(err.message)?err.message : err.code
              EventBus.snackbarError=true
            })
        }
      },
      getCarImage(imgId) {
        const url = '/renter/car/image?imageId=' + imgId
        return url;
      },
      isArrEmpty() {
        console.log("Arr empty? "+this.cars === undefined || this.cars.length == 0)
        return this.cars === undefined || this.cars.length == 0
      }
    },
    computed: {
      filteredCars() {
        return (this.onlyFree) ? this.cars.filter((car) => car.status == 'FREE') : this.cars
      },
      isLoggedIn(){
        return EventBus.loggedIn;
      }
    },
    components: {
      'esel-car-details': CarDetails
    },
    mounted() {
      let link = ''
      if (this.user) {
        link = '/renter/user/rentedCars?status=RENTED'
        this.onlyFree = false
        this.infoMessage = 'Brak wypożyczonych samochodów'
      } else {
        link = '/renter/car/all'
        this.onlyFree = true
        this.infoMessage = 'Brak dostępnych samochodów'
      }
      // console.log('Using ' + link)
      this.loading = true
      this.$http.get(link).then((request) => {
        // console.dir(request)
        this.cars = request.body;
        this.loading = false
        this.arrEmpty = this.isArrEmpty()
        // console.dir(this.cars)
      }, (err) => {
        this.loading = false
        EventBus.snackbarErrorMessage='Nie udało się pobieranie listy samochodów'
        EventBus.snackbarError=true
      })
    }
  }
</script>

<style scoped>
</style>
