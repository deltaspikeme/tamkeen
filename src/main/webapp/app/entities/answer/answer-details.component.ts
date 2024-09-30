import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import AnswerService from './answer.service';
import { type IAnswer } from '@/shared/model/answer.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'AnswerDetails',
  setup() {
    const answerService = inject('answerService', () => new AnswerService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const answer: Ref<IAnswer> = ref({});

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

    return {
      alertService,
      answer,

      previousState,
      t$: useI18n().t,
    };
  },
});
