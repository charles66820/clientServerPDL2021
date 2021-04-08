<template>
  <div class="card">
    <div class="card-body">
      <h5 class="card-title">{{ algo.title }}</h5>
      <form @submit="applyAlgorithmSubmit($event)">
        <input type="hidden" name="algorithm" :value="algo.name" required />
        <div class="form-group row" v-for="arg in algo.args" :key="arg.name">
          <label class="col-sm-6 col-form-label">{{ arg.title }}</label>
          <div class="col-sm-6">
            <select
              v-if="arg.type == 'select'"
              class="form-control"
              :name="arg.name"
              :required="arg.required"
            >
              <option
                v-for="option in arg.options"
                :key="option.name"
                :value="option.name"
                :title="option.title"
              >
                {{ option.title }}
              </option>
            </select>
            <input
              v-else
              :type="arg.type"
              class="form-control"
              :placeholder="arg.title"
              :title="arg.title"
              :name="arg.name"
              :min="arg.min"
              :max="arg.max"
              :required="arg.required"
              step="0.01"
            />
          </div>
        </div>
        <div
          class="alert alert-danger alert-dismissible fade show"
          v-for="err in errors"
          :key="err.type"
          role="alert"
        >
          <strong>{{ t("errors.title") }} :</strong> {{ getErrorMsg(err) }}
        </div>
        <div class="form-row">
          <span class="col-sm-6"></span>
          <div class="col-sm-6">
            <button type="submit" class="btn btn-primary mx-4">
              {{ t("components.image.algorithm.apply") }}
            </button>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import { useI18n } from "vue-i18n";
import httpApi from "../http-api.js";

export default {
  name: "AlgorithmMenuItem",
  props: {
    imageId: { required: true, type: Number },
    algo: { required: true, type: Object },
  },
  data() {
    return {
      t: useI18n({ useScope: "global" }).t,
      errors: [],
    };
  },
  methods: {
    applyAlgorithmSubmit(e) {
      e.preventDefault();

      let query = new URLSearchParams();
      for (const field of e.target.elements)
        if (field.name && (field.required || field.value))
          query.append(field.name, field.value);

      let queryString = query.toString();

      httpApi
        .get_image_with_algo(this.imageId, queryString)
        .then((res) => {
          this.errors = [];
          this.$parent.showProcessedImage(res.data);
        })
        .catch((err) => {
          this.errors = [];
          this.errors.push(err);
        });
    },
    getErrorMsg(err) {
      return err.response != null &&
        err.response.headers["content-type"] == "text/plain"
        ? err.response.data
        : err.message;
    },
  },
};
</script>