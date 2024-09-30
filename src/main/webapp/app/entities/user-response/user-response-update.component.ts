import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import UserResponseService from './user-response.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import AnswerService from '@/entities/answer/answer.service';
import { type IAnswer } from '@/shared/model/answer.model';
import QuestionService from '@/entities/question/question.service';
import { type IQuestion } from '@/shared/model/question.model';
import UserService from '@/entities/user/user.service';
import { type IUserResponse, UserResponse } from '@/shared/model/user-response.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'UserResponseUpdate',
  setup() {
    const userResponseService = inject('userResponseService', () => new UserResponseService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const userResponse: Ref<IUserResponse> = ref(new UserResponse());

    const answerService = inject('answerService', () => new AnswerService());

    const answers: Ref<IAnswer[]> = ref([]);

    const questionService = inject('questionService', () => new QuestionService());

    const questions: Ref<IQuestion[]> = ref([]);
    const userService = inject('userService', () => new UserService());
    const users: Ref<Array<any>> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'ar-ly'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveUserResponse = async userResponseId => {
      try {
        const res = await userResponseService().find(userResponseId);
        res.responseDate = new Date(res.responseDate);
        userResponse.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.userResponseId) {
      retrieveUserResponse(route.params.userResponseId);
    }

    const initRelationships = () => {
      answerService()
        .retrieve()
        .then(res => {
          answers.value = res.data;
        });
      questionService()
        .retrieve()
        .then(res => {
          questions.value = res.data;
        });
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
      responseDate: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      answer: {},
      question: {},
      user: {},
    };
    const v$ = useVuelidate(validationRules, userResponse as any);
    v$.value.$validate();

    return {
      userResponseService,
      alertService,
      userResponse,
      previousState,
      isSaving,
      currentLanguage,
      answers,
      questions,
      users,
      v$,
      ...useDateFormat({ entityRef: userResponse }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.userResponse.id) {
        this.userResponseService()
          .update(this.userResponse)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('tamkeenApp.userResponse.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.userResponseService()
          .create(this.userResponse)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('tamkeenApp.userResponse.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
