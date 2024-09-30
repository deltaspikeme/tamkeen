import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import TestResultService from './test-result.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import PersonalityTestService from '@/entities/personality-test/personality-test.service';
import { type IPersonalityTest } from '@/shared/model/personality-test.model';
import PersonalityTypeService from '@/entities/personality-type/personality-type.service';
import { type IPersonalityType } from '@/shared/model/personality-type.model';
import { type ITestResult, TestResult } from '@/shared/model/test-result.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'TestResultUpdate',
  setup() {
    const testResultService = inject('testResultService', () => new TestResultService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const testResult: Ref<ITestResult> = ref(new TestResult());
    const userService = inject('userService', () => new UserService());
    const users: Ref<Array<any>> = ref([]);

    const personalityTestService = inject('personalityTestService', () => new PersonalityTestService());

    const personalityTests: Ref<IPersonalityTest[]> = ref([]);

    const personalityTypeService = inject('personalityTypeService', () => new PersonalityTypeService());

    const personalityTypes: Ref<IPersonalityType[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'ar-ly'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveTestResult = async testResultId => {
      try {
        const res = await testResultService().find(testResultId);
        res.resultDate = new Date(res.resultDate);
        testResult.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.testResultId) {
      retrieveTestResult(route.params.testResultId);
    }

    const initRelationships = () => {
      userService()
        .retrieve()
        .then(res => {
          users.value = res.data;
        });
      personalityTestService()
        .retrieve()
        .then(res => {
          personalityTests.value = res.data;
        });
      personalityTypeService()
        .retrieve()
        .then(res => {
          personalityTypes.value = res.data;
        });
    };

    initRelationships();

    const dataUtils = useDataUtils();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      analysis: {},
      resultDate: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      user: {},
      personalityTest: {},
      personalityType: {},
    };
    const v$ = useVuelidate(validationRules, testResult as any);
    v$.value.$validate();

    return {
      testResultService,
      alertService,
      testResult,
      previousState,
      isSaving,
      currentLanguage,
      users,
      personalityTests,
      personalityTypes,
      ...dataUtils,
      v$,
      ...useDateFormat({ entityRef: testResult }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.testResult.id) {
        this.testResultService()
          .update(this.testResult)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('tamkeenApp.testResult.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.testResultService()
          .create(this.testResult)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('tamkeenApp.testResult.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
