<template>
  <v-layout>
    <v-flex xs12 sm6 md4 offset-sm3 offset-md4>
      <v-card>
        <v-card-title primary-title class="justify-center" lazy-validation>
          <div>
            <h3 class="headline mb-0">Login</h3>
            <v-form v-model="valid" align-center>
              <v-text-field v-model="email" :rules="emailRules" label="Email" required autofocus></v-text-field>
              <v-text-field v-model="password" :rules="passwordRules"
                            :append-icon="showPwd ? 'visibility' : 'visibility_off'"
                            :append-icon-cb="() => (showPwd = !showPwd)" :type="showPwd ? 'password' : 'text'"
                            name="input-10-1" label="Hasło" required v-on:keyup.enter.stop="login"></v-text-field>
            </v-form>
          </div>
        </v-card-title>
        <v-card-actions>
          <v-btn flat color="orange" @click.stop="singup" >Rejestracja</v-btn>
          <v-spacer></v-spacer>
          <v-btn flat color="orange" @click.stop="login" :disabled="!valid">Login</v-btn>
        </v-card-actions>
      </v-card>
    </v-flex>
  </v-layout>
</template>

<script>
  import {EventBus} from "../../main";

  export default {
    name: "Login",
    data() {
      return {
        email: '',
        password: '',
        showPwd: true,
        valid: false,
        emailRules: [
          v => !!v || 'Pole email jest wymagane',
          v => /^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/.test(v) || 'Email nie jest poprawny'
        ],
        passwordRules: [
          v => !!v || 'Pole hasło jest wymagane',
          v => v.length > 3 || 'Hasło powinno zawierać przynajmniej 3 znaki'
        ],
        snackbarError: true,
        errormsg: ''
      }
    },
    methods: {
      login() {
        console.log('Logging inn with : ' + this.email + ' : ' + this.password)
        this.$http.post('/login', {
          username: this.email.toLowerCase(),
          password: this.password
        }, {emulateJSON: true})
          .then(() => {
              EventBus.loggedIn = true
              EventBus.$emit('userLoggedIn')
              this.$router.push('/')
            },
            (err) => {
              console.log('Niepoprawne logowanie', err.message)
              EventBus.snackbarErrorMessage = 'Niepoprawyny email lub hasło'
              EventBus.snackbarError = true
            })
      },
      singup() {
              this.$router.push('/register')

      }
    }
  }
</script>

<style scoped>

</style>
