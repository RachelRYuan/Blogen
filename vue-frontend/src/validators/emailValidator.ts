// functions to validate email
export default validate;

interface ValidationResult {
  state: boolean;
  invalidFeedback: string;
  validFeedback: string;
}

function validate(value: string): ValidationResult {
  return {
    state: state(value),
    invalidFeedback: invalidFeedback(value),
    validFeedback: validFeedback(value)
  };
}

function state(value: string): boolean {
  return validateEmail(value);
}

function invalidFeedback(value: string): string {
  if (validateEmail(value)) {
    return '';
  } else {
    return 'Please enter a valid email address';
  }
}

function validFeedback(value: string): string {
  return 'Looks Good';
}

function validateEmail(email: string): boolean {
  // eslint-disable-next-line
  const re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
  return re.test(String(email).toLowerCase());
}
