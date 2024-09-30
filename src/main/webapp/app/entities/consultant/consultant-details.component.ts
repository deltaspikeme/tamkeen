import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import ConsultantService from './consultant.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { type IConsultant } from '@/shared/model/consultant.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ConsultantDetails',
  setup() {
    const consultantService = inject('consultantService', () => new ConsultantService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const dataUtils = useDataUtils();

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const consultant: Ref<IConsultant> = ref({});

    const retrieveConsultant = async consultantId => {
      try {
        const res = await consultantService().find(consultantId);
        consultant.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.consultantId) {
      retrieveConsultant(route.params.consultantId);
    }

    return {
      alertService,
      consultant,

      ...dataUtils,

      previousState,
      t$: useI18n().t,
    };
  },
});
