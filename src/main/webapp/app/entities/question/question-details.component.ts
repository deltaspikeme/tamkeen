import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import QuestionService from './question.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { type IQuestion } from '@/shared/model/question.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'QuestionDetails',
  setup() {
    const questionService = inject('questionService', () => new QuestionService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const dataUtils = useDataUtils();

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const question: Ref<IQuestion> = ref({});

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

    return {
      alertService,
      question,

      ...dataUtils,

      previousState,
      t$: useI18n().t,
    };
  },
});
