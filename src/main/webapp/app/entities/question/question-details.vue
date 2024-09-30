<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="question">
        <h2 class="jh-entity-heading" data-cy="questionDetailsHeading">
          <span v-text="t$('tamkeenApp.question.detail.title')"></span> {{ question.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('tamkeenApp.question.questionText')"></span>
          </dt>
          <dd>
            <span>{{ question.questionText }}</span>
          </dd>
          <dt>
            <span v-text="t$('tamkeenApp.question.questionType')"></span>
          </dt>
          <dd>
            <span v-text="t$('tamkeenApp.QuestionType.' + question.questionType)"></span>
          </dd>
          <dt>
            <span v-text="t$('tamkeenApp.question.personalityTest')"></span>
          </dt>
          <dd>
            <div v-if="question.personalityTest">
              <router-link :to="{ name: 'PersonalityTestView', params: { personalityTestId: question.personalityTest.id } }">{{
                question.personalityTest.id
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link v-if="question.id" :to="{ name: 'QuestionEdit', params: { questionId: question.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./question-details.component.ts"></script>
