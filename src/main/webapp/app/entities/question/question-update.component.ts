import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import QuestionService from './question.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import PersonalityTestService from '@/entities/personality-test/personality-test.service';
import { type IPersonalityTest } from '@/shared/model/personality-test.model';
import { type IQuestion, Question } from '@/shared/model/question.model';
import { QuestionType } from '@/shared/model/enumerations/question-type.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'QuestionUpdate',
  setup() {
    const questionService = inject('questionService', () => new QuestionService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const question: Ref<IQuestion> = ref(new Question());

    const personalityTestService = inject('personalityTestService', () => new PersonalityTestService());

    const personalityTests: Ref<IPersonalityTest[]> = ref([]);
    const questionTypeValues: Ref<string[]> = ref(Object.keys(QuestionType));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'ar-ly'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveQuestion = async questionId => {
      try {
        const res = await questionService().find(questionId);
        question.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.questionId) {
      retrieveQuestion(route.params.questionId);
    }

    const initRelationships = () => {
      personalityTestService()
        .retrieve()
        .then(res => {
          personalityTests.value = res.data;
        });
    };

    initRelationships();

    const dataUtils = useDataUtils();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      questionText: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      questionType: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      personalityTest: {},
    };
    const v$ = useVuelidate(validationRules, question as any);
    v$.value.$validate();

    return {
      questionService,
      alertService,
      question,
      previousState,
      questionTypeValues,
      isSaving,
      currentLanguage,
      personalityTests,
      ...dataUtils,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.question.id) {
        this.questionService()
          .update(this.question)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('tamkeenApp.question.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.questionService()
          .create(this.question)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('tamkeenApp.question.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
