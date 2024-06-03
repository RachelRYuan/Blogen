// import the configured axios instance
import axios from '../axios-auth';
import ApiError from './ApiError';

/**
 * calls the REST API to log in a blogen user using their username and password.
 * @param username {string}
 * @param password {string}
 * @returns {Promise<string>} the users jwt if the login was successful, else
 * an ApiError is thrown with the shape:
 * { code, message } where code is the error status code from the api, and message
 * is a human-readable description of the message
 */
export async function loginByUsername(username: string, password: string): Promise<string> {
  return axios.post('/login/form', { username, password })
    .then(res => {
      const token = res.headers.authorization.split(' ')[1];
      console.log('received access token:', token);
      return token;
    })
    .catch(error => {
      console.error(error);
      throw mapAxiosErrorToApiError(error);
    });
}

/**
 * calls the REST API to retrieve authenticated user information.
 * The user's JWT must be set in the VUEX store.
 * @returns {Promise<AxiosResponse<any>>}
 */
export async function getAuthenticatedUserInfo(): Promise<any> {
  return axios.get('/api/v1/users/authenticate')
    .then(res => res)
    .catch(error => {
      console.error(error);
      throw mapAxiosErrorToApiError(error);
    });
}

/**
 * calls the REST API to fetch an end user's information using their blogen id
 * @param id {string}
 * @returns {Promise<AxiosResponse<any>>}
 */
export async function getUserInfoById(id: string): Promise<any> {
  return axios.get(`/api/v1/users/${id}`)
    .then(res => res)
    .catch(error => {
      console.error(error);
      throw mapAxiosErrorToApiError(error);
    });
}

/**
 * call the REST API to fetch a list of avatar filenames currently in use by end users.
 * @returns {Promise<AxiosResponse<any>>}
 */
export async function getAvatarFileNames(): Promise<any> {
  return axios.get('/api/v1/userPrefs/avatars')
    .then(res => res)
    .catch(error => {
      console.error(error);
      throw mapAxiosErrorToApiError(error);
    });
}

/**
 * call the REST API to fetch a page of posts for the specified user id
 * @param id {string} - the user's id
 * @param pageNum {number} - the page number of posts to return
 * @param pageLimit {number} - the number of posts to retrieve per page
 * @param categoryId {string} - the category id of the posts
 * @returns {Promise<AxiosResponse<any>>}
 */
export async function getPostsByUserId(id: string, pageNum: number, pageLimit: number, categoryId: string): Promise<any> {
  return axios.get(`/api/v1/users/${id}/posts`, {
    params: {
      page: pageNum,
      limit: pageLimit,
      category: categoryId
    }
  })
    .then(res => res)
    .catch(error => {
      console.error(error);
      throw mapAxiosErrorToApiError(error);
    });
}

/**
 * call the REST API to delete a post by its primary id
 * @param id {string} - the primary id of the post
 */
export async function deletePostById(id: string): Promise<any> {
  return axios.delete(`/api/v1/posts/${id}`)
    .then(res => res)
    .catch(error => {
      console.error(error);
      throw mapAxiosErrorToApiError(error);
    });
}

/**
 * update an existing post with data from the passed in post
 * @param id {string} - the id of the post to be updated
 * @param updatedPost {object} - the post containing properties to be updated
 * @returns {Promise<AxiosResponse<any>>}
 */
export async function putPostById(id: string, updatedPost: object): Promise<any> {
  return axios.put(`/api/v1/posts/${id}`, updatedPost)
    .then(res => res)
    .catch(error => {
      console.error(error);
      throw mapAxiosErrorToApiError(error);
    });
}

/**
 * create a new post thread (i.e. a "parent post"), or a new "child" post
 * @param id {string | null | undefined} - if id is not null, then we are creating a child post, and the id is the id of the parent post,
 * if id is undefined or null, then we are creating a parent post
 * @param newPost {object} - object containing the properties of the post to be created
 * @returns {Promise<AxiosResponse<any>>}
 */
export async function createPost(id: string | null | undefined, newPost: object): Promise<any> {
  const url = id ? `/api/v1/posts/${id}` : '/api/v1/posts';
  return axios.post(url, newPost)
    .then(res => res)
    .catch(error => {
      console.error(error);
      throw mapAxiosErrorToApiError(error);
    });
}

/**
 * fetches a page worth of blogen posts for a specific category
 * @param pageNum {number} - the page number to fetch
 * @param pageLimit {number} - the number of posts to fetch per page
 * @param categoryId {string} - the categoryId of the posts, if this is equal to -1, then all posts across all
 * categories will be fetched
 * @returns {Promise<AxiosResponse<any>>}
 */
export async function getPostsByCategory(pageNum: number, pageLimit: number, categoryId: string): Promise<any> {
  return axios.get('/api/v1/posts', {
    params: {
      page: pageNum,
      limit: pageLimit,
      category: categoryId
    }
  })
    .then(res => res)
    .catch(error => {
      console.error(error);
      throw mapAxiosErrorToApiError(error);
    });
}

/**
 * update an existing category with data from the updatedCategory object
 * @param id {string} - the id of the category to be updated
 * @param updatedCategory {object} - category object containing the properties to update
 * @returns {Promise<AxiosResponse<any>>}
 */
export async function putCategoryById(id: string, updatedCategory: object): Promise<any> {
  return axios.put(`/api/v1/categories/${id}`, updatedCategory)
    .then(res => res)
    .catch(error => {
      console.error(error);
      throw mapAxiosErrorToApiError(error);
    });
}

/**
 * create a new category using the properties of the newCategory
 * @param newCategory {object} - a category object to be created
 * @returns {Promise<AxiosResponse<any>>}
 */
export async function createCategory(newCategory: object): Promise<any> {
  return axios.post('/api/v1/categories', newCategory)
    .then(res => res)
    .catch(error => {
      console.error(error);
      throw mapAxiosErrorToApiError(error);
    });
}

export async function getAllCategoriesByPage(pageNum: number, pageLimit: number): Promise<any> {
  return axios.get('/api/v1/categories', {
    params: {
      page: pageNum,
      limit: pageLimit
    }
  })
    .then(res => res)
    .catch(error => {
      console.error(error);
      throw mapAxiosErrorToApiError(error);
    });
}

/**
 * call the REST API to clear a users session with the backend
 * @returns {Promise<AxiosResponse<any>>}
 */
export async function logout(): Promise<any> {
  return axios.get('/logout')
    .then(res => res)
    .catch(error => {
      console.error(error);
      throw mapAxiosErrorToApiError(error);
    });
}

/**
 * map a Blogen REST Api error JSON object to an ApiError object:
 *
 * @param error {any} - the API error JSON
 * @returns {ApiError}
 */
function mapAxiosErrorToApiError(error: any): ApiError {
  return new ApiError(error.response.status, error.response.data.globalError[0].message);
}
