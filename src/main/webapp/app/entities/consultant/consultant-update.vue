<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="tamkeenApp.consultant.home.createOrEditLabel"
          data-cy="ConsultantCreateUpdateHeading"
          v-text="t$('tamkeenApp.consultant.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="consultant.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="consultant.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('tamkeenApp.consultant.name')" for="consultant-name"></label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="consultant-name"
              data-cy="name"
              :class="{ valid: !v$.name.$invalid, invalid: v$.name.$invalid }"
              v-model="v$.name.$model"
              required
            />
            <div v-if="v$.name.$anyDirty && v$.name.$invalid">
              <small class="form-text text-danger" v-for="error of v$.name.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('tamkeenApp.consultant.expertise')" for="consultant-expertise"></label>
            <input
              type="text"
              class="form-control"
              name="expertise"
              id="consultant-expertise"
              data-cy="expertise"
              :class="{ valid: !v$.expertise.$invalid, invalid: v$.expertise.$invalid }"
              v-model="v$.expertise.$model"
              required
            />
            <div v-if="v$.expertise.$anyDirty && v$.expertise.$invalid">
              <small class="form-text text-danger" v-for="error of v$.expertise.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('tamkeenApp.consultant.bio')" for="consultant-bio"></label>
            <textarea
              class="form-control"
              name="bio"
              id="consultant-bio"
              data-cy="bio"
              :class="{ valid: !v$.bio.$invalid, invalid: v$.bio.$invalid }"
              v-model="v$.bio.$model"
            ></textarea>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('tamkeenApp.consultant.email')" for="consultant-email"></label>
            <input
              type="text"
              class="form-control"
              name="email"
              id="consultant-email"
              data-cy="email"
              :class="{ valid: !v$.email.$invalid, invalid: v$.email.$invalid }"
              v-model="v$.email.$model"
              required
            />
            <div v-if="v$.email.$anyDirty && v$.email.$invalid">
              <small class="form-text text-danger" v-for="error of v$.email.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('tamkeenApp.consultant.phone')" for="consultant-phone"></label>
            <input
              type="text"
              class="form-control"
              name="phone"
              id="consultant-phone"
              data-cy="phone"
              :class="{ valid: !v$.phone.$invalid, invalid: v$.phone.$invalid }"
              v-model="v$.phone.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('tamkeenApp.consultant.servicesOffered')" for="consultant-servicesOffered"></label>
            <textarea
              class="form-control"
              name="servicesOffered"
              id="consultant-servicesOffered"
              data-cy="servicesOffered"
              :class="{ valid: !v$.servicesOffered.$invalid, invalid: v$.servicesOffered.$invalid }"
              v-model="v$.servicesOffered.$model"
            ></textarea>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('tamkeenApp.consultant.user')" for="consultant-user"></label>
            <select class="form-control" id="consultant-user" data-cy="user" name="user" v-model="consultant.user">
              <option :value="null"></option>
              <option
                :value="consultant.user && userOption.id === consultant.user.id ? consultant.user : userOption"
                v-for="userOption in users"
                :key="userOption.id"
              >
                {{ userOption.login }}
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
<script lang="ts" src="./consultant-update.component.ts"></script>
