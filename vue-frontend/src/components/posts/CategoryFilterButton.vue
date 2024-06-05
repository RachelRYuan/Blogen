<template>
  <b-dropdown
    id="categoryFilterButton"
    text="Filter By Category"
    variant="success"
  >
    <b-dropdown-item
      v-for="cat in categories"
      :key="cat.id"
      @click="$emit('input', cat)"
    >
      {{ cat.name }}
    </b-dropdown-item>
  </b-dropdown>
</template>

<script>
export default {
  name: "CategoryFilterButton",
  props: {
    value: {
      type: Object,
      default: () => ({}),
    },
  },
  computed: {
    categories() {
      return [{ id: -1, name: "All", categoryUrl: "" }, ...this.$store.state.categories];
    },
  },
};
</script>

<style scoped></style>
