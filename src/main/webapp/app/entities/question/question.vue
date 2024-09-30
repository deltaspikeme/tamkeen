<template>
  <div>
    <h2 id="page-heading" data-cy="QuestionHeading">
      <span v-text="t$('tamkeenApp.question.home.title')" id="question-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('tamkeenApp.question.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'QuestionCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-question"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('tamkeenApp.question.home.createLabel')"></span>
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
              :placeholder="t$('tamkeenApp.question.home.search')"
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
    <div class="alert alert-warning" v-if="!isFetching && questions && questions.length === 0">
      <span v-text="t$('tamkeenApp.question.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="questions && questions.length > 0">
      <table class="table table-striped" aria-describedby="questions">
        <thead>
          <tr>
            <th scope="row" @click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('questionText')">
              <span v-text="t$('tamkeenApp.question.questionText')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'questionText'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('questionType')">
              <span v-text="t$('tamkeenApp.question.questionType')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'questionType'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('personalityTest.id')">
              <span v-text="t$('tamkeenApp.question.personalityTest')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'personalityTest.id'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="question in questions" :key="question.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'QuestionView', params: { questionId: question.id } }">{{ question.id }}</router-link>
            </td>
            <td>{{ question.questionText }}</td>
            <td v-text="t$('tamkeenApp.QuestionType.' + question.questionType)"></td>
            <td>
              <div v-if="question.personalityTest">
                <router-link :to="{ name: 'PersonalityTestView', params: { personalityTestId: question.personalityTest.id } }">{{
                  question.personalityTest.id
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'QuestionView', params: { questionId: question.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'QuestionEdit', params: { questionId: question.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(question)"
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
        <span id="tamkeenApp.question.delete.question" data-cy="questionDeleteDialogHeading" v-text="t$('entity.delete.title')"></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-question-heading" v-text="t$('tamkeenApp.question.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-question"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removeQuestion()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="questions && questions.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./question.component.ts"></script>
