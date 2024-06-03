// functions to validate the length of a text string
export default validate;

interface ValidationResult {
  state: boolean;
  invalidFeedback: string;
  validFeedback: string;
}

function validate(value: string, minLength: number, maxLength: number = 999): ValidationResult {
  return {
    state: state(value, minLength, maxLength),
    invalidFeedback: invalidFeedback(value, minLength, maxLength),
    validFeedback: validFeedback(value, minLength, maxLength)
  };
}

function state(value: string, minLength: number, maxLength: number): boolean {
  return value.length >= minLength && value.length <= maxLength;
}

function invalidFeedback(value: string, minLength: number, maxLength: number): string {
  if (state(value, minLength, maxLength)) {
    return '';
  } else if (value.length < minLength) {
    return `Please enter at least ${minLength} characters`;
  } else if (value.length > maxLength) {
    return `Please enter no more than ${maxLength} characters`;
  } else {
    return 'Please enter something';
  }
}

function validFeedback(value: string, minLength: number, maxLength: number): string {
  return 'Looks Good';
}
