<template>
  <div class="card">
    <div class="card-body">
      <h5 class="card-title">{{ algo.title }}</h5>
      <form @submit="applyAlgorithmSubmit($event)">
        <input type="hidden" name="algorithm" :value="algo.name" required />
        <div class="form-group row" v-for="arg in algo.args" :key="arg.name">
          <label class="col-sm-6 col-form-label">{{ arg.title }}</label>
          <div class="col-sm-6">
            <input
              :type="arg.type"
              class="form-control"
              :placeholder="arg.title"
              :title="arg.title"
              :name="arg.name"
              :min="arg.min"
              :max="arg.max"
              :required="arg.required"
            />
          </div>
        </div>
        <div class="form-row">
          <span class="col-sm-6"></span>
          <div class="col-sm-6">
            <button type="submit" class="btn btn-primary mx-4">Apply</button>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
export default {
  name: "AlgorithmMenuItem",
  props: {
    imageId: { required: true, type: Number },
    algo: { required: true, type: Object },
  },
  methods: {
    applyAlgorithmSubmit(e) {
      e.preventDefault();

      let query = new URLSearchParams();
      for (const field of e.target.elements)
        if (field.name && (field.required || field.value))
          query.append(field.name, field.value);

      let queryString = query.toString();

      console.log(queryString);
      // TODO: call "/images/" + this.imageId + "?" + queryString
    },
  },
};
</script>