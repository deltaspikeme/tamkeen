<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="consultant">
        <h2 class="jh-entity-heading" data-cy="consultantDetailsHeading">
          <span v-text="t$('tamkeenApp.consultant.detail.title')"></span> {{ consultant.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('tamkeenApp.consultant.name')"></span>
          </dt>
          <dd>
            <span>{{ consultant.name }}</span>
          </dd>
          <dt>
            <span v-text="t$('tamkeenApp.consultant.expertise')"></span>
          </dt>
          <dd>
            <span>{{ consultant.expertise }}</span>
          </dd>
          <dt>
            <span v-text="t$('tamkeenApp.consultant.bio')"></span>
          </dt>
          <dd>
            <span>{{ consultant.bio }}</span>
          </dd>
          <dt>
            <span v-text="t$('tamkeenApp.consultant.email')"></span>
          </dt>
          <dd>
            <span>{{ consultant.email }}</span>
          </dd>
          <dt>
            <span v-text="t$('tamkeenApp.consultant.phone')"></span>
          </dt>
          <dd>
            <span>{{ consultant.phone }}</span>
          </dd>
          <dt>
            <span v-text="t$('tamkeenApp.consultant.servicesOffered')"></span>
          </dt>
          <dd>
            <span>{{ consultant.servicesOffered }}</span>
          </dd>
          <dt>
            <span v-text="t$('tamkeenApp.consultant.user')"></span>
          </dt>
          <dd>
            {{ consultant.user ? consultant.user.id : '' }}
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="consultant.id"
          :to="{ name: 'ConsultantEdit', params: { consultantId: consultant.id } }"
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

<script lang="ts" src="./consultant-details.component.ts"></script>
