<template>
  <div>
    <h2 id="page-heading" data-cy="UserResponseHeading">
      <span v-text="t$('tamkeenApp.userResponse.home.title')" id="user-response-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('tamkeenApp.userResponse.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'UserResponseCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-user-response"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('tamkeenApp.userResponse.home.createLabel')"></span>
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
              :placeholder="t$('tamkeenApp.userResponse.home.search')"
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
    <div class="alert alert-warning" v-if="!isFetching && userResponses && userResponses.length === 0">
      <span v-text="t$('tamkeenApp.userResponse.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="userResponses && userResponses.length > 0">
      <table class="table table-striped" aria-describedby="userResponses">
        <thead>
          <tr>
            <th scope="row" @click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('responseDate')">
              <span v-text="t$('tamkeenApp.userResponse.responseDate')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'responseDate'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('answer.id')">
              <span v-text="t$('tamkeenApp.userResponse.answer')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'answer.id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('question.id')">
              <span v-text="t$('tamkeenApp.userResponse.question')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'question.id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('user.id')">
              <span v-text="t$('tamkeenApp.userResponse.user')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'user.id'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="userResponse in userResponses" :key="userResponse.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'UserResponseView', params: { userResponseId: userResponse.id } }">{{
                userResponse.id
              }}</router-link>
            </td>
            <td>{{ formatDateShort(userResponse.responseDate) || '' }}</td>
            <td>
              <div v-if="userResponse.answer">
                <router-link :to="{ name: 'AnswerView', params: { answerId: userResponse.answer.id } }">{{
                  userResponse.answer.id
                }}</router-link>
              </div>
            </td>
            <td>
              <div v-if="userResponse.question">
                <router-link :to="{ name: 'QuestionView', params: { questionId: userResponse.question.id } }">{{
                  userResponse.question.id
                }}</router-link>
              </div>
            </td>
            <td>
              {{ userResponse.user ? userResponse.user.id : '' }}
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'UserResponseView', params: { userResponseId: userResponse.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'UserResponseEdit', params: { userResponseId: userResponse.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(userResponse)"
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
          id="tamkeenApp.userResponse.delete.question"
          data-cy="userResponseDeleteDialogHeading"
          v-text="t$('entity.delete.title')"
        ></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-userResponse-heading" v-text="t$('tamkeenApp.userResponse.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-userResponse"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removeUserResponse()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="userResponses && userResponses.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./user-response.component.ts"></script>
