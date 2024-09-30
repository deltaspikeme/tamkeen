<template>
  <div>
    <h2 id="page-heading" data-cy="UserSubscriptionHeading">
      <span v-text="t$('tamkeenApp.userSubscription.home.title')" id="user-subscription-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('tamkeenApp.userSubscription.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'UserSubscriptionCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-user-subscription"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('tamkeenApp.userSubscription.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <div class="row">
      <div class="col-sm-12">
        <form name="searchForm" class="form-inline" @submit.prevent="search(currentSearch)">
          <div class="input-group w-100 mt-3">
            <input
              type="text"
              class="form-control"
              name="currentSearch"
              id="currentSearch"
              :placeholder="t$('tamkeenApp.userSubscription.home.search')"
              v-model="currentSearch"
            />
            <button type="button" id="launch-search" class="btn btn-primary" @click="search(currentSearch)">
              <font-awesome-icon icon="search"></font-awesome-icon>
            </button>
            <button type="button" id="clear-search" class="btn btn-secondary" @click="clear()" v-if="currentSearch">
              <font-awesome-icon icon="trash"></font-awesome-icon>
            </button>
          </div>
        </form>
      </div>
    </div>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && userSubscriptions && userSubscriptions.length === 0">
      <span v-text="t$('tamkeenApp.userSubscription.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="userSubscriptions && userSubscriptions.length > 0">
      <table class="table table-striped" aria-describedby="userSubscriptions">
        <thead>
          <tr>
            <th scope="row" @click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('subscriptionType')">
              <span v-text="t$('tamkeenApp.userSubscription.subscriptionType')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'subscriptionType'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('startDate')">
              <span v-text="t$('tamkeenApp.userSubscription.startDate')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'startDate'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('endDate')">
              <span v-text="t$('tamkeenApp.userSubscription.endDate')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'endDate'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('user.id')">
              <span v-text="t$('tamkeenApp.userSubscription.user')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'user.id'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="userSubscription in userSubscriptions" :key="userSubscription.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'UserSubscriptionView', params: { userSubscriptionId: userSubscription.id } }">{{
                userSubscription.id
              }}</router-link>
            </td>
            <td v-text="t$('tamkeenApp.SubscriptionType.' + userSubscription.subscriptionType)"></td>
            <td>{{ userSubscription.startDate }}</td>
            <td>{{ userSubscription.endDate }}</td>
            <td>
              {{ userSubscription.user ? userSubscription.user.id : '' }}
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link
                  :to="{ name: 'UserSubscriptionView', params: { userSubscriptionId: userSubscription.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link
                  :to="{ name: 'UserSubscriptionEdit', params: { userSubscriptionId: userSubscription.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(userSubscription)"
                  variant="danger"
                  class="btn btn-sm"
                  data-cy="entityDeleteButton"
                  v-b-modal.removeEntity
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                  <span class="d-none d-md-inline" v-text="t$('entity.action.delete')"></span>
                </b-button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <template #modal-title>
        <span
          id="tamkeenApp.userSubscription.delete.question"
          data-cy="userSubscriptionDeleteDialogHeading"
          v-text="t$('entity.delete.title')"
        ></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-userSubscription-heading" v-text="t$('tamkeenApp.userSubscription.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-userSubscription"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removeUserSubscription()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="userSubscriptions && userSubscriptions.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./user-subscription.component.ts"></script>
