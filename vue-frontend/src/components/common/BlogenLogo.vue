<template>
  <img
    src="@/assets/images/blogen-logo.png"
    :width="width"
    :height="height"
    class="d-inline-block align-top"
    alt="Blogen Logo"
  />
</template>

<script>
export default {
  name: "BlogenLogo",
  props: {
    height: {
      type: String,
      required: true,
    },
    width: {
      type: String,
      required: true,
    },
  },
};
</script>

<style scoped></style>
