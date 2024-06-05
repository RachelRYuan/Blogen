<template>
  <b-form>
    <b-form-group
      id="categoryNameGroup"
      label="Category Name"
      label-for="categoryName"
      :state="nameValidator.state"
      :invalid-feedback="nameValidator.invalidFeedback"
      :valid-feedback="nameValidator.validFeedback"
    >
      <b-form-input
        v-model="category.name"
        id="categoryName"
        type="text"
        placeholder="Category name"
        :state="nameValidator.state"
        @input="validateName"
        required
      ></b-form-input>
    </b-form-group>

    <b-button type="button" variant="secondary" @click="$emit('cancel')">
      Cancel
    </b-button>
    <b-button
      type="button"
      variant="success"
      @click="submitForm"
      :disabled="!allFieldsValid"
    >
      Submit
    </b-button>
  </b-form>
</template>

<script>
import textLengthValidator from "../../validators/textLengthValidator";

export default {
  name: "CategoryForm",
  props: {
    id: Number,
    name: String,
  },
  data() {
    return {
      // make a copy of the props so we don't mutate them
      category: {
        id: this.id,
        name: this.name,
      },
      nameValidator: { state: null, invalidFeedback: "", validFeedback: "" },
    };
  },
  methods: {
    validateName(val) {
      this.nameValidator = textLengthValidator(val, 3);
    },
    submitForm() {
      if (this.allFieldsValid) {
        this.$emit('submit', this.category);
      }
    }
  },
  computed: {
    allFieldsValid() {
      return this.nameValidator.state === true;
    },
  },
  mounted() {
    // Validate the name initially in case there's a pre-filled value
    this.validateName(this.category.name);
  },
};
</script>

<style scoped></style>
