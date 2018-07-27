import Vue from 'vue'
import Vuex from 'vuex'
import axios from '../axios-auth'
import {logAxiosError} from '../common'

Vue.use(Vuex)

export const store = new Vuex.Store({
  state: {
    AUTH_TOKEN: '',
    // user holds details of the currently logged in user
    user: {
      id: 0,
      firstName: '',
      lastName: '',
      userName: '',
      email: '',
      roles: []
    },
    // categories holds a list of the current blogen categories, they are used across multiple components
    categories: [
      {id: 0, name: '', categoryUrl: ''}
    ],
    // holds a page worth of posts
    posts: [{
      id: 0,
      text: '',
      title: '',
      created: '',
      imageUrl: '',
      user: {},
      category: {},
      postUrl: '',
      parentPostUrl: null,
      children: []
    }],
    pageInfo: {          // pageInfo is returned by the API on each page request
      totalElements: 0,   // total posts available using the current filter criteria
      totalPages: 0,      // total pages available using the current filter criteria
      pageNumber: 0,      // the page number of data that was requested (0 based indices)
      pageSize: 0         // the maximum number of parent posts that will be displayed on a page
    }
  },
  getters: {
    getAuthToken: state => {
      return state.AUTH_TOKEN
    },
    getUserRoles: state => {
      return state.user.roles
    },
    getUser: state => {
      return state.user
    },
    getCategories: state => {
      return state.categories
    },
    getPostById: (state) => (id) => {
      for (const post of state.posts) {
        if (post.id === id) {
          return post
        } else {
          // search child posts
          const child = post.children.find(c => c.id === id)
          if (child) {
            return child
          }
        }
      }
    },
    getPostIndicesById: (state) => (id) => {
      // returns an object of two integers representing the parent index and child index of the post that
      // has the passed in id. If child index is -1, then the post is a parent post
      let indices = {pi: -1, ci: -1}
      state.posts.forEach((post, idx) => {
        if (post.id === id) {
          indices.pi = idx
        } else {
          indices.ci = post.children.findIndex(child => child.id === id)
        }
      })
      return indices
    },
    isAuthenticated: state => {
      return state.AUTH_TOKEN.length > 0
    },
    isAdmin: state => {
      return state.user.roles.includes('ADMIN')
    }
  },
  mutations: {
    'SET_AUTH_TOKEN' (state, token) {
      state.AUTH_TOKEN = token
    },
    'SET_USER' (state, userObj) {
      state.user = userObj
    },
    'LOGOUT' (state) {
      // very basic logout that simply clears the AUTH_TOKEN
      if (state.AUTH_TOKEN.length > 0) {
        state.AUTH_TOKEN = ''
      }
    },
    'RESET_USER' (state) {
      state.user = { id: 0, firstName: '', lastName: '', userName: '', email: '', roles: [] }
    },
    'SET_CATEGORIES' (state, categoriesArr) {
      state.categories = categoriesArr
    },
    'ADD_CATEGORY' (state, category) {
      state.categories.push(category)
    },
    'SET_POSTS' (state, postsArr) {
      state.posts = postsArr
    },
    'PREPEND_POST' (state, post) {
      state.posts.splice(0, 0, post)
    },
    'ADD_CHILD_POST' (state, {pi, newPost}) {
      // add a child post to a parent post, pi is the parent post index
      state.posts[pi].children.splice(0, 0, newPost)
    },
    'UPDATE_POST' (state, {pi, ci, newPost}) {
      // pi is index of parent post to update and ci is index of child post to update
      if (ci >= 0) {
        state.posts[pi].children[ci] = newPost
      } else {
        state.posts[pi] = newPost
      }
    },
    'DELETE_POST' (state, {pi, ci}) {
      if (ci) {
        state.posts[pi].children.splice(ci, 1)
      } else {
        state.posts[pi].splice(pi, 1)
      }
    },
    'SET_PAGE_INFO' (state, pageInfo) {
      state.pageInfo = pageInfo
    }
  },
  actions: {
    saveUserProfile: ({commit}) => {
      // TODO save profile might be done in the component
      // save user profile changes to db
      // axios.put...
      // commit('SET_USER', savedUser );
    },
    doLogout: ({commit}) => {
      commit('LOGOUT')
      commit('RESET_USER')
    },
    updatePostData: ({commit, getters}, {id, newPost}) => {
      let updateData = getters.getPostIndicesById(id)
      updateData.newPost = newPost  // { ci, pi, newPost }
      commit('UPDATE_POST', updateData)
    },
    fetchCategories: ({commit}) => {
      axios.get('/api/v1/categories')
        .then(res => {
          console.log('fetchCategories response:', res.data)
          commit('SET_CATEGORIES', res.data.categories)
        })
        .catch(error => {
          // TODO check for 401 and redirect
          logAxiosError(error)
        })
    },
    fetchPosts: ({commit}, {pageNum, pageLimit, categoryId}) => {
      axios.get('/api/v1/posts', {
        params: {
          page: pageNum,
          limit: pageLimit,
          category: categoryId
        }
      })
        .then(res => {
          console.log('fetchPosts response:', res.data)
          commit('SET_POSTS', res.data.posts)
          commit('SET_PAGE_INFO', res.data.pageInfo)
        })
        .catch(error => {
          // TODO check for 401 and redirect
          logAxiosError(error)
        })
    },
    createPost: (context, {id, post}) => {
      // if id is passed, then we are creating a child post, if id is undefined then we are creating a parent post
      const url = (id) ? `/api/v1/posts/${id}` : '/api/v1/posts'
      axios.post(url, post)
        .then(res => {
          console.log(`createPost url:${url} response:`, res.data)
          if (!id) {
            context.commit('PREPEND_POST', res.data)
          } else {
            let indices = context.getters.getPostIndicesById(id)
            indices.newPost = res.data
            context.commit('UPDATE_POST', indices)
          }
        })
        .catch(error => {
          // TODO check for 401 and redirect
          logAxiosError(error)
        })
    },
    updatePost: (context, {id, post}) => {
      // id and post are mandatory parameters
      const url = `/api/v1/posts/${id}`
      axios.put(url, post)
        .then(res => {
          console.log(`updatePost url:${url} response:`, res.data)
          context.dispatch('updatePostData', {id: id, newPost: res.data})
        })
        .catch(error => {
          // TODO check for 401 and redirect
          logAxiosError(error)
        })
    },
    deletePost: (context, id) => {
      const url = `/api/v1/posts/${id}`
      axios.delete(url)
        .then(res => {
          console.log(`deletePost url:${url} response:`, res.data)
          const indices = context.getters.getPostIndicesById(id)
          context.commit('DELETE_POST', indices)
        })
        .catch(error => {
          // TODO check for 401 and redirect
          logAxiosError(error)
        })
    }
  }
})
