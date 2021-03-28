<template>
  <!-- Modal -->
  <div
    class="modal fade"
    id="modalDelete"
    tabindex="-1"
    role="dialog"
    aria-labelledby="modalDeleteLabel"
    aria-hidden="true"
  >
    <div class="modal-dialog modal-dialog-centered" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="modalDeleteLabel">Delete image</h5>
          <button
            type="button"
            class="close"
            data-dismiss="modal"
            aria-label="Close"
          >
            <span aria-hidden="true">&times;</span>
          </button>
        </div>
        <div class="modal-body text-center">
          Are you sure to delete this image ?
        </div>
        <div
          class="alert alert-danger alert-dismissible fade show"
          v-if="error"
          role="alert"
        >
          <strong>Error :</strong> {{ error.message }}
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-danger" data-dismiss="modal">
            No
          </button>
          <button
            type="button"
            class="btn btn-success"
            @click="btnDeleteImage($event)"
          >
            Yes
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import httpApi from "../http-api.js";
export default {
  name: "ImageDelete",
  props: {
    id: { required: true, type: Number },
  },
    data() {
    return {
      error: null
    };
  },
  methods: {
    btnDeleteImage() {
      httpApi
        .delete_image(this.id)
        .then(() => {
          // Close modal
          document.body.removeChild(document.querySelector(".modal-backdrop"));

          this.$router.push({ name: "Home" });
        })
        .catch((err) => {
          this.error = err;
        });
    },
  },
};
</script>

<style scoped>
.btn {
  border-radius: 100%;
}
</style>