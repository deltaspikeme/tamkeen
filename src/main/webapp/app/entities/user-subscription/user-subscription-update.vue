<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="tamkeenApp.userSubscription.home.createOrEditLabel"
          data-cy="UserSubscriptionCreateUpdateHeading"
          v-text="t$('tamkeenApp.userSubscription.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="userSubscription.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="userSubscription.id" readonly />
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('tamkeenApp.userSubscription.subscriptionType')"
              for="user-subscription-subscriptionType"
            ></label>
            <select
              class="form-control"
              name="subscriptionType"
              :class="{ valid: !v$.subscriptionType.$invalid, invalid: v$.subscriptionType.$invalid }"
              v-model="v$.subscriptionType.$model"
              id="user-subscription-subscriptionType"
              data-cy="subscriptionType"
              required
            >
              <option
                v-for="subscriptionType in subscriptionTypeValues"
                :key="subscriptionType"
                :value="subscriptionType"
                :label="t$('tamkeenApp.SubscriptionType.' + subscriptionType)"
              >
                {{ subscriptionType }}
              </option>
            </select>
            <div v-if="v$.subscriptionType.$anyDirty && v$.subscriptionType.$invalid">
              <small class="form-text text-danger" v-for="error of v$.subscriptionType.$errors" :key="error.$uid">{{
                error.$message
              }}</small>
            </div>
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('tamkeenApp.userSubscription.startDate')"
              for="user-subscription-startDate"
            ></label>
            <b-input-group class="mb-3">
              <b-input-group-prepend>
                <b-form-datepicker
                  aria-controls="user-subscription-startDate"
                  v-model="v$.startDate.$model"
                  name="startDate"
                  class="form-control"
                  :locale="currentLanguage"
                  button-only
                  today-button
                  reset-button
                  close-button
                >
                </b-form-datepicker>
              </b-input-group-prepend>
              <b-form-input
                id="user-subscription-startDate"
                data-cy="startDate"
                type="text"
                class="form-control"
                name="startDate"
                :class="{ valid: !v$.startDate.$invalid, invalid: v$.startDate.$invalid }"
                v-model="v$.startDate.$model"
                required
              />
            </b-input-group>
            <div v-if="v$.startDate.$anyDirty && v$.startDate.$invalid">
              <small class="form-text text-danger" v-for="error of v$.startDate.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('tamkeenApp.userSubscription.endDate')" for="user-subscription-endDate"></label>
            <b-input-group class="mb-3">
              <b-input-group-prepend>
                <b-form-datepicker
                  aria-controls="user-subscription-endDate"
                  v-model="v$.endDate.$model"
                  name="endDate"
                  class="form-control"
                  :locale="currentLanguage"
                  button-only
                  today-button
                  reset-button
                  close-button
                >
                </b-form-datepicker>
              </b-input-group-prepend>
              <b-form-input
                id="user-subscription-endDate"
                data-cy="endDate"
                type="text"
                class="form-control"
                name="endDate"
                :class="{ valid: !v$.endDate.$invalid, invalid: v$.endDate.$invalid }"
                v-model="v$.endDate.$model"
                required
              />
            </b-input-group>
            <div v-if="v$.endDate.$anyDirty && v$.endDate.$invalid">
              <small class="form-text text-danger" v-for="error of v$.endDate.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('tamkeenApp.userSubscription.user')" for="user-subscription-user"></label>
            <select class="form-control" id="user-subscription-user" data-cy="user" name="user" v-model="userSubscription.user">
              <option :value="null"></option>
              <option
                :value="userSubscription.user && userOption.id === userSubscription.user.id ? userSubscription.user : userOption"
                v-for="userOption in users"
                :key="userOption.id"
              >
                {{ userOption.id }}
              </option>
            </select>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" @click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.cancel')"></span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="v$.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.save')"></span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./user-subscription-update.component.ts"></script>
