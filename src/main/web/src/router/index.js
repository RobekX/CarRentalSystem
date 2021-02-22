import Vue from 'vue'
import Router from 'vue-router'
import Landing from '@/components/Landing'
import MainCarView from '@/components/car/MainCarView'
import Reports from '@/components/reports/Reports'
import User from '@/components/user/User'
import Logout from '@/components/user/Logout'
import Login from '@/components/user/Login'
import Register from '@/components/user/Register'
import RentACar from '@/components/car/RentACar'
import FindNearestCar from '@/components/car/FindNearestCar'
import AddCar from '@/components/car/AddCar'
import UserCarsView from '@/components/car/UserCarsView'
import UserRentals from '@/components/car/UserRentals'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'Landing',
      component: Landing
    }, {
      path: '/cars',
      name: 'CarCardList',
      component: MainCarView
    }, {
      path: '/userCars',
      name: 'UserCarsView',
      component: UserCarsView
    }, {
      path: '/reports',
      name: 'Reports',
      component: Reports
    }, {
      path: '/account',
      name: 'User',
      component: User
    }, {
      path: '/logout',
      name: 'Logout',
      component: Logout
    }, {
      path: '/login',
      name: 'Login',
      component: Login
    }, {
      path: '/rent/:id',
      name: 'RentACar',
      component: RentACar,
      props: true
    }, {
      path: '/findNearestCar',
      name: 'FindNearestCar',
      component: FindNearestCar
    }, {
      path: '/addCar',
      name: 'AddCar',
      component: AddCar
    }, {
      path: '/myRentals',
      name: 'UserRentals',
      component: UserRentals
    }, {
      path: '/register',
      name: 'Register',
      component: Register
    }
  ]
})
