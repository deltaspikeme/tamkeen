<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="tamkeenApp.personalityTest.home.createOrEditLabel"
          data-cy="PersonalityTestCreateUpdateHeading"
          v-text="t$('tamkeenApp.personalityTest.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="personalityTest.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="personalityTest.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('tamkeenApp.personalityTest.testName')" for="personality-test-testName"></label>
            <input
              type="text"
              class="form-control"
              name="testName"
              id="personality-test-testName"
              data-cy="testName"
              :class="{ valid: !v$.testName.$invalid, invalid: v$.testName.$invalid }"
              v-model="v$.testName.$model"
              required
            />
            <div v-if="v$.testName.$anyDirty && v$.testName.$invalid">
              <small class="form-text text-danger" v-for="error of v$.testName.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('tamkeenApp.personalityTest.description')"
              for="personality-test-description"
            ></label>
            <textarea
              class="form-control"
              name="description"
              id="personality-test-description"
              data-cy="description"
              :class="{ valid: !v$.description.$invalid, invalid: v$.description.$invalid }"
              v-model="v$.description.$model"
            ></textarea>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" @click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.cancel')"></span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="v$.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.save')"></span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./personality-test-update.component.ts"></script>
