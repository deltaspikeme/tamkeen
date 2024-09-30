import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import PersonalityTypeService from './personality-type.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import { type IPersonalityType, PersonalityType } from '@/shared/model/personality-type.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'PersonalityTypeUpdate',
  setup() {
    const personalityTypeService = inject('personalityTypeService', () => new PersonalityTypeService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const personalityType: Ref<IPersonalityType> = ref(new PersonalityType());
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'ar-ly'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrievePersonalityType = async personalityTypeId => {
      try {
        const res = await personalityTypeService().find(personalityTypeId);
        personalityType.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.personalityTypeId) {
      retrievePersonalityType(route.params.personalityTypeId);
    }

    const dataUtils = useDataUtils();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      typeCode: {
        required: validations.required(t$('entity.validation.required').toString()),
        minLength: validations.minLength(t$('entity.validation.minlength', { min: 4 }).toString(), 4),
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 4 }).toString(), 4),
      },
      description: {},
      strengths: {},
      weaknesses: {},
    };
    const v$ = useVuelidate(validationRules, personalityType as any);
    v$.value.$validate();

    return {
      personalityTypeService,
      alertService,
      personalityType,
      previousState,
      isSaving,
      currentLanguage,
      ...dataUtils,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.personalityType.id) {
        this.personalityTypeService()
          .update(this.personalityType)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('tamkeenApp.personalityType.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.personalityTypeService()
          .create(this.personalityType)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('tamkeenApp.personalityType.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
