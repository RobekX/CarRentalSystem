<template>
  <v-card>
    <v-card-title>
      <v-spacer></v-spacer>
      <v-text-field v-model="search" append-icon="search" label="Szukaj" single-line hide-details
                    class="text-sm-left"></v-text-field>
      <v-btn slot="activator" color="primary" dark class="text-sm-left" to="/addCar">Dodaj samochó

        d</v-btn>
    </v-card-title>
    <v-data-table :headers="headers" :items="cars" :search="search" hide-actions class="elevation-1">
      <template slot="items" slot-scope="props">
        <td>{{ props.item.id }}</td>
        <td class="text-xs-right">{{ props.item.name }}</td>
        <td class="text-xs-right">{{ props.item.description.info }}</td>
        <td class="text-xs-right">{{ props.item.status }}</td>
        <td class="justify-center layout px-0">
          <v-btn icon class="mx-0" @click="editItem(props.item)">
            <v-icon color="teal">edit</v-icon>
          </v-btn>
          <v-btn icon class="mx-0" @click="deleteItem(props.item)" :disabled="props.item.status!='FREE'">
            <v-icon color="pink">delete</v-icon>
          </v-btn>
        </td>
      </template>
      <v-snackbar timeout="2000" top="true" v-model="snackbar">
        Samochód usniety.
        <v-btn flat color="orange" @click.native="snackbar = false">Close</v-btn>
      </v-snackbar>
      <v-snackbar timeout="2000" top="true" v-model="snackbarError">
        {{snackbarErrorMessage}}
        <v-btn flat color="orange" @click.native="snackbarError = false">Close</v-btn>
      </v-snackbar>
      <v-alert slot="no-results" :value="true" color="error" icon="warning">
        Brak wyników dla "{{ search }}".
      </v-alert>
    </v-data-table>
  </v-card>
</template>

<script>
  export default {
    name: 'Reports',
    data() {
      return {
        search: '',
        snackbar: false,
        snackbarError: false,
        snackbarErrorMessage: '',
        headers: [
          {text: 'Identyfikator', value: 'id', align: 'right'},
          {text: 'Model', value: 'description.model', align: 'left'},
          {text: 'Opis', value: 'description.info', align: 'left'},
          {text: 'Status', value: 'status', align: 'right'}
        ],
        cars: []
      }
    },
    methods: {
      deleteItem(car) {
        if (confirm('Na pewno chcesz usunac ten samochod ?')) {
          const index = this.cars.indexOf(car)
          if (index > -1) {
            this.$http.get('/renter/car/delete?carId=' + this.cars[index].id)
              .then((response) => {
                this.snackbar = true;
                setTimeout(() => this.$router.push('/reports'), 2000)
              }, (err) => {
                console.log(err)
                this.snackbarError = true;
                this.snackbarErrorMessage = err.message
              })
            this.cars.splice(index, 1)
          }
        }
      },
      editItem(car) {
        // const editedIndex = this.cars.indexOf(car)
        this.$router.push('/addCar?edit='+car.id)


      }
    },
    mounted() {
      this.$http.get('/renter/car/all').then((request) => {
        console.dir(request)
        this.cars = request.body;
        console.dir(this.cars)
      }, (err) => {
        console.log(err)
      })
    }
  }
</script>

<style scoped>

</style>
