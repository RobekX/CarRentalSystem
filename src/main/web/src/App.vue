<template>
  <v-app>
    <v-navigation-drawer persistent :mini-variant="miniVariant" :clipped="true" v-model="drawer" enable-resize-watcher
                         fixed app>
      <v-list>
        <v-list-tile value="true" v-for="(item, i) in items" :key="i" :to="item.goto" v-if="item.visible">

          <v-list-tile-action>
            <v-tooltip bottom>
              <v-icon slot="activator" v-html="item.icon"></v-icon>
              <span>{{ item.title }}</span>
            </v-tooltip>

          </v-list-tile-action>
          <v-list-tile-content>
            <v-list-tile-title v-text="item.title"></v-list-tile-title>
          </v-list-tile-content>
        </v-list-tile>
      </v-list>
    </v-navigation-drawer>
    <v-toolbar app clipped-left>
      <v-toolbar-side-icon @click.stop="drawer = !drawer"></v-toolbar-side-icon>
      <v-toolbar-title v-text="title"></v-toolbar-title>
      <v-spacer></v-spacer>
      <v-btn icon @click.native="changeFuctionButton()">
        <v-avatar>
          <v-icon dark>account_circle</v-icon>
        </v-avatar>
      </v-btn>
    </v-toolbar>
    <v-content>
      <router-view/>
      <v-snackbar top="true" v-model="snackbarError" color="error" multi-line="true">
        {{ snackbarErrorMessage }}
        <v-btn flat dark @click.native="closeSnackbarError">Close</v-btn>
      </v-snackbar>
      <v-snackbar top="true" v-model="snackbarSuccess" color="success" timeout="2000">
        {{ snackbarSuccessMessage }}
        <v-btn flat dark @click.native="closeSnackbarSuccess">Close</v-btn>
      </v-snackbar>
    </v-content>
    <v-footer :fixed="fixed" app>
      <span>&copy; 2018</span>
    </v-footer>
  </v-app>
</template>

<script>
  import {EventBus} from "./main";

  export default {
    name: 'App',
    data() {
      return {
        drawer: true,
        fixed: true,
        items: [{
          icon: 'home',
          title: 'Strona Główna',
          goto: '/',
          visible: true
        }, {
          icon: 'directions_car',
          title: 'Samochody',
          goto: '/cars',
          visible: true
        }, {
          icon: 'fa-arrow-alt-circle-down',
          title: 'Oddaj samochód',
          goto: '/userCars',
          userLevel: 'USER',
          visible: false
        }, {
          icon: 'fa-receipt',
          title: 'Moje wynajęcia',
          goto: '/myRentals',
          userLevel: 'USER',
          visible: false
        }, {
          icon: 'assignment',
          title: 'Raporty',
          goto: '/reports',
          userLevel: 'ADMIN',
          visible: false
        }, {
          icon: 'account_circle',
          title: 'Moje konto',
          goto: '/account',
          userLevel: 'USER',
          visible: false
        }],
        miniVariant: true,
        right: true,
        rightDrawer: false,
        title: 'Wypożyczalnia samochodów'
      }
    },
    computed: {
      snackbarError() {
        return EventBus.snackbarError;
      },
      snackbarErrorMessage() {
        return EventBus.snackbarErrorMessage;
      },
      snackbarSuccess() {
        return EventBus.snackbarSuccess;
      },
      snackbarSuccessMessage() {
        return EventBus.snackbarSuccessMessage;
      }

    },
    watch: {
      items: () => {
      }
    },
    methods: {
      closeSnackbarError() {
        EventBus.snackbarError = false;
      },
      closeSnackbarSuccess() {
        EventBus.snackbarSuccess = false;
      },
      checkPermission(item) {
        console.dir("Checking visibility for :: " + item);
        if (item.userLevel && item.userLevel === 'ADMIN') {
          console.log("User level ADMIN needed for this resource. Checking ");
          this.$http.get('/security/isAdmin').then((response) => item.visible = response.body)
        } else if (item.userLevel && item.userLevel === 'USER') {
          console.log("User level USER needed for this resource. Checking ");
          item.visible = EventBus.loggedIn
          // this.$http.get('/security/isAdmin').then((response) => item.visible = response.body)
        }
      },
      changeFuctionButton() {
        if(EventBus.loggedIn == true){
          this.$router.push('/account')
        }else {
          this.$router.push('/login')
        }
      }
    },
    created() {
      EventBus.$on('userLoggedIn', () => {
        console.log("Got userLoggedIn event");
        this.items.filter(elem => elem.userLevel).forEach(elem => this.checkPermission(elem))
      })
      EventBus.$on('userLoggedOut', () => {
        console.log("Got userLoggedIn event");
        this.items.filter(elem => elem.userLevel).forEach(elem => this.checkPermission(elem))
      })
    }
  }
</script>
