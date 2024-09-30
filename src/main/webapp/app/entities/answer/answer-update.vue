<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="tamkeenApp.answer.home.createOrEditLabel"
          data-cy="AnswerCreateUpdateHeading"
          v-text="t$('tamkeenApp.answer.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="answer.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="answer.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('tamkeenApp.answer.answerText')" for="answer-answerText"></label>
            <input
              type="text"
              class="form-control"
              name="answerText"
              id="answer-answerText"
              data-cy="answerText"
              :class="{ valid: !v$.answerText.$invalid, invalid: v$.answerText.$invalid }"
              v-model="v$.answerText.$model"
              required
            />
            <div v-if="v$.answerText.$anyDirty && v$.answerText.$invalid">
              <small class="form-text text-danger" v-for="error of v$.answerText.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('tamkeenApp.answer.score')" for="answer-score"></label>
            <input
              type="number"
              class="form-control"
              name="score"
              id="answer-score"
              data-cy="score"
              :class="{ valid: !v$.score.$invalid, invalid: v$.score.$invalid }"
              v-model.number="v$.score.$model"
              required
            />
            <div v-if="v$.score.$anyDirty && v$.score.$invalid">
              <small class="form-text text-danger" v-for="error of v$.score.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('tamkeenApp.answer.question')" for="answer-question"></label>
            <select class="form-control" id="answer-question" data-cy="question" name="question" v-model="answer.question">
              <option :value="null"></option>
              <option
                :value="answer.question && questionOption.id === answer.question.id ? answer.question : questionOption"
                v-for="questionOption in questions"
                :key="questionOption.id"
              >
                {{ questionOption.id }}
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
<script lang="ts" src="./answer-update.component.ts"></script>
