<template>
  <!-- Main Splash Page recent posts -->
  <b-card-group columns class="mt-4">
    <app-post-card
      v-for="post in posts"
      :key="post.id"
      v-bind="post"
    ></app-post-card>
  </b-card-group>
</template>

<script>
import PostCard from "./PostCard";
import axios from "../../axios-auth";
import { handleAxiosError } from "../../common/errorHandlers";

export default {
  name: "PostCards",
  data() {
    return {
      posts: [],
    };
  },
  methods: {
    getLatestPosts() {
      axios
        .get("/api/v1/auth/latestPosts")
        .then((res) => {
          console.log("api response:", res.data);
          this.posts = this.posts.concat(res.data.posts);
        })
        .catch((error) => {
          handleAxiosError(error);
        });
    },
  },
  components: {
    appPostCard: PostCard,
  },
  created() {
    this.getLatestPosts();
  },
};
</script>

<style scoped></style>
