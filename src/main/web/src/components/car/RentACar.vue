<template>
  <v-layout>
    <v-container fill-height v-if="loading">
      <v-layout row wrap align-center>
        <v-flex class="text-xs-center">
          <v-progress-circular indeterminate color="amber" center></v-progress-circular>
        </v-flex>
      </v-layout>
    </v-container>
    <v-flex xs12 sm6 offset-sm3>
      <v-card>
        <v-card-media :src="'/renter/car/image?imageId='+car.description.imgId" height="200px">
        </v-card-media>
        <v-card-title primary-title>
          <div>
            <h3 class="headline mb-0">{{ car.name }}</h3>
            <!--<div>{{car.description.info}}</div>-->
          </div>
        </v-card-title>
        <v-card-text>
          <p>Model: {{car.description.model}}</p>
          <p>Silnik: {{car.description.engine}}</p>
          <p>Rok produkcji: {{car.description.productionYear}}</p>
          <p>Siedzeń: {{car.description.noOfSeats}}</p>
          <p>Zasięg: {{car.description.reach}} km</p>
          <p>Opis: {{car.description.info}}</p>
          <p v-if="car.available!=null">Dostępny: {{availableWhen}}</p>
          <v-checkbox label="Szybkie" v-model="fast">Szybkie</v-checkbox>
          <p class="caption">Szybkie wynajęcie powoduje wynajęcie samochodu na 24h. Samochód zostanie dostarczony do
            miejsca w którym się znajdujesz. </p>
          <p>{{ getLocation() }}</p>
          <v-form v-model="valid" v-if="!fast">

            <v-flex xs12 sm6 md4>
              <v-dialog ref="dialog" v-model="dateModal" :return-value.sync="returnDate" persistent lazy full-width
                        width="290px" locale="pl-PL">
                <v-text-field slot="activator" v-model="returnDate" label="Data oddania" prepend-icon="event" readonly
                              locale="pl-PL"></v-text-field>
                <v-date-picker v-model="returnDate" scrollable locale="pl-PL" :min="dateFormat(new Date())">
                  <v-spacer></v-spacer>
                  <!--<v-btn flat color="primary" @click="dateModal = false">Cancel</v-btn>-->
                  <v-btn flat color="primary" @click="$refs.dialog.save(returnDate)">OK</v-btn>
                </v-date-picker>
              </v-dialog>
            </v-flex>
            <p></p>
            <v-text-field v-model="pos.latitude" label="Pozycja szerokość" required></v-text-field>
            <v-text-field v-model="pos.longitude" label="Pozycja wysokość" required></v-text-field>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-btn flat color="orange" @click.stop="rentACar()">Wynajmij</v-btn>
        </v-card-actions>
      </v-card>
    </v-flex>
    <v-snackbar timeout="2000" top="true" color="success"  v-model="snackbar">
      Samochód wynajęty.
      <v-btn flat color="orange" @click.native="snackbar = false">Close</v-btn>
    </v-snackbar>
    <v-snackbar timeout="2000" top="true" v-model="snackbarError">
      {{snackbarErrorMessage}}
      <v-btn flat @click.native="snackbarError = false">Close</v-btn>
    </v-snackbar>
  </v-layout>
</template>

<script>
  import {EventBus} from "../../main";

  export default {
    name: "RentACar",
    data() {
      return {
        carId: this.$route.params.id,
        car: null,
        fast: true,
        menu: false,
        snackbar: false,
        returnDate: this.dateFormat(new Date(new Date().getTime() + 24 * 60 * 60 * 1000)),
        pos: {latitude: null, longitude: null},
        rentalType: 'DISPATCH',
        loading: false
      }
    },
    methods: {
      getLocation() {
        if (navigator.geolocation) {
          console.log('Geolocation is supported!');
          this.getPosition().then((res) => {
            this.pos.latitude = res.coords.latitude;
            this.pos.longitude = res.coords.longitude;
          }, (err) => console.log(err))
        }
        else {
          console.log('Geolocation is not supported for this Browser/OS.');
          alert("Geolokacja nie jest możliwa. Nie będzie można skorzystać z szybkiego wynajęcia.")
        }
      },
      getPosition() {
        // Simple wrapper
        return new Promise((res, rej) => {
          navigator.geolocation.getCurrentPosition(res, rej);
        });
      },
      rentACar() {
        let split = this.returnDate.split('-')
        split[1] = Number(split[1])-1
        this.$http.post('/renter/user/rentCar?carId=' + this.carId, {
          type: this.rentalType,
          startDate: new Date(),
          plannedReturnDate: new Date(split[0], split[1], split[2])
        })
          .then((response) => {
              this.snackbar = true;
              setTimeout(() => this.$router.push('/userCars'), 2000)
            },
            (err) => {
              console.log(err)
              EventBus.snackbarErrorMessage = 'Wypożyczenie samochodu się nie powiodło. ' + (err.message) ? err.message : err.code
              EventBus.snackbarError = true
            })
      },
      dateFormat(date) {
        console.log('Data :: ', date)
        return date.getFullYear() + '-' + (date.getMonth()+1).toString().padStart(2, '0') + '-' + (date.getDate()).toString().padStart(2, '0')
      }
    },
    computed: {
      isLoggedIn() {
        return EventBus.loggedIn;
      }
    },
    created() {
      this.loading = true
      this.$http.get('/renter/car/one?id=' + this.carId).then(
        (response) => {
          this.loading = false
          console.dir(response.body)
          this.car = response.body
        },
        (err) => {
          console.log("Got error. Message :: " + err)
        }
      )
    }
  }
</script>

<style scoped>

</style>
