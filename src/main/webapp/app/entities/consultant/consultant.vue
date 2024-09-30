<template>
  <div>
    <h2 id="page-heading" data-cy="ConsultantHeading">
      <span v-text="t$('tamkeenApp.consultant.home.title')" id="consultant-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('tamkeenApp.consultant.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'ConsultantCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-consultant"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('tamkeenApp.consultant.home.createLabel')"></span>
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
              :placeholder="t$('tamkeenApp.consultant.home.search')"
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
    <div class="alert alert-warning" v-if="!isFetching && consultants && consultants.length === 0">
      <span v-text="t$('tamkeenApp.consultant.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="consultants && consultants.length > 0">
      <table class="table table-striped" aria-describedby="consultants">
        <thead>
          <tr>
            <th scope="row" @click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('name')">
              <span v-text="t$('tamkeenApp.consultant.name')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'name'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('expertise')">
              <span v-text="t$('tamkeenApp.consultant.expertise')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'expertise'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('bio')">
              <span v-text="t$('tamkeenApp.consultant.bio')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'bio'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('email')">
              <span v-text="t$('tamkeenApp.consultant.email')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'email'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('phone')">
              <span v-text="t$('tamkeenApp.consultant.phone')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'phone'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('servicesOffered')">
              <span v-text="t$('tamkeenApp.consultant.servicesOffered')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'servicesOffered'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('user.login')">
              <span v-text="t$('tamkeenApp.consultant.user')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'user.login'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="consultant in consultants" :key="consultant.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'ConsultantView', params: { consultantId: consultant.id } }">{{ consultant.id }}</router-link>
            </td>
            <td>{{ consultant.name }}</td>
            <td>{{ consultant.expertise }}</td>
            <td>{{ consultant.bio }}</td>
            <td>{{ consultant.email }}</td>
            <td>{{ consultant.phone }}</td>
            <td>{{ consultant.servicesOffered }}</td>
            <td>
              {{ consultant.user ? consultant.user.id : '' }}
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'ConsultantView', params: { consultantId: consultant.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'ConsultantEdit', params: { consultantId: consultant.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(consultant)"
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
        <span id="tamkeenApp.consultant.delete.question" data-cy="consultantDeleteDialogHeading" v-text="t$('entity.delete.title')"></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-consultant-heading" v-text="t$('tamkeenApp.consultant.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-consultant"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removeConsultant()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="consultants && consultants.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./consultant.component.ts"></script>
