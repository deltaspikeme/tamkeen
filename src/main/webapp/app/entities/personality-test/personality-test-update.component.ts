import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import PersonalityTestService from './personality-test.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import { type IPersonalityTest, PersonalityTest } from '@/shared/model/personality-test.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'PersonalityTestUpdate',
  setup() {
    const personalityTestService = inject('personalityTestService', () => new PersonalityTestService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const personalityTest: Ref<IPersonalityTest> = ref(new PersonalityTest());
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'ar-ly'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrievePersonalityTest = async personalityTestId => {
      try {
        const res = await personalityTestService().find(personalityTestId);
        personalityTest.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.personalityTestId) {
      retrievePersonalityTest(route.params.personalityTestId);
    }

    const dataUtils = useDataUtils();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      testName: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      description: {},
    };
    const v$ = useVuelidate(validationRules, personalityTest as any);
    v$.value.$validate();

    return {
      personalityTestService,
      alertService,
      personalityTest,
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
      if (this.personalityTest.id) {
        this.personalityTestService()
          .update(this.personalityTest)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('tamkeenApp.personalityTest.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.personalityTestService()
          .create(this.personalityTest)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('tamkeenApp.personalityTest.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
