import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import ConsultationService from './consultation.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import ConsultantService from '@/entities/consultant/consultant.service';
import { type IConsultant } from '@/shared/model/consultant.model';
import { Consultation, type IConsultation } from '@/shared/model/consultation.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ConsultationUpdate',
  setup() {
    const consultationService = inject('consultationService', () => new ConsultationService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const consultation: Ref<IConsultation> = ref(new Consultation());

    const consultantService = inject('consultantService', () => new ConsultantService());

    const consultants: Ref<IConsultant[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'ar-ly'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveConsultation = async consultationId => {
      try {
        const res = await consultationService().find(consultationId);
        res.consultationDate = new Date(res.consultationDate);
        consultation.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.consultationId) {
      retrieveConsultation(route.params.consultationId);
    }

    const initRelationships = () => {
      consultantService()
        .retrieve()
        .then(res => {
          consultants.value = res.data;
        });
    };

    initRelationships();

    const dataUtils = useDataUtils();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      consultantName: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      consultationDate: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      notes: {},
      consultant: {},
    };
    const v$ = useVuelidate(validationRules, consultation as any);
    v$.value.$validate();

    return {
      consultationService,
      alertService,
      consultation,
      previousState,
      isSaving,
      currentLanguage,
      consultants,
      ...dataUtils,
      v$,
      ...useDateFormat({ entityRef: consultation }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.consultation.id) {
        this.consultationService()
          .update(this.consultation)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('tamkeenApp.consultation.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.consultationService()
          .create(this.consultation)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('tamkeenApp.consultation.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
