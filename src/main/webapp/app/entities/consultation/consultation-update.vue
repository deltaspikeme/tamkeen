<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="tamkeenApp.consultation.home.createOrEditLabel"
          data-cy="ConsultationCreateUpdateHeading"
          v-text="t$('tamkeenApp.consultation.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="consultation.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="consultation.id" readonly />
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('tamkeenApp.consultation.consultantName')"
              for="consultation-consultantName"
            ></label>
            <input
              type="text"
              class="form-control"
              name="consultantName"
              id="consultation-consultantName"
              data-cy="consultantName"
              :class="{ valid: !v$.consultantName.$invalid, invalid: v$.consultantName.$invalid }"
              v-model="v$.consultantName.$model"
              required
            />
            <div v-if="v$.consultantName.$anyDirty && v$.consultantName.$invalid">
              <small class="form-text text-danger" v-for="error of v$.consultantName.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('tamkeenApp.consultation.consultationDate')"
              for="consultation-consultationDate"
            ></label>
            <div class="d-flex">
              <input
                id="consultation-consultationDate"
                data-cy="consultationDate"
                type="datetime-local"
                class="form-control"
                name="consultationDate"
                :class="{ valid: !v$.consultationDate.$invalid, invalid: v$.consultationDate.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.consultationDate.$model)"
                @change="updateInstantField('consultationDate', $event)"
              />
            </div>
            <div v-if="v$.consultationDate.$anyDirty && v$.consultationDate.$invalid">
              <small class="form-text text-danger" v-for="error of v$.consultationDate.$errors" :key="error.$uid">{{
                error.$message
              }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('tamkeenApp.consultation.notes')" for="consultation-notes"></label>
            <textarea
              class="form-control"
              name="notes"
              id="consultation-notes"
              data-cy="notes"
              :class="{ valid: !v$.notes.$invalid, invalid: v$.notes.$invalid }"
              v-model="v$.notes.$model"
            ></textarea>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('tamkeenApp.consultation.consultant')" for="consultation-consultant"></label>
            <select
              class="form-control"
              id="consultation-consultant"
              data-cy="consultant"
              name="consultant"
              v-model="consultation.consultant"
            >
              <option :value="null"></option>
              <option
                :value="
                  consultation.consultant && consultantOption.id === consultation.consultant.id ? consultation.consultant : consultantOption
                "
                v-for="consultantOption in consultants"
                :key="consultantOption.id"
              >
                {{ consultantOption.id }}
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
<script lang="ts" src="./consultation-update.component.ts"></script>
