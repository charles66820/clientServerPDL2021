<template>
  <div class="card">
    <div class="card-body">
      <h5 class="card-title">{{ algo.title }}</h5>
      <form>
        <!-- TODO: add args support -->
        <div class="form-group row">
          <label class="col-sm-6 col-form-label">Value</label>
          <div class="col-sm-6">
            <input
              type="text"
              class="form-control"
              placeholder="Value"
              required
            />
          </div>
        </div>
        <div class="form-row">
          <span class="col-sm-6"></span>
          <div class="col-sm-6">
            <button class="btn btn-primary mx-4">Apply</button>
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
    algo: {required: true, type: Object},
  },
};
</script>