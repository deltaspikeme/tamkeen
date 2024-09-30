<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="testResult">
        <h2 class="jh-entity-heading" data-cy="testResultDetailsHeading">
          <span v-text="t$('tamkeenApp.testResult.detail.title')"></span> {{ testResult.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('tamkeenApp.testResult.analysis')"></span>
          </dt>
          <dd>
            <span>{{ testResult.analysis }}</span>
          </dd>
          <dt>
            <span v-text="t$('tamkeenApp.testResult.resultDate')"></span>
          </dt>
          <dd>
            <span v-if="testResult.resultDate">{{ formatDateLong(testResult.resultDate) }}</span>
          </dd>
          <dt>
            <span v-text="t$('tamkeenApp.testResult.user')"></span>
          </dt>
          <dd>
            {{ testResult.user ? testResult.user.id : '' }}
          </dd>
          <dt>
            <span v-text="t$('tamkeenApp.testResult.personalityTest')"></span>
          </dt>
          <dd>
            <div v-if="testResult.personalityTest">
              <router-link :to="{ name: 'PersonalityTestView', params: { personalityTestId: testResult.personalityTest.id } }">{{
                testResult.personalityTest.id
              }}</router-link>
            </div>
          </dd>
          <dt>
            <span v-text="t$('tamkeenApp.testResult.personalityType')"></span>
          </dt>
          <dd>
            <div v-if="testResult.personalityType">
              <router-link :to="{ name: 'PersonalityTypeView', params: { personalityTypeId: testResult.personalityType.id } }">{{
                testResult.personalityType.id
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="testResult.id"
          :to="{ name: 'TestResultEdit', params: { testResultId: testResult.id } }"
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

<script lang="ts" src="./test-result-details.component.ts"></script>
