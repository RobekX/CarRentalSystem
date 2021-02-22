<template>
  <v-layout justify-center>
    <v-flex xs12 sm10 md8 lg6>
      <v-card ref="form">
        <v-card-text>
          <v-container fluid  v-if="!isEdit">
            <v-flex xs12 class="text-xs-center text-sm-center text-md-center text-lg-center">
              <img :src="imageUrl" height="150" v-if="imageUrl"/>
              <v-text-field label="Załącz zdjęcie samochodu" @click='pickFile' v-model='imageName'
                            prepend-icon='attach_file' :rules="[() => !!imageName || 'To pole jest wymagane']"
                            :error-messages="errorMessages"></v-text-field>
              <input type="file" style="display: none" ref="image" accept="image/*" @change="onFilePicked">
            </v-flex>
            <v-dialog v-model="dialog" max-width="290">
              <v-card>
                <v-card-title class="headline"></v-card-title>
                <v-card-text>
                </v-card-text>
                <v-card-actions>
                  <v-spacer></v-spacer>
                  <v-btn color="green darken-1" flat="flat" @click.native="dialog = false">Close</v-btn>
                </v-card-actions>
              </v-card>
            </v-dialog>
          </v-container>
          <v-text-field ref="name" v-model="name" :rules="[() => !!name || 'To pole jest wymagane']"
                        :error-messages="errorMessages" label="Marka samochodu" hint="Marka samochodu np.: Fiat"
                        required></v-text-field>
          <v-text-field ref="model" :rules="[() => !!model || 'To pole jest wymagane']" v-model="model" label="Model"
                        hint="model samochodu np.: uno" required></v-text-field>
          <v-text-field ref="engine" v-model="engine" :rules="[() => !!engine || 'To pole jest wymagane']"
                        label="Silnik" required hint="model silnika np. 1.0MPI"></v-text-field>
          <v-text-field ref="productionYear"
                        :rules="[() => !!productionYear || 'To pole jest wymagane', (year) => !isNaN(year) || 'Pole powinno zawierać tylko liczby']"
                        v-model="productionYear" label="Rok produkcji" required
                        hint="Rok produkcji. Np. 2017"></v-text-field>
          <v-select ref="noOfSeats" :rules="[() => !!noOfSeats || 'To pole jest wymagane']" :items="noOfSeat"
                    v-model="noOfSeats" autocomplete label="Ilosc miejsc" placeholder="Wybierz..."
                    hint="Ilość miejsc w samochodzie" required></v-select>
          <v-text-field ref="reach"
                        :rules="[() => !!reach || 'To pole jest wymagane', (reach) => !isNaN(reach) || 'Pole powinno zawierać tylko liczby']"
                        v-model="reach" label="Zasieg"
                        required hint="Dystans możliwy do przejechania na jednym baku" suffix="km"></v-text-field>
          <v-text-field ref="price"
                        :rules="[() => !!price || 'To pole jest wymagane', (price) => !isNaN(price) || 'Pole powinno zawierać tylko liczby']"
                        v-model="price" label="Cena"
                        required hint="Cena za godzinę" suffix="zł"></v-text-field>
          <v-text-field ref="info" :rules="[() => !!info || 'To pole jest wymagane']" v-model="info"
                        label="Informacje dodatkowe" hint="Opis samochodu" required></v-text-field>
        </v-card-text>
        <v-divider class="mt-5"></v-divider>
        <v-card-actions>
          <v-btn to="/reports" flat v-if="!isEdit">Anuluj</v-btn>
          <v-spacer></v-spacer>
          <v-slide-x-reverse-transition>
            <v-tooltip v-if="formHasErrors" left>
              <v-btn slot="activator" icon class="my-0" @click="resetForm">
                <v-icon>refresh</v-icon>
              </v-btn>
              <span>Refresh form</span>
            </v-tooltip>
          </v-slide-x-reverse-transition>
          <v-btn color="primary" flat @click="add" v-if="!isEdit">Dodaj</v-btn>
          <v-btn color="primary" flat @click="update" v-if="isEdit">Zapisz</v-btn>
        </v-card-actions>
      </v-card>
    </v-flex>
  </v-layout>
</template>


<script>
  import {EventBus} from "../../main";

  export default {
    data: () => ({
      noOfSeat: [1, 2, 3, 4, 5, 6, 7, 8],
      errorMessages: [],
      name: null,
      status: 'FREE',
      model: null,
      engine: null,
      productionYear: null,
      noOfSeats: null,
      reach: null,
      info: null,
      price: null,
      formHasErrors: false,
      title: "Image Upload",
      dialog: false,
      imageName: '',
      imageUrl: '',
      imageFile: '',
      isEdit: false
    }),
    computed: {
      form() {
        return {
          name: this.name,
          model: this.model,
          engine: this.engine,
          productionYear: this.productionYear,
          noOfSeats: this.noOfSeats,
          reach: this.reach,
          info: this.info,
          price: this.price
        }
      }
    },
    watch: {
      name() {
        this.errorMessages = []
      }
    },
    methods: {
      resetForm() {
        this.errorMessages = []
        this.formHasErrors = false
        Object.keys(this.form).forEach(f => {
          this.$refs[f].reset()
        })
      },
      add() {
        let formData = new FormData();
        this.formHasErrors = false
        Object.keys(this.form).forEach(f => {
          if (!this.form[f]) this.formHasErrors = true
          this.$refs[f].validate(true)
        })
        if (this.formHasErrors) {
          EventBus.snackbarErrorMessage = 'Formularz nie jest poprawnie wypełniony'
          EventBus.snackbarError = true
        } else {
          formData.append('file', this.imageFile, this.imageName);
          this.$http.post('/renter/car/upload', formData)
            .then(
              (response) => {
                console.dir(response)
                console.log("Got image id :: " + response.bodyText)
                this.$http.post('/renter/car/add', {
                  name: this.name,
                  status: this.status,
                  price: this.price * 100,
                  description: {
                    model: this.model,
                    engine: this.engine,
                    productionYear: this.productionYear,
                    noOfSeats: this.noOfSeats,
                    reach: this.reach,
                    info: this.info,
                    imgId: response.bodyText
                  }
                })
                  .then(() => {
                      EventBus.snackbarSuccessMessage = 'Samochód dodany'
                      EventBus.snackbarSuccess = true
                      setTimeout(() => this.$router.push('/reports'), 2000);
                    },
                    (err) => {
                      console.log(err)
                      EventBus.snackbarErrorMessage = err.message
                      EventBus.snackbarError = true;
                    })
              },
              (err) => console.log('Got error :: ' + err.message))
        }
      },
      update() {
        let formData = new FormData();
        this.formHasErrors = false
        Object.keys(this.form).forEach(f => {
          if (!this.form[f]) this.formHasErrors = true
          this.$refs[f].validate(true)
        })
        if (this.formHasErrors) {
          EventBus.snackbarErrorMessage = 'Formularz nie jest poprawnie wypełniony'
          EventBus.snackbarError = true
        } else {
          this.$http.post('/renter/car/update', {
            id: this.$route.query.edit,
            name: this.name,
            status: this.status,
            price: this.price * 100,
            description: {
              model: this.model,
              engine: this.engine,
              productionYear: this.productionYear,
              noOfSeats: this.noOfSeats,
              reach: this.reach,
              info: this.info,
            }
          })
            .then(() => {
                EventBus.snackbarSuccessMessage = 'Samochód edytowany'
                EventBus.snackbarSuccess = true
                setTimeout(() => this.$router.push('/reports'), 2000);
              },
              (err) => {
                console.log(err)
                EventBus.snackbarErrorMessage = err.message
                EventBus.snackbarError = true;
              })
        }
      },
      pickFile() {
        this.$refs.image.click()
      },
      onFilePicked(e) {
        const files = e.target.files
        if (files[0] !== undefined) {
          this.imageName = files[0].name
          if (this.imageName.lastIndexOf('.') <= 0) {
            return
          }
          const fr = new FileReader()
          fr.readAsDataURL(files[0])
          fr.addEventListener('load', () => {
            this.imageUrl = fr.result
            this.imageFile = files[0] // this is an image file that can be sent to server...
            console.dir(this.imageFile)
          })
        } else {
          this.imageName = ''
          this.imageFile = ''
          this.imageUrl = ''
        }
      }
    }, mounted() {
      console.log('Got ', this.$route.edit)
      console.log('Got ',)
      if (this.$route.query.edit) {
        this.$http.get('/renter/car/one?id=' + this.$route.query.edit).then(
          (response) => {
            this.isEdit = true
            let car = response.body
            console.log('Got car :: ', car)
            this.name = car.name
            this.model = car.description.model
            this.engine = car.description.engine
            this.productionYear = car.description.productionYear
            this.noOfSeats = car.description.noOfSeats
            console.log('Number of seats :: ', this.noOfSeats)
            this.reach = car.description.reach
            this.info = car.description.info
            this.price = car.price/100
            // this.$refs.noOfSeats.select(this.noOfSeats)
          },
          (err) => {
            console.log(err.message)
            EventBus.snackbarErrorMessage = 'Nie udało się pobrać danych samochodu'
            EventBus.snackbarError = true
          }
        )
      } else {
        this.isEdit = false
      }
    }
  }
</script>
