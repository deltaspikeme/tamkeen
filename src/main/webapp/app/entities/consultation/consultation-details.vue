<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="consultation">
        <h2 class="jh-entity-heading" data-cy="consultationDetailsHeading">
          <span v-text="t$('tamkeenApp.consultation.detail.title')"></span> {{ consultation.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('tamkeenApp.consultation.consultantName')"></span>
          </dt>
          <dd>
            <span>{{ consultation.consultantName }}</span>
          </dd>
          <dt>
            <span v-text="t$('tamkeenApp.consultation.consultationDate')"></span>
          </dt>
          <dd>
            <span v-if="consultation.consultationDate">{{ formatDateLong(consultation.consultationDate) }}</span>
          </dd>
          <dt>
            <span v-text="t$('tamkeenApp.consultation.notes')"></span>
          </dt>
          <dd>
            <span>{{ consultation.notes }}</span>
          </dd>
          <dt>
            <span v-text="t$('tamkeenApp.consultation.consultant')"></span>
          </dt>
          <dd>
            <div v-if="consultation.consultant">
              <router-link :to="{ name: 'ConsultantView', params: { consultantId: consultation.consultant.id } }">{{
                consultation.consultant.id
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="consultation.id"
          :to="{ name: 'ConsultationEdit', params: { consultationId: consultation.id } }"
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

<script lang="ts" src="./consultation-details.component.ts"></script>
