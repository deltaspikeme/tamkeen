import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import TestResultService from './test-result.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useDateFormat } from '@/shared/composables';
import { type ITestResult } from '@/shared/model/test-result.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'TestResultDetails',
  setup() {
    const dateFormat = useDateFormat();
    const testResultService = inject('testResultService', () => new TestResultService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const dataUtils = useDataUtils();

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const testResult: Ref<ITestResult> = ref({});

    const retrieveTestResult = async testResultId => {
      try {
        const res = await testResultService().find(testResultId);
        testResult.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.testResultId) {
      retrieveTestResult(route.params.testResultId);
    }

    return {
      ...dateFormat,
      alertService,
      testResult,

      ...dataUtils,

      previousState,
      t$: useI18n().t,
    };
  },
});
