import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import ConsultationService from './consultation.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useDateFormat } from '@/shared/composables';
import { type IConsultation } from '@/shared/model/consultation.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ConsultationDetails',
  setup() {
    const dateFormat = useDateFormat();
    const consultationService = inject('consultationService', () => new ConsultationService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const dataUtils = useDataUtils();

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const consultation: Ref<IConsultation> = ref({});

    const retrieveConsultation = async consultationId => {
      try {
        const res = await consultationService().find(consultationId);
        consultation.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.consultationId) {
      retrieveConsultation(route.params.consultationId);
    }

    return {
      ...dateFormat,
      alertService,
      consultation,

      ...dataUtils,

      previousState,
      t$: useI18n().t,
    };
  },
});
