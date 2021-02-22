<template>
  <v-layout>
    <v-flex xs12 sm2 offset-sm5 md4 offset-md4 sm4 offset-sm3 style="margin-top: 20px">
      <v-card>
        <v-card-title primary-title>
          <div>
            <h3 class="headline mb-0">{{ user.firstName }} {{ user.lastName }}</h3>
            <div>
              <p>login: {{ user.login }}</p>
              <p>mail: {{ user.email }}</p>
              <p>telefon: {{ user.phone }}</p>
            </div>
          </div>
        </v-card-title>
        <v-card-actions>
          <v-btn flat color="orange" @click.stop="dialog=true">Zmień hasło</v-btn>
          <v-spacer></v-spacer>
          <v-btn flat color="orange" @click.stop="logout()">Wyloguj</v-btn>
        </v-card-actions>
      </v-card>
    </v-flex>
    <v-dialog v-model="dialog" persistent max-width="500px">
      <v-card>
        <v-card-title>
          <span class="headline">Zmiana hasła</span>
        </v-card-title>
        <v-card-text>
          <v-container grid-list-md>
            <v-layout wrap>
              <v-flex xs12>
                <v-text-field label="Hasło bierzące" type="password" v-model="password" required></v-text-field>
              </v-flex>
              <v-flex xs12>
                <v-text-field label="Nowe hasło" type="password" v-model="newPass" required></v-text-field>
              </v-flex>
              <v-flex xs12>
                <v-text-field label="Powtórz nowe hasło" type="password" v-model="newPassRepeat" required></v-text-field>
              </v-flex>
            </v-layout>
          </v-container>
          <small>*indicates required field</small>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="blue darken-1" flat @click.native="dialog = false">Close</v-btn>
          <v-btn color="blue darken-1" flat @click.native="changePassword()">Save</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-layout>
</template>

<script>
  import {EventBus} from "../../main";

  export default {
    name: 'User',
    data() {
      return {
        user: {},
        pass:'',
        newPass:'',
        newPassRepeat:'',
        avatar: require('../../assets/avatar.png'),
        dialog: false
      }
    },
    methods: {
      logout() {
        this.$http.get('/logout').then(() => {
            EventBus.loggedIn = false;
            EventBus.$emit('userLoggedOut');
            this.$router.push('/');
          },
          (err) => {
            EventBus.snackbarErrorMessage = 'Wylogowywanie się nie powiodło się. Powód:' + (err.message) ? err.message : err.code;
            EventBus.snackbarError = true;
          })
      },
      changePassword() {
        if(this.newPass === this.newPassRepeat){
          this.$http.get('/renter/user/changePassword?password='+this.password+'&newPassword='+this.newPass). then(
            (response) => {
              if (response.body){
                this.password=''
                this.newPass=''
                this.newPassRepeat=''
                this.dialog = false
                EventBus.snackbarSuccessMessage='Zmiana hasła się udała'
                EventBus.snackbarSuccess=true
              } else {
                EventBus.snackbarErrorMessage = 'Zmiana hasła się nie powiodła. Złe hasło?'
                EventBus.snackbarError = true;
              }
            },
            () => {
              EventBus.snackbarErrorMessage = 'Zmiana hasła się nie powiodła. Złe hasło?'
              EventBus.snackbarError = true;
            }
          )
        } else {
          EventBus.snackbarErrorMessage = 'Hasła się nie zgadzają'
          EventBus.snackbarError = true;
        }

      }
    },
    mounted(){
      this.$http.get('/renter/user/details').then(
        (response) => {
          this.user = response.body
        },
        (err) => {
          console.log(err.message)
          EventBus.snackbarErrorMessage='Nie udało się pobrać danych użytkownika'
          EventBus.snackbarError=true
        }
      )
    }
  }
</script>

<style scoped>

</style>
