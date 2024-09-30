import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import UserSubscriptionService from './user-subscription.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import { type IUserSubscription, UserSubscription } from '@/shared/model/user-subscription.model';
import { SubscriptionType } from '@/shared/model/enumerations/subscription-type.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'UserSubscriptionUpdate',
  setup() {
    const userSubscriptionService = inject('userSubscriptionService', () => new UserSubscriptionService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const userSubscription: Ref<IUserSubscription> = ref(new UserSubscription());
    const userService = inject('userService', () => new UserService());
    const users: Ref<Array<any>> = ref([]);
    const subscriptionTypeValues: Ref<string[]> = ref(Object.keys(SubscriptionType));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'ar-ly'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

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

    const initRelationships = () => {
      userService()
        .retrieve()
        .then(res => {
          users.value = res.data;
        });
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      subscriptionType: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      startDate: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      endDate: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      user: {},
    };
    const v$ = useVuelidate(validationRules, userSubscription as any);
    v$.value.$validate();

    return {
      userSubscriptionService,
      alertService,
      userSubscription,
      previousState,
      subscriptionTypeValues,
      isSaving,
      currentLanguage,
      users,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.userSubscription.id) {
        this.userSubscriptionService()
          .update(this.userSubscription)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('tamkeenApp.userSubscription.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.userSubscriptionService()
          .create(this.userSubscription)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('tamkeenApp.userSubscription.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
