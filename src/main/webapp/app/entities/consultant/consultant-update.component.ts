import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import ConsultantService from './consultant.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import { Consultant, type IConsultant } from '@/shared/model/consultant.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ConsultantUpdate',
  setup() {
    const consultantService = inject('consultantService', () => new ConsultantService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const consultant: Ref<IConsultant> = ref(new Consultant());
    const userService = inject('userService', () => new UserService());
    const users: Ref<Array<any>> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'ar-ly'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

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

    const initRelationships = () => {
      userService()
        .retrieve()
        .then(res => {
          users.value = res.data;
        });
    };

    initRelationships();

    const dataUtils = useDataUtils();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      name: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      expertise: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      bio: {},
      email: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      phone: {},
      servicesOffered: {},
      user: {},
    };
    const v$ = useVuelidate(validationRules, consultant as any);
    v$.value.$validate();

    return {
      consultantService,
      alertService,
      consultant,
      previousState,
      isSaving,
      currentLanguage,
      users,
      ...dataUtils,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.consultant.id) {
        this.consultantService()
          .update(this.consultant)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('tamkeenApp.consultant.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.consultantService()
          .create(this.consultant)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('tamkeenApp.consultant.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
