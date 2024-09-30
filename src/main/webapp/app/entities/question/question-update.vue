<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="tamkeenApp.question.home.createOrEditLabel"
          data-cy="QuestionCreateUpdateHeading"
          v-text="t$('tamkeenApp.question.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="question.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="question.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('tamkeenApp.question.questionText')" for="question-questionText"></label>
            <textarea
              class="form-control"
              name="questionText"
              id="question-questionText"
              data-cy="questionText"
              :class="{ valid: !v$.questionText.$invalid, invalid: v$.questionText.$invalid }"
              v-model="v$.questionText.$model"
              required
            ></textarea>
            <div v-if="v$.questionText.$anyDirty && v$.questionText.$invalid">
              <small class="form-text text-danger" v-for="error of v$.questionText.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('tamkeenApp.question.questionType')" for="question-questionType"></label>
            <select
              class="form-control"
              name="questionType"
              :class="{ valid: !v$.questionType.$invalid, invalid: v$.questionType.$invalid }"
              v-model="v$.questionType.$model"
              id="question-questionType"
              data-cy="questionType"
              required
            >
              <option
                v-for="questionType in questionTypeValues"
                :key="questionType"
                :value="questionType"
                :label="t$('tamkeenApp.QuestionType.' + questionType)"
              >
                {{ questionType }}
              </option>
            </select>
            <div v-if="v$.questionType.$anyDirty && v$.questionType.$invalid">
              <small class="form-text text-danger" v-for="error of v$.questionType.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('tamkeenApp.question.personalityTest')" for="question-personalityTest"></label>
            <select
              class="form-control"
              id="question-personalityTest"
              data-cy="personalityTest"
              name="personalityTest"
              v-model="question.personalityTest"
            >
              <option :value="null"></option>
              <option
                :value="
                  question.personalityTest && personalityTestOption.id === question.personalityTest.id
                    ? question.personalityTest
                    : personalityTestOption
                "
                v-for="personalityTestOption in personalityTests"
                :key="personalityTestOption.id"
              >
                {{ personalityTestOption.id }}
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
<script lang="ts" src="./question-update.component.ts"></script>
