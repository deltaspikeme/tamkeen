<template>
  <div>
    <h2 id="page-heading" data-cy="AnswerHeading">
      <span v-text="t$('tamkeenApp.answer.home.title')" id="answer-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('tamkeenApp.answer.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'AnswerCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-answer"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('tamkeenApp.answer.home.createLabel')"></span>
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
              :placeholder="t$('tamkeenApp.answer.home.search')"
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
    <div class="alert alert-warning" v-if="!isFetching && answers && answers.length === 0">
      <span v-text="t$('tamkeenApp.answer.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="answers && answers.length > 0">
      <table class="table table-striped" aria-describedby="answers">
        <thead>
          <tr>
            <th scope="row" @click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('answerText')">
              <span v-text="t$('tamkeenApp.answer.answerText')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'answerText'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('score')">
              <span v-text="t$('tamkeenApp.answer.score')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'score'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('question.id')">
              <span v-text="t$('tamkeenApp.answer.question')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'question.id'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="answer in answers" :key="answer.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'AnswerView', params: { answerId: answer.id } }">{{ answer.id }}</router-link>
            </td>
            <td>{{ answer.answerText }}</td>
            <td>{{ answer.score }}</td>
            <td>
              <div v-if="answer.question">
                <router-link :to="{ name: 'QuestionView', params: { questionId: answer.question.id } }">{{
                  answer.question.id
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'AnswerView', params: { answerId: answer.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'AnswerEdit', params: { answerId: answer.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(answer)"
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
        <span id="tamkeenApp.answer.delete.question" data-cy="answerDeleteDialogHeading" v-text="t$('entity.delete.title')"></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-answer-heading" v-text="t$('tamkeenApp.answer.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-answer"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removeAnswer()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="answers && answers.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./answer.component.ts"></script>
