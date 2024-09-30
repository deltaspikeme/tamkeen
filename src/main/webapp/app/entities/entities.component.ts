import { defineComponent, provide } from 'vue';

import PersonalityTestService from './personality-test/personality-test.service';
import QuestionService from './question/question.service';
import AnswerService from './answer/answer.service';
import UserResponseService from './user-response/user-response.service';
import TestResultService from './test-result/test-result.service';
import PersonalityTypeService from './personality-type/personality-type.service';
import UserSubscriptionService from './user-subscription/user-subscription.service';
import ConsultationService from './consultation/consultation.service';
import ConsultantService from './consultant/consultant.service';
import UserService from '@/entities/user/user.service';
// jhipster-needle-add-entity-service-to-entities-component-import - JHipster will import entities services here

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Entities',
  setup() {
    provide('userService', () => new UserService());
    provide('personalityTestService', () => new PersonalityTestService());
    provide('questionService', () => new QuestionService());
    provide('answerService', () => new AnswerService());
    provide('userResponseService', () => new UserResponseService());
    provide('testResultService', () => new TestResultService());
    provide('personalityTypeService', () => new PersonalityTypeService());
    provide('userSubscriptionService', () => new UserSubscriptionService());
    provide('consultationService', () => new ConsultationService());
    provide('consultantService', () => new ConsultantService());
    // jhipster-needle-add-entity-service-to-entities-component - JHipster will import entities services here
  },
});
