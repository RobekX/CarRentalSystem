<template>
  <v-card>
    <v-flex xs12 sm6>
      <v-text-field v-model="latitude" box lable="Szerokosc geograficzna"></v-text-field>
    </v-flex>
    <v-flex xs12 sm6>
      <v-text-field v-model="longitudes" box lable="Dlugosc geograficzna"></v-text-field>
    </v-flex>
    <v-data-table :headers="headers" :items="cars" hide-actions class="elevation-1">
      <template slot="items" slot-scope="props">
        <td>{{ props.item.name }}</td>
        <td class="text-xs-right">{{ props.item.desc }}</td>
        <td class="text-xs-right">{{ props.item.lati }}</td>
        <td class="text-xs-right">{{ props.item.longi }}</td>
      </template>
    </v-data-table>
  </v-card>
</template>

<script>
  import VCardTitle from "vuetify/src/components/VCard/VCardTitle";

  export default {
    components: {VCardTitle},
    name: 'findNearestCar',
    data(){
      return {
        latitude: '',
        longitudes: '',

        headers: [
          {text: 'Model', value: 'name', align: 'right'},
          {text: 'Opis', value: 'desc', align: 'right'},
          {text: 'Szerokosc', value: 'lati', align: 'right'},
          {text: 'Dlugosc', value: 'longi', align: 'right'},

        ],
        cars: [
          {name: 'auto1', desc: 'lorem', lati: '34.0331 N', longi: '50.0103 E'},
          {name: 'auto2', desc: 'lorem', lati: '43.0331 S', longi: '10.0103 W'},
          {name: 'auto3', desc: 'lorem', lati: '21.0331 N', longi: '20.0103 E'},
          {name: 'auto4', desc: 'lorem', lati: '26.0331 S', longi: '40.0103 W'},
        ]
      }
    }
  }
</script>
