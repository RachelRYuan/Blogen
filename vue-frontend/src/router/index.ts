import Vue from 'vue';
import Router, { RouteConfig } from 'vue-router';
import Home from '../components/home/Home.vue';
import Login from '../components/login/Login.vue';
import Signup from '../components/signup/Signup.vue';
import UserProfile from '../components/profile/UserProfile.vue';
import Posts from '../components/posts/Posts.vue';
import Categories from '../components/categories/Categories.vue';
import Users from '../components/users/Users.vue';

Vue.use(Router);

const routes: RouteConfig[] = [
  { path: '/', name: 'home', component: Home },
  { path: '/login', name: 'login', component: Login, props: true },
  { path: '/users', name: 'users', component: Users, props: true },
  { path: '/userProfile', name: 'userProfile', component: UserProfile },
  { path: '/signup', name: 'signup', component: Signup },
  { path: '/posts', name: 'posts', component: Posts },
  { path: '/categories', name: 'categories', component: Categories },
  { path: '*', redirect: '/' }
];

export default new Router({
  routes,
  mode: 'history'
});
