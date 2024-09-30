<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="answer">
        <h2 class="jh-entity-heading" data-cy="answerDetailsHeading">
          <span v-text="t$('tamkeenApp.answer.detail.title')"></span> {{ answer.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('tamkeenApp.answer.answerText')"></span>
          </dt>
          <dd>
            <span>{{ answer.answerText }}</span>
          </dd>
          <dt>
            <span v-text="t$('tamkeenApp.answer.score')"></span>
          </dt>
          <dd>
            <span>{{ answer.score }}</span>
          </dd>
          <dt>
            <span v-text="t$('tamkeenApp.answer.question')"></span>
          </dt>
          <dd>
            <div v-if="answer.question">
              <router-link :to="{ name: 'QuestionView', params: { questionId: answer.question.id } }">{{ answer.question.id }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link v-if="answer.id" :to="{ name: 'AnswerEdit', params: { answerId: answer.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./answer-details.component.ts"></script>
