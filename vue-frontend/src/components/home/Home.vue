<template>
  <div id="homePage" class="bg-secondary">
    <header id="welcome-head-section" class="bg-primary text-white">
      <div class="container">
        <div class="row">
          <div class="col text-center">
            <h1 class="display-4">Welcome To Blogen</h1>
            <p class="lead">Share your thoughts on a variety of topics</p>
          </div>
        </div>
      </div>
    </header>

    <!-- MAIN PAGE HEADER -->
    <section id="main-header" class="bg-secondary text-white">
      <div class="container">
        <div class="row">
          <div class="col-md text-md-left">
            <h3>
              <font-awesome-icon
                icon="keyboard"
                scale="1.5"
              ></font-awesome-icon>
              Latest Blogens:
            </h3>
          </div>
        </div>
      </div>
    </section>

    <!-- CARD COLUMNS -->
    <section id="cards">
      <app-post-cards></app-post-cards>
    </section>
  </div>
</template>

<script>
import PostCards from "./PostCards";

export default {
  name: "HomePage",
  components: {
    appPostCards: PostCards,
  },
};
</script>

<style scoped>
#homePage {
  background-color: #6c757d; /* Adjusted to match bg-secondary */
}
#welcome-head-section {
  background-color: #007bff; /* Adjusted to match bg-primary */
  color: #fff;
}
#main-header {
  background-color: #6c757d; /* Adjusted to match bg-secondary */
  color: #fff;
}
</style>
