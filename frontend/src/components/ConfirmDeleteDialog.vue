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
          <h5 class="modal-title" id="modalDeleteLabel">
            {{ $t("components.confirmDeleteDialog.title") }}
          </h5>
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
          {{ $t("components.confirmDeleteDialog.content") }}
        </div>
        <div
          class="alert alert-danger alert-dismissible fade show"
          v-if="error"
          role="alert"
        >
          <strong>{{ $t("errors.title") }} :</strong> {{ getErrorMsg(error) }}
        </div>
        <div class="modal-footer">
          <button
            type="button"
            class="btn btn-danger"
            data-dismiss="modal"
            :disabled="loading"
          >
            {{ $t("components.confirmDeleteDialog.cancel") }}
          </button>
          <button
            type="button"
            class="btn btn-success"
            @click="btnDeleteImage($event)"
            :disabled="loading"
          >
            {{ $t("components.confirmDeleteDialog.validate") }}
            <span
              v-if="loading"
              class="spinner-border spinner-border-sm"
              role="status"
              aria-hidden="true"
            ></span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import httpApi from "../http-api.js";
import { getErrorMsg } from "../i18n";

export default {
  name: "ImageDelete",
  props: {
    id: { required: true, type: Number },
  },
  data() {
    return {
      getErrorMsg,
      loading: false,
      error: null,
    };
  },
  methods: {
    btnDeleteImage() {
      this.loading = true;
      httpApi
        .delete_image(this.id)
        .then(() => {
          this.loading = false;

          // Close modal
          document.body.removeChild(document.querySelector(".modal-backdrop"));

          this.$router.push({ name: "Home" });
        })
        .catch((err) => {
          this.loading = false;
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