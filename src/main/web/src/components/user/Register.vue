<template>
  <v-layout>
    <v-flex xs12 sm6 md4 offset-sm3 offset-md4>
      <v-card>
        <v-card-title primary-title class="justify-center">
          <div>
            <h3 class="headline mb-0">Login</h3>
            <v-form ref="form" v-model="valid" align-center lazy-validation>
              <v-text-field v-model="user.email" :rules="emailRules" label="Email" required autofocus></v-text-field>
              <v-text-field v-model="user.login" :rules="textRules" label="Login" required></v-text-field>
              <v-text-field v-model="user.firstName" :rules="textRules" label="Imie" required></v-text-field>
              <v-text-field v-model="user.lastName" :rules="textRules" label="Nazwisko" required></v-text-field>
              <v-text-field v-model="user.phone" :rules="phoneNumberRules" label="Telefon" required></v-text-field>
              <v-text-field v-model="user.password" :rules="passwordRules"
                            :append-icon="showPwd ? 'visibility' : 'visibility_off'"
                            :append-icon-cb="() => (showPwd = !showPwd)" :type="showPwd ? 'password' : 'text'"
                            name="input-10-1" label="Hasło" required></v-text-field>
            </v-form>
          </div>
        </v-card-title>
        <v-card-actions>
          <v-btn :disabled="!valid" @click="submit"> Rejestracja</v-btn>
          <v-spacer></v-spacer>
          <v-btn @click="clear"> Wyczyść</v-btn>
        </v-card-actions>
      </v-card>
    </v-flex>
  </v-layout>
</template>

<script>
  import {EventBus} from "../../main";

  export default {
    name: "Register",
    data() {
      return {
        valid: false,
        showPwd: true,
        user: {
          email: '',
          password: '',
          login: '',
          firstName: '',
          lastName: '',
          phone: ''
        },
        emailRules: [
          v => !!v || 'E-mail jest wymagany',
          v => /^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/.test(v) || 'Niepoprawny e-mail'
        ],
        passwordRules: [
          v => !!v || 'Hasło nie może być puste',
          v => (v && v.length >= 6) || 'Hasło musi mieć conajmniej 6 znaków'
        ],
        textRules: [
          v => !!v || "Pole jest wymagane"
        ],
        phoneNumberRules: [
          v => !!v || "Pole jest wymagane",
          v => /^[(]{0,1}[0-9]{3}[)]{0,1}[-\s\.]{0,1}[0-9]{3}[-\s\.]{0,1}[0-9]{3}$/.test(v) || 'Niepoprawny numer telefonu'

        ]
      }
    },
    methods: {
      submit() {
        if (this.$refs.form.validate()) {
          this.$http.post('/renter/user/add', this.user ).then(
            () => {
              EventBus.snackbarSuccessMessage = 'Dodano nowego użytkownika'
              EventBus.snackbarSuccess = true
              this.$router.push('/login')
            },
            (err) => {
              console.log('Error in register form ', err)
              EventBus.snackbarErrorMessage = 'Rejestracja się nie powiodła'
              EventBus.snackbarError = true
            }
          )
        }
      },
      clear() {
        this.$refs.form.reset()
      }
    }
  }
</script>

<style scoped>

</style>

