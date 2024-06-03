/**
 * ApiError is used to map the error code and 'globalError' message fields
 * of a blogen REST Api error response into a JavaScript Error object
 * code - is the HTTP status code returned by the API
 * message - is the error description returned by the API
 */

export default class ApiError extends Error {
  code: number;

  constructor(code: number, message: string) {
    super(message);
    this.code = code;
    this.name = 'ApiError';
  }
}
