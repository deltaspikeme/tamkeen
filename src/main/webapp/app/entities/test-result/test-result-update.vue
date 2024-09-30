<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="tamkeenApp.testResult.home.createOrEditLabel"
          data-cy="TestResultCreateUpdateHeading"
          v-text="t$('tamkeenApp.testResult.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="testResult.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="testResult.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('tamkeenApp.testResult.analysis')" for="test-result-analysis"></label>
            <textarea
              class="form-control"
              name="analysis"
              id="test-result-analysis"
              data-cy="analysis"
              :class="{ valid: !v$.analysis.$invalid, invalid: v$.analysis.$invalid }"
              v-model="v$.analysis.$model"
            ></textarea>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('tamkeenApp.testResult.resultDate')" for="test-result-resultDate"></label>
            <div class="d-flex">
              <input
                id="test-result-resultDate"
                data-cy="resultDate"
                type="datetime-local"
                class="form-control"
                name="resultDate"
                :class="{ valid: !v$.resultDate.$invalid, invalid: v$.resultDate.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.resultDate.$model)"
                @change="updateInstantField('resultDate', $event)"
              />
            </div>
            <div v-if="v$.resultDate.$anyDirty && v$.resultDate.$invalid">
              <small class="form-text text-danger" v-for="error of v$.resultDate.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('tamkeenApp.testResult.user')" for="test-result-user"></label>
            <select class="form-control" id="test-result-user" data-cy="user" name="user" v-model="testResult.user">
              <option :value="null"></option>
              <option
                :value="testResult.user && userOption.id === testResult.user.id ? testResult.user : userOption"
                v-for="userOption in users"
                :key="userOption.id"
              >
                {{ userOption.id }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('tamkeenApp.testResult.personalityTest')"
              for="test-result-personalityTest"
            ></label>
            <select
              class="form-control"
              id="test-result-personalityTest"
              data-cy="personalityTest"
              name="personalityTest"
              v-model="testResult.personalityTest"
            >
              <option :value="null"></option>
              <option
                :value="
                  testResult.personalityTest && personalityTestOption.id === testResult.personalityTest.id
                    ? testResult.personalityTest
                    : personalityTestOption
                "
                v-for="personalityTestOption in personalityTests"
                :key="personalityTestOption.id"
              >
                {{ personalityTestOption.id }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('tamkeenApp.testResult.personalityType')"
              for="test-result-personalityType"
            ></label>
            <select
              class="form-control"
              id="test-result-personalityType"
              data-cy="personalityType"
              name="personalityType"
              v-model="testResult.personalityType"
            >
              <option :value="null"></option>
              <option
                :value="
                  testResult.personalityType && personalityTypeOption.id === testResult.personalityType.id
                    ? testResult.personalityType
                    : personalityTypeOption
                "
                v-for="personalityTypeOption in personalityTypes"
                :key="personalityTypeOption.id"
              >
                {{ personalityTypeOption.id }}
              </option>
            </select>
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
<script lang="ts" src="./test-result-update.component.ts"></script>
