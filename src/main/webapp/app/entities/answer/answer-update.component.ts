import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import AnswerService from './answer.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import QuestionService from '@/entities/question/question.service';
import { type IQuestion } from '@/shared/model/question.model';
import { Answer, type IAnswer } from '@/shared/model/answer.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'AnswerUpdate',
  setup() {
    const answerService = inject('answerService', () => new AnswerService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const answer: Ref<IAnswer> = ref(new Answer());

    const questionService = inject('questionService', () => new QuestionService());

    const questions: Ref<IQuestion[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'ar-ly'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveAnswer = async answerId => {
      try {
        const res = await answerService().find(answerId);
        answer.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.answerId) {
      retrieveAnswer(route.params.answerId);
    }

    const initRelationships = () => {
      questionService()
        .retrieve()
        .then(res => {
          questions.value = res.data;
        });
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      answerText: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      score: {
        required: validations.required(t$('entity.validation.required').toString()),
        integer: validations.integer(t$('entity.validation.number').toString()),
      },
      question: {},
    };
    const v$ = useVuelidate(validationRules, answer as any);
    v$.value.$validate();

    return {
      answerService,
      alertService,
      answer,
      previousState,
      isSaving,
      currentLanguage,
      questions,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.answer.id) {
        this.answerService()
          .update(this.answer)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('tamkeenApp.answer.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.answerService()
          .create(this.answer)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('tamkeenApp.answer.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
