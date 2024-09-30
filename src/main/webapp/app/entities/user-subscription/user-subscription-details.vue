<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="userSubscription">
        <h2 class="jh-entity-heading" data-cy="userSubscriptionDetailsHeading">
          <span v-text="t$('tamkeenApp.userSubscription.detail.title')"></span> {{ userSubscription.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('tamkeenApp.userSubscription.subscriptionType')"></span>
          </dt>
          <dd>
            <span v-text="t$('tamkeenApp.SubscriptionType.' + userSubscription.subscriptionType)"></span>
          </dd>
          <dt>
            <span v-text="t$('tamkeenApp.userSubscription.startDate')"></span>
          </dt>
          <dd>
            <span>{{ userSubscription.startDate }}</span>
          </dd>
          <dt>
            <span v-text="t$('tamkeenApp.userSubscription.endDate')"></span>
          </dt>
          <dd>
            <span>{{ userSubscription.endDate }}</span>
          </dd>
          <dt>
            <span v-text="t$('tamkeenApp.userSubscription.user')"></span>
          </dt>
          <dd>
            {{ userSubscription.user ? userSubscription.user.id : '' }}
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="userSubscription.id"
          :to="{ name: 'UserSubscriptionEdit', params: { userSubscriptionId: userSubscription.id } }"
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

<script lang="ts" src="./user-subscription-details.component.ts"></script>
