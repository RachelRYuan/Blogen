// functions to validate that something was selected in a form-select input
export default validate;

interface ValidationResult {
  state: boolean;
  invalidFeedback: string;
  validFeedback: string;
}

function validate(value: any): ValidationResult {
  return {
    state: state(value),
    invalidFeedback: invalidFeedback(value),
    validFeedback: validFeedback(value)
  };
}

function state(value: any): boolean {
  return value !== null;
}

function invalidFeedback(value: any): string {
  if (state(value)) {
    return '';
  } else {
    return 'Please select an option';
  }
}

function validFeedback(value: any): string {
  return 'Looks Good';
}
