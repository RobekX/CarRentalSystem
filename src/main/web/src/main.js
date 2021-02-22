// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'

import Vuetify from 'vuetify'
import 'vuetify/dist/vuetify.min.css'
Vue.use(Vuetify)

import VueResource from "vue-resource"
Vue.use(VueResource);

Vue.config.productionTip = false

export const EventBus = new Vue({
  data() {
    return {
      loggedIn: false,
      snackbarError: false,
      snackbarErrorMessage: 'Error has occurred',
      snackbarSuccess: false,
      snackbarSuccessMessage: 'Success has occurred'
    }
  },
  methods: {

  }
})

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  components: {App},
  template: '<App/>'
})
