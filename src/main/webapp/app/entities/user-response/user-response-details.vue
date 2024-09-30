<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="userResponse">
        <h2 class="jh-entity-heading" data-cy="userResponseDetailsHeading">
          <span v-text="t$('tamkeenApp.userResponse.detail.title')"></span> {{ userResponse.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('tamkeenApp.userResponse.responseDate')"></span>
          </dt>
          <dd>
            <span v-if="userResponse.responseDate">{{ formatDateLong(userResponse.responseDate) }}</span>
          </dd>
          <dt>
            <span v-text="t$('tamkeenApp.userResponse.answer')"></span>
          </dt>
          <dd>
            <div v-if="userResponse.answer">
              <router-link :to="{ name: 'AnswerView', params: { answerId: userResponse.answer.id } }">{{
                userResponse.answer.id
              }}</router-link>
            </div>
          </dd>
          <dt>
            <span v-text="t$('tamkeenApp.userResponse.question')"></span>
          </dt>
          <dd>
            <div v-if="userResponse.question">
              <router-link :to="{ name: 'QuestionView', params: { questionId: userResponse.question.id } }">{{
                userResponse.question.id
              }}</router-link>
            </div>
          </dd>
          <dt>
            <span v-text="t$('tamkeenApp.userResponse.user')"></span>
          </dt>
          <dd>
            {{ userResponse.user ? userResponse.user.id : '' }}
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="userResponse.id"
          :to="{ name: 'UserResponseEdit', params: { userResponseId: userResponse.id } }"
          custom
          v-slot="{ navigate }"
        >
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./user-response-details.component.ts"></script>
