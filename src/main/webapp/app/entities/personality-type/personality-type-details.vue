<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="personalityType">
        <h2 class="jh-entity-heading" data-cy="personalityTypeDetailsHeading">
          <span v-text="t$('tamkeenApp.personalityType.detail.title')"></span> {{ personalityType.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('tamkeenApp.personalityType.typeCode')"></span>
          </dt>
          <dd>
            <span>{{ personalityType.typeCode }}</span>
          </dd>
          <dt>
            <span v-text="t$('tamkeenApp.personalityType.description')"></span>
          </dt>
          <dd>
            <span>{{ personalityType.description }}</span>
          </dd>
          <dt>
            <span v-text="t$('tamkeenApp.personalityType.strengths')"></span>
          </dt>
          <dd>
            <span>{{ personalityType.strengths }}</span>
          </dd>
          <dt>
            <span v-text="t$('tamkeenApp.personalityType.weaknesses')"></span>
          </dt>
          <dd>
            <span>{{ personalityType.weaknesses }}</span>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="personalityType.id"
          :to="{ name: 'PersonalityTypeEdit', params: { personalityTypeId: personalityType.id } }"
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

<script lang="ts" src="./personality-type-details.component.ts"></script>
