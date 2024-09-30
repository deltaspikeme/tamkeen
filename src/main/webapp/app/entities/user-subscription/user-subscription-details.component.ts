import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import UserSubscriptionService from './user-subscription.service';
import { type IUserSubscription } from '@/shared/model/user-subscription.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'UserSubscriptionDetails',
  setup() {
    const userSubscriptionService = inject('userSubscriptionService', () => new UserSubscriptionService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const userSubscription: Ref<IUserSubscription> = ref({});

    const retrieveUserSubscription = async userSubscriptionId => {
      try {
        const res = await userSubscriptionService().find(userSubscriptionId);
        userSubscription.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.userSubscriptionId) {
      retrieveUserSubscription(route.params.userSubscriptionId);
    }

    return {
      alertService,
      userSubscription,

      previousState,
      t$: useI18n().t,
    };
  },
});
