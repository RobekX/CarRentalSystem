<template>
  <div>
    {{  }}
    <v-container fill-height v-if="loading">
      <v-layout row wrap align-center>
        <v-flex class="text-xs-center">
          <v-progress-circular indeterminate color="amber" center></v-progress-circular>
        </v-flex>
      </v-layout>
    </v-container>
    <v-card>
      <v-card-title>Moje wypożyczenia</v-card-title>
      <v-data-table :headers="tableHeaders" :items="tableItems">
        <template slot="items" slot-scope="props">
          <td>{{props.item.rentalNumber}}</td>
          <td>{{new Date(Date.parse(props.item.startDate)).toLocaleString()}}</td>
          <td>{{(!isNaN(Date.parse(props.item.finishDate)))? new Date(Date.parse(props.item.finishDate)).toLocaleString() : 'brak'}}</td>
          <td>{{props.item.payment/100}}</td>
          <td>{{(props.item.isPaid)? 'Zapłacone' : 'Do zapłaty'}}</td>
          <td class="justify-center layout px-0">
            <v-tooltip bottom>
              <v-btn slot="activator" icon @click="genInvoice(props.item.rentalNumber)">
                <v-icon>fa-file-pdf</v-icon>
              </v-btn>
              <span>Faktura</span>
            </v-tooltip>
          </td>
        </template>
      </v-data-table>
    </v-card>
  </div>
</template>

<script>
  import {EventBus} from "../../main";

  export default {
    name: "UserRentals",
    data() {
      return {
        tableHeaders: [
          {text: 'Numer wypożyczenia', value: 'rentalNumber'},
          {text: 'Data rozpoczęcia', value: 'startDate'},
          {text: 'Data zakończenia', value: 'finishDate'},
          {text: 'Kwota (w PLN)', value: 'payment'},
          {text: 'Zapłacone', value: 'isPaid'}],
        tableItems: [],
        loading: false
      }
    },
    methods: {
      genInvoice(rentalNumber){
        window.open('/renter/user/invoice?rentalId='+rentalNumber, '_blank');
      }
    },
    mounted() {
      this.loading = true
      this.$http.get('/renter/user/rentals?status=FINISHED')
        .then((response) => {
          this.tableItems = response.body
          this.loading = false
        }, (err) => {
          console.log("Error. " + err.message)
          EventBus.snackbarErrorMessage = 'Nie udało się pobrać wypożyczeń użytkownika.'
          EventBus.snackbarError = true
          this.loading = false
        })
    }
  }
</script>

<style scoped>
</style>
