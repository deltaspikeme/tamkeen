<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="personalityTest">
        <h2 class="jh-entity-heading" data-cy="personalityTestDetailsHeading">
          <span v-text="t$('tamkeenApp.personalityTest.detail.title')"></span> {{ personalityTest.testName }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('tamkeenApp.personalityTest.testName')"></span>
          </dt>
          <dd>
            <span>{{ personalityTest.testName }}</span>
          </dd>
          <dt>
            <span v-text="t$('tamkeenApp.personalityTest.description')"></span>
          </dt>
          <dd>
            <span>{{ personalityTest.description }}</span>
          </dd>
        </dl>

        <!-- Questions Section -->
        <div v-if="personalityTest.questions && personalityTest.questions.length">
          <h3>{{ t$('tamkeenApp.personalityTest.questions.title', 'الأسئلة') }}</h3>
          <ul class="list-group mb-3">
            <li v-for="question in personalityTest.questions" :key="question.id" class="list-group-item">
              <strong>{{ question.questionText }}</strong>
              <ul class="mt-2">
                <li v-if="question.answers.length" v-for="answer in question.answers" :key="answer.id">- {{ answer.answerText }}</li>
                <li v-else>
                  {{ t$('tamkeenApp.personalityTest.noAnswers', 'لا توجد إجابات متاحة لهذا السؤال.') }}
                </li>
              </ul>
            </li>
          </ul>
        </div>

        <!-- Back and Edit Buttons -->
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="personalityTest.id"
          :to="{ name: 'PersonalityTestEdit', params: { personalityTestId: personalityTest.id } }"
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

<script lang="ts" src="./personality-test-details.component.ts"></script>
