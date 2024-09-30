<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="tamkeenApp.personalityType.home.createOrEditLabel"
          data-cy="PersonalityTypeCreateUpdateHeading"
          v-text="t$('tamkeenApp.personalityType.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="personalityType.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="personalityType.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('tamkeenApp.personalityType.typeCode')" for="personality-type-typeCode"></label>
            <input
              type="text"
              class="form-control"
              name="typeCode"
              id="personality-type-typeCode"
              data-cy="typeCode"
              :class="{ valid: !v$.typeCode.$invalid, invalid: v$.typeCode.$invalid }"
              v-model="v$.typeCode.$model"
              required
            />
            <div v-if="v$.typeCode.$anyDirty && v$.typeCode.$invalid">
              <small class="form-text text-danger" v-for="error of v$.typeCode.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('tamkeenApp.personalityType.description')"
              for="personality-type-description"
            ></label>
            <textarea
              class="form-control"
              name="description"
              id="personality-type-description"
              data-cy="description"
              :class="{ valid: !v$.description.$invalid, invalid: v$.description.$invalid }"
              v-model="v$.description.$model"
            ></textarea>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('tamkeenApp.personalityType.strengths')" for="personality-type-strengths"></label>
            <textarea
              class="form-control"
              name="strengths"
              id="personality-type-strengths"
              data-cy="strengths"
              :class="{ valid: !v$.strengths.$invalid, invalid: v$.strengths.$invalid }"
              v-model="v$.strengths.$model"
            ></textarea>
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('tamkeenApp.personalityType.weaknesses')"
              for="personality-type-weaknesses"
            ></label>
            <textarea
              class="form-control"
              name="weaknesses"
              id="personality-type-weaknesses"
              data-cy="weaknesses"
              :class="{ valid: !v$.weaknesses.$invalid, invalid: v$.weaknesses.$invalid }"
              v-model="v$.weaknesses.$model"
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
<script lang="ts" src="./personality-type-update.component.ts"></script>
