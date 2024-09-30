// personality-test-details.component.ts
import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import PersonalityTestService from './personality-test.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { type IPersonalityTest } from '@/shared/model/personality-test.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'PersonalityTestDetails',
  setup() {
    const personalityTestService = inject('personalityTestService', () => new PersonalityTestService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const dataUtils = useDataUtils();

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const personalityTest: Ref<IPersonalityTest> = ref({});

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

    return {
      alertService,
      personalityTest,

      ...dataUtils,

      previousState,
      t$: useI18n().t,
    };
  },
});
