import axios from '../axios-auth';

interface ValidationResult {
  state: boolean;
  invalidFeedback: string;
  validFeedback: string;
}

export default validate;

function validate(value: string): Promise<ValidationResult> {
  return checkUserNameExists(value);
}

function checkUserNameExists(value: string): Promise<ValidationResult> {
  return axios.get(`/api/v1/auth/username/${value}`)
    .then(res => {
      // user name exists
      if (res.data === true) {
        console.log(`user name is taken: ${value}`, res.status);
        return {
          state: false,
          invalidFeedback: 'User Name is taken',
          validFeedback: ''
        };
      } else if (res.data === false) {
        return {
          state: true,
          invalidFeedback: '',
          validFeedback: 'User Name is available'
        };
      } else {
        throw new Error(`unknown response returned from api: ${res.data}`);
      }
    })
    .catch(error => {
      console.error(error);
      return {
        state: false,
        invalidFeedback: 'Error checking user name',
        validFeedback: ''
      };
    });
}
