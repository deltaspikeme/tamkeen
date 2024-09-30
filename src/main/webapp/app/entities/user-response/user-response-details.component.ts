import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import UserResponseService from './user-response.service';
import { useDateFormat } from '@/shared/composables';
import { type IUserResponse } from '@/shared/model/user-response.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'UserResponseDetails',
  setup() {
    const dateFormat = useDateFormat();
    const userResponseService = inject('userResponseService', () => new UserResponseService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const userResponse: Ref<IUserResponse> = ref({});

    const retrieveUserResponse = async userResponseId => {
      try {
        const res = await userResponseService().find(userResponseId);
        userResponse.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.userResponseId) {
      retrieveUserResponse(route.params.userResponseId);
    }

    return {
      ...dateFormat,
      alertService,
      userResponse,

      previousState,
      t$: useI18n().t,
    };
  },
});
