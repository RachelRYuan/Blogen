<template>
  <div>
    <!-- Header -->
    <header id="categoryHeader" class="py-2 bg-success text-white">
      <div class="container">
        <div class="row">
          <div class="col-md-6">
            <h1>
              <font-awesome-icon
                class="mx-2"
                icon="folder"
                scale="2"
              ></font-awesome-icon>
              Categories
            </h1>
          </div>
        </div>
      </div>
    </header>

    <!-- New Category Button -->
    <div class="container">
      <div class="row mt-4">
        <div class="col-md-4 offset-md-4">
          <app-new-category @submit="createCategory"></app-new-category>
        </div>
      </div>
      <!-- Category Status Message -->
      <div class="row justify-content-center pt-2">
        <app-status-alert
          v-bind="status"
          @dismissed="dismissStatusAlert"
        ></app-status-alert>
      </div>

      <!-- Categories Table -->
      <div class="row my-4">
        <div class="col">
          <b-card no-body border-variant="success">
            <b-card-header>
              <h2>Categories</h2>
            </b-card-header>
            <b-card-body>
              <table class="table table-bordered table-striped table-hover">
                <thead>
                  <tr>
                    <th scope="col">ID</th>
                    <th scope="col">Name</th>
                    <th scope="col"></th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="cat in categories" :key="cat.id">
                    <td>{{ cat.id }}</td>
                    <td>{{ cat.name }}</td>
                    <td>
                      <transition appear name="fade" mode="out-in">
                        <app-edit-category
                          v-bind="cat"
                          @submit="editCategory"
                        ></app-edit-category>
                      </transition>
                    </td>
                  </tr>
                </tbody>
              </table>
            </b-card-body>

            <b-card-footer>
              <!-- Pagination -->
              <b-pagination
                class="tpage"
                v-model="tableCurrentPage"
                :total-rows="pageInfo.totalElements"
                :per-page="pageInfo.pageSize"
                @change="fetchPage"
              ></b-pagination>
            </b-card-footer>
          </b-card>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import NewCategory from "./NewCategory";
import StatusAlert from "../common/StatusAlert";
import EditCategory from "./EditCategory";

export default {
  name: "Categories",
  components: {
    appNewCategory: NewCategory,
    appStatusAlert: StatusAlert,
    appEditCategory: EditCategory,
  },
  data() {
    return {
      status: {
        code: 200,
        message: "",
        show: false,
      },
      tableCurrentPage: 1,
      categories: [],
      pageInfo: {
        pageNumber: 0,
        totalElements: 0,
        totalPages: 0,
        pageSize: 4,
      },
    };
  },
  methods: {
    async fetchCategories(pageNum, pageLimit) {
      try {
        const data = await this.$store.dispatch("fetchCategories", { pageNum, pageLimit });
        this.categories = data.categories;
        this.pageInfo = data.pageInfo;
      } catch (error) {
        console.error(`Failed to fetch categories: ${error}`);
      }
    },
    fetchPage(page) {
      this.fetchCategories(page - 1, this.pageInfo.pageSize);
    },
    async createCategory(newCategory) {
      try {
        await this.$store.dispatch("createCategory", newCategory);
        this.status = { code: 200, message: `Category: ${newCategory.name} created`, show: true };
        this.tableCurrentPage = 1;
        this.fetchPage(1);
      } catch (apiError) {
        this.status = { code: apiError.code, message: apiError.message, show: true };
      }
    },
    displayStatusAlert() {
      this.status.show = true;
    },
    dismissStatusAlert() {
      this.status.show = false;
    },
    async editCategory(cat) {
      try {
        await this.$store.dispatch("updateCategory", cat);
        const idx = this.categories.findIndex((c) => c.id === cat.id);
        this.categories.splice(idx, 1, cat);
      } catch (apiError) {
        this.status = { code: apiError.code, message: apiError.message, show: true };
      }
    },
  },
  created() {
    this.fetchCategories(0, this.pageInfo.pageSize);
  },
};
</script>

<style>
.fade-enter {
  opacity: 0;
}
.fade-enter-active,
.fade-leave-active {
  transition: opacity 1s;
}
.fade-leave-to {
  opacity: 0;
}
.slide-enter-active {
  animation: slide-in 500ms ease-out forwards;
}
.slide-leave-active {
  animation: slide-out 500ms ease-out forwards;
}
@keyframes slide-in {
  from {
    transform: translateX(50px);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}
@keyframes slide-out {
  from {
    transform: translateX(0);
    opacity: 1;
  }
  to {
    transform: translateX(50px);
    opacity: 0;
  }
}
</style>
