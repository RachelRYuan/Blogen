import Vue from 'vue';
import Vuex, { StoreOptions } from 'vuex';
import axios from '../axios-auth';
import { handleAxiosError } from '../common/errorHandlers';
import {
  createCategory,
  createPost,
  deletePostById,
  getAllCategoriesByPage,
  getAuthenticatedUserInfo,
  getAvatarFileNames,
  getPostsByCategory,
  getPostsByUserId,
  getUserInfoById,
  loginByUsername,
  logout,
  putCategoryById,
  putPostById
} from '../api/blogen-api';

Vue.use(Vuex);

interface User {
  id: number;
  firstName: string;
  lastName: string;
  userName: string;
  email: string;
  password: string;
  avatarImage: string;
  roles: string[];
}

interface Category {
  id: number;
  name: string;
  categoryUrl: string;
}

interface Post {
  id: number;
  text: string;
  title: string;
  created: string;
  imageUrl: string;
  user: object;
  category: object;
  postUrl: string;
  parentPostUrl: string | null;
  children: Post[];
}

interface PageInfo {
  totalElements: number;
  totalPages: number;
  pageNumber: number;
  pageSize: number;
}

interface State {
  AUTH_TOKEN: string;
  user: User;
  categories: Category[];
  posts: Post[];
  pageInfo: PageInfo;
  avatars: string[];
}

const store: StoreOptions<State> = {
  state: {
    AUTH_TOKEN: '',
    user: {
      id: 0,
      firstName: '',
      lastName: '',
      userName: '',
      email: '',
      password: '',
      avatarImage: '',
      roles: []
    },
    categories: [
      { id: 0, name: '', categoryUrl: '' }
    ],
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
    pageInfo: {
      totalElements: 0,
      totalPages: 0,
      pageNumber: 0,
      pageSize: 0
    },
    avatars: []
  },
  getters: {
    getAuthToken: state => state.AUTH_TOKEN,
    getAuthUserRoles: state => state.user.roles,
    getAuthUser: state => state.user,
    getAuthUserId: state => state.user.id,
    getCategories: state => state.categories,
    getAvatarUrlByFileName: state => (fileName: string) => process.env.VUE_APP_DEFAULT_AVATAR_URL + '/' + fileName,
    getPostById: state => (id: number) => {
      for (const post of state.posts) {
        if (post.id === id) {
          return post;
        } else {
          const child = post.children.find(c => c.id === id);
          if (child) {
            return child;
          }
        }
      }
    },
    getPostIndicesById: state => (id: number) => {
      const indices = { pi: -1, ci: -1 };
      for (let pi = 0; pi < state.posts.length; pi++) {
        if (state.posts[pi].id === id) {
          indices.pi = pi;
          break;
        } else {
          for (let ci = 0; ci < state.posts[pi].children.length; ci++) {
            if (state.posts[pi].children[ci].id === id) {
              indices.pi = pi;
              indices.ci = ci;
              break;
            }
          }
        }
      }
      return indices;
    },
    isAuthenticated: state => state.AUTH_TOKEN.length > 0,
    isAdmin: state => state.user.roles.includes('ADMIN')
  },
  mutations: {
    'SET_AUTH_TOKEN'(state, token: string) {
      state.AUTH_TOKEN = token;
      axios.defaults.headers.common.Authorization = 'Bearer ' + token;
    },
    'SET_USER'(state, userObj: User) {
      state.user = userObj;
    },
    'LOGOUT'(state) {
      if (state.AUTH_TOKEN.length > 0) {
        state.AUTH_TOKEN = '';
      }
      delete axios.defaults.headers.common.Authorization;
    },
    'RESET_USER'(state) {
      state.user = { id: 0, firstName: '', lastName: '', userName: '', email: '', password: '', avatarImage: '', roles: [] };
    },
    'SET_CATEGORIES'(state, categoriesArr: Category[]) {
      state.categories = categoriesArr;
    },
    'ADD_CATEGORY'(state, category: Category) {
      state.categories.push(category);
    },
    'UPDATE_CATEGORY'(state, updatedCategory: Category) {
      const index = state.categories.findIndex(c => c.id === updatedCategory.id);
      state.categories.splice(index, 1, updatedCategory);
    },
    'SET_POSTS'(state, postsArr: Post[]) {
      state.posts = postsArr;
    },
    'PREPEND_POST'(state, post: Post) {
      state.posts.splice(0, 0, post);
    },
    'ADD_CHILD_POST'(state, { pi, newPost }: { pi: number, newPost: Post }) {
      state.posts[pi].children.splice(0, 0, newPost);
    },
    'UPDATE_POST'(state, { pi, ci, newPost }: { pi: number, ci: number, newPost: Post }) {
      if (ci >= 0) {
        state.posts[pi].children.splice(ci, 1, newPost);
      } else {
        state.posts.splice(pi, 1, newPost);
      }
    },
    'DELETE_POST'(state, { pi, ci }: { pi: number, ci: number }) {
      if (ci >= 0) {
        state.posts[pi].children.splice(ci, 1);
      } else {
        state.posts.splice(pi, 1);
      }
    },
    'SET_PAGE_INFO'(state, pageInfo: PageInfo) {
      state.pageInfo = pageInfo;
    },
    'SET_CURRENT_PAGE_NUM'(state, pageNum: number) {
      state.pageInfo.pageNumber = pageNum;
    },
    'SET_AVATARS'(state, avatarsArr: string[]) {
      state.avatars = avatarsArr;
    }
  },
  actions: {
    updatePostData: ({ commit, getters }, { id, newPost }: { id: number, newPost: Post }) => {
      const updateData = getters.getPostIndicesById(id);
      updateData.newPost = newPost;
      commit('UPDATE_POST', updateData);
    },
    fetchAndStoreCategories: ({ commit }, { pageNum = 0, pageLimit = 20 }: { pageNum: number, pageLimit: number }) => {
      return getAllCategoriesByPage(pageNum, pageLimit)
        .then(res => {
          console.log('fetchAndStoreCategories response:', res.data);
          commit('SET_CATEGORIES', res.data.categories);
        })
        .catch(error => {
          handleAxiosError(error);
        });
    },
    fetchCategories: ({ commit }, { pageNum = 0, pageLimit = 20 }: { pageNum: number, pageLimit: number }) => {
      return getAllCategoriesByPage(pageNum, pageLimit)
        .then(res => {
          console.log('fetchCategories response:', res.data);
          return res.data;
        })
        .catch(error => {
          handleAxiosError(error);
        });
    },
    createCategory: ({ commit }, categoryObj: Category) => {
      console.log('create category with object:', categoryObj);
      return createCategory(categoryObj)
        .then(res => {
          console.log('createCategory response:', res.data);
          commit('ADD_CATEGORY', res.data);
          return res.data;
        })
        .catch(error => {
          handleAxiosError(error);
          throw error;
        });
    },
    updateCategoryById: ({ commit }, categoryObj: Category) => {
      console.log('update category with object:', categoryObj);
      delete categoryObj.categoryUrl;
      return putCategoryById(categoryObj.id, categoryObj)
        .then(res => {
          console.log('updateCategory response:', res.data);
          commit('UPDATE_CATEGORY', res.data);
          return res.data;
        })
        .catch(error => {
          handleAxiosError(error);
          throw error;
        });
    },
    fetchPosts: ({ commit }, { pageNum, pageLimit, categoryId }: { pageNum: number, pageLimit: number, categoryId: string }) => {
      return getPostsByCategory(pageNum, pageLimit, categoryId)
        .then(res => {
          console.log('fetchPosts response:', res.data);
          commit('SET_POSTS', res.data.posts);
          commit('SET_PAGE_INFO', res.data.pageInfo);
        })
        .catch(error => {
          handleAxiosError(error);
        });
    },
    createPost: (context, { id, post }: { id: number | null, post: Post }) => {
      return createPost(id, post)
        .then(res => {
          console.log(`createPost response id:${id} response:`, res.data);
          if (!id) {
            context.commit('PREPEND_POST', res.data);
          } else {
            const indices = context.getters.getPostIndicesById(id);
            indices.newPost = res.data;
            context.commit('UPDATE_POST', indices);
          }
        })
        .catch(error => {
          handleAxiosError(error);
        });
    },
    updatePost: (context, { id, post }: { id: number, post: Post }) => {
      return putPostById(id, post)
        .then(res => {
          console.log(`updatePost ${id} response:`, res.data);
          context.dispatch('updatePostData', { id: id, newPost: res.data });
        })
        .catch(error => {
          handleAxiosError(error);
        });
    },
    deletePost: (context, id: number) => {
      return deletePostById(id)
        .then(res => {
          console.log(`deletePost ${id} response:`, res.data);
          const indices = context.getters.getPostIndicesById(id);
          context.commit('DELETE_POST', indices);
        })
        .catch(error => {
          handleAxiosError(error);
        });
    },
    fetchPostsByUser: (context, { id, pageNum, pageLimit, categoryId }: { id: number, pageNum: number, pageLimit: number, categoryId: string }) => {
      return getPostsByUserId(id, pageNum, pageLimit, categoryId)
        .then(res => {
          console.log(`get posts for user:${id} response:`, res);
          context.commit('SET_POSTS', res.data.posts);
          context.commit('SET_PAGE_INFO', res.data.pageInfo);
          return res.data;
        })
        .catch(error => {
          handleAxiosError(error);
          throw error;
        });
    },
    fetchAvatarFileNames: ({ commit }) => {
      return getAvatarFileNames()
        .then(res => {
          console.log('fetch avatar file name response:', res);
          commit('SET_AVATARS', res.data.avatars);
        })
        .catch(error => {
          handleAxiosError(error);
          throw error;
        });
    },
    fetchUserInfo: (context, id: number) => {
      return getUserInfoById(id)
        .then(res => {
          console.log('fetch user info response:', res);
          return res.data;
        })
        .catch(error => {
          throw error;
        });
    },
    fetchAuthenticatedUserInfo: ({ commit }) => {
      return getAuthenticatedUserInfo()
        .then(res => {
          console.log('got authenticated user data:', res.data);
          commit('SET_USER', res.data);
        })
        .catch(error => {
          throw error;
        });
    },
    loginWithUsername: ({ commit, dispatch }, { username, password }: { username: string, password: string }) => {
      return loginByUsername(username, password)
        .then(token => {
          commit('SET_AUTH_TOKEN', token);
          return token;
        })
        .then(_ => {
          return dispatch('fetchAuthenticatedUserInfo');
        })
        .catch(apiError => {
          commit('LOGOUT');
          commit('RESET_USER');
          throw apiError;
        });
    },
    validateToken: ({ commit, dispatch }, token: string) => {
      commit('SET_AUTH_TOKEN', token);
      axios.defaults.headers.common.Authorization = 'Bearer ' + token;
      return dispatch('fetchAuthenticatedUserInfo')
        .then((res) => res)
        .catch(error => {
          commit('LOGOUT');
          commit('RESET_USER');
          throw error;
        });
    },
    doLogout: ({ commit }) => {
      commit('LOGOUT');
      commit('RESET_USER');
      return logout()
        .then(res => res)
        .catch(err => {
          console.log('error during logout:', err);
          throw err;
        });
    }
  }
};

export const storeInstance = new Vuex.Store(store);
