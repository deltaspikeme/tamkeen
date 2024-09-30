<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="tamkeenApp.userResponse.home.createOrEditLabel"
          data-cy="UserResponseCreateUpdateHeading"
          v-text="t$('tamkeenApp.userResponse.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="userResponse.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="userResponse.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('tamkeenApp.userResponse.responseDate')" for="user-response-responseDate"></label>
            <div class="d-flex">
              <input
                id="user-response-responseDate"
                data-cy="responseDate"
                type="datetime-local"
                class="form-control"
                name="responseDate"
                :class="{ valid: !v$.responseDate.$invalid, invalid: v$.responseDate.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.responseDate.$model)"
                @change="updateInstantField('responseDate', $event)"
              />
            </div>
            <div v-if="v$.responseDate.$anyDirty && v$.responseDate.$invalid">
              <small class="form-text text-danger" v-for="error of v$.responseDate.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('tamkeenApp.userResponse.answer')" for="user-response-answer"></label>
            <select class="form-control" id="user-response-answer" data-cy="answer" name="answer" v-model="userResponse.answer">
              <option :value="null"></option>
              <option
                :value="userResponse.answer && answerOption.id === userResponse.answer.id ? userResponse.answer : answerOption"
                v-for="answerOption in answers"
                :key="answerOption.id"
              >
                {{ answerOption.id }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('tamkeenApp.userResponse.question')" for="user-response-question"></label>
            <select class="form-control" id="user-response-question" data-cy="question" name="question" v-model="userResponse.question">
              <option :value="null"></option>
              <option
                :value="userResponse.question && questionOption.id === userResponse.question.id ? userResponse.question : questionOption"
                v-for="questionOption in questions"
                :key="questionOption.id"
              >
                {{ questionOption.id }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('tamkeenApp.userResponse.user')" for="user-response-user"></label>
            <select class="form-control" id="user-response-user" data-cy="user" name="user" v-model="userResponse.user">
              <option :value="null"></option>
              <option
                :value="userResponse.user && userOption.id === userResponse.user.id ? userResponse.user : userOption"
                v-for="userOption in users"
                :key="userOption.id"
              >
                {{ userOption.id }}
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
<script lang="ts" src="./user-response-update.component.ts"></script>
