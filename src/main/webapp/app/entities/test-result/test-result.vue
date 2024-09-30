<template>
  <div>
    <h2 id="page-heading" data-cy="TestResultHeading">
      <span v-text="t$('tamkeenApp.testResult.home.title')" id="test-result-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('tamkeenApp.testResult.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'TestResultCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-test-result"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('tamkeenApp.testResult.home.createLabel')"></span>
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
              :placeholder="t$('tamkeenApp.testResult.home.search')"
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
    <div class="alert alert-warning" v-if="!isFetching && testResults && testResults.length === 0">
      <span v-text="t$('tamkeenApp.testResult.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="testResults && testResults.length > 0">
      <table class="table table-striped" aria-describedby="testResults">
        <thead>
          <tr>
            <th scope="row" @click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('analysis')">
              <span v-text="t$('tamkeenApp.testResult.analysis')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'analysis'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('resultDate')">
              <span v-text="t$('tamkeenApp.testResult.resultDate')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'resultDate'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('user.id')">
              <span v-text="t$('tamkeenApp.testResult.user')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'user.id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('personalityTest.id')">
              <span v-text="t$('tamkeenApp.testResult.personalityTest')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'personalityTest.id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('personalityType.id')">
              <span v-text="t$('tamkeenApp.testResult.personalityType')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'personalityType.id'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="testResult in testResults" :key="testResult.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'TestResultView', params: { testResultId: testResult.id } }">{{ testResult.id }}</router-link>
            </td>
            <td>{{ testResult.analysis }}</td>
            <td>{{ formatDateShort(testResult.resultDate) || '' }}</td>
            <td>
              {{ testResult.user ? testResult.user.id : '' }}
            </td>
            <td>
              <div v-if="testResult.personalityTest">
                <router-link :to="{ name: 'PersonalityTestView', params: { personalityTestId: testResult.personalityTest.id } }">{{
                  testResult.personalityTest.id
                }}</router-link>
              </div>
            </td>
            <td>
              <div v-if="testResult.personalityType">
                <router-link :to="{ name: 'PersonalityTypeView', params: { personalityTypeId: testResult.personalityType.id } }">{{
                  testResult.personalityType.id
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'TestResultView', params: { testResultId: testResult.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'TestResultEdit', params: { testResultId: testResult.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(testResult)"
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
        <span id="tamkeenApp.testResult.delete.question" data-cy="testResultDeleteDialogHeading" v-text="t$('entity.delete.title')"></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-testResult-heading" v-text="t$('tamkeenApp.testResult.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-testResult"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removeTestResult()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="testResults && testResults.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./test-result.component.ts"></script>
