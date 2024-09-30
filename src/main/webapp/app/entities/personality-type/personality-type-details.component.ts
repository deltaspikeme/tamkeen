import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import PersonalityTypeService from './personality-type.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { type IPersonalityType } from '@/shared/model/personality-type.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'PersonalityTypeDetails',
  setup() {
    const personalityTypeService = inject('personalityTypeService', () => new PersonalityTypeService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const dataUtils = useDataUtils();

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const personalityType: Ref<IPersonalityType> = ref({});

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

    return {
      alertService,
      personalityType,

      ...dataUtils,

      previousState,
      t$: useI18n().t,
    };
  },
});
