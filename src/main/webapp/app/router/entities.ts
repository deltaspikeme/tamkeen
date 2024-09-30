import { Authority } from '@/shared/security/authority';
/* tslint:disable */
// prettier-ignore
const Entities = () => import('@/entities/entities.vue');

const PersonalityTest = () => import('@/entities/personality-test/personality-test.vue');
const PersonalityTestUpdate = () => import('@/entities/personality-test/personality-test-update.vue');
const PersonalityTestDetails = () => import('@/entities/personality-test/personality-test-details.vue');

const Question = () => import('@/entities/question/question.vue');
const QuestionUpdate = () => import('@/entities/question/question-update.vue');
const QuestionDetails = () => import('@/entities/question/question-details.vue');

const Answer = () => import('@/entities/answer/answer.vue');
const AnswerUpdate = () => import('@/entities/answer/answer-update.vue');
const AnswerDetails = () => import('@/entities/answer/answer-details.vue');

const UserResponse = () => import('@/entities/user-response/user-response.vue');
const UserResponseUpdate = () => import('@/entities/user-response/user-response-update.vue');
const UserResponseDetails = () => import('@/entities/user-response/user-response-details.vue');

const TestResult = () => import('@/entities/test-result/test-result.vue');
const TestResultUpdate = () => import('@/entities/test-result/test-result-update.vue');
const TestResultDetails = () => import('@/entities/test-result/test-result-details.vue');

const PersonalityType = () => import('@/entities/personality-type/personality-type.vue');
const PersonalityTypeUpdate = () => import('@/entities/personality-type/personality-type-update.vue');
const PersonalityTypeDetails = () => import('@/entities/personality-type/personality-type-details.vue');

const UserSubscription = () => import('@/entities/user-subscription/user-subscription.vue');
const UserSubscriptionUpdate = () => import('@/entities/user-subscription/user-subscription-update.vue');
const UserSubscriptionDetails = () => import('@/entities/user-subscription/user-subscription-details.vue');

const Consultation = () => import('@/entities/consultation/consultation.vue');
const ConsultationUpdate = () => import('@/entities/consultation/consultation-update.vue');
const ConsultationDetails = () => import('@/entities/consultation/consultation-details.vue');

const Consultant = () => import('@/entities/consultant/consultant.vue');
const ConsultantUpdate = () => import('@/entities/consultant/consultant-update.vue');
const ConsultantDetails = () => import('@/entities/consultant/consultant-details.vue');

// jhipster-needle-add-entity-to-router-import - JHipster will import entities to the router here

export default {
  path: '/',
  component: Entities,
  children: [
    {
      path: 'personality-test',
      name: 'PersonalityTest',
      component: PersonalityTest,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'personality-test/new',
      name: 'PersonalityTestCreate',
      component: PersonalityTestUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'personality-test/:personalityTestId/edit',
      name: 'PersonalityTestEdit',
      component: PersonalityTestUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'personality-test/:personalityTestId/view',
      name: 'PersonalityTestView',
      component: PersonalityTestDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'question',
      name: 'Question',
      component: Question,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'question/new',
      name: 'QuestionCreate',
      component: QuestionUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'question/:questionId/edit',
      name: 'QuestionEdit',
      component: QuestionUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'question/:questionId/view',
      name: 'QuestionView',
      component: QuestionDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'answer',
      name: 'Answer',
      component: Answer,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'answer/new',
      name: 'AnswerCreate',
      component: AnswerUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'answer/:answerId/edit',
      name: 'AnswerEdit',
      component: AnswerUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'answer/:answerId/view',
      name: 'AnswerView',
      component: AnswerDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'user-response',
      name: 'UserResponse',
      component: UserResponse,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'user-response/new',
      name: 'UserResponseCreate',
      component: UserResponseUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'user-response/:userResponseId/edit',
      name: 'UserResponseEdit',
      component: UserResponseUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'user-response/:userResponseId/view',
      name: 'UserResponseView',
      component: UserResponseDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'test-result',
      name: 'TestResult',
      component: TestResult,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'test-result/new',
      name: 'TestResultCreate',
      component: TestResultUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'test-result/:testResultId/edit',
      name: 'TestResultEdit',
      component: TestResultUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'test-result/:testResultId/view',
      name: 'TestResultView',
      component: TestResultDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'personality-type',
      name: 'PersonalityType',
      component: PersonalityType,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'personality-type/new',
      name: 'PersonalityTypeCreate',
      component: PersonalityTypeUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'personality-type/:personalityTypeId/edit',
      name: 'PersonalityTypeEdit',
      component: PersonalityTypeUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'personality-type/:personalityTypeId/view',
      name: 'PersonalityTypeView',
      component: PersonalityTypeDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'user-subscription',
      name: 'UserSubscription',
      component: UserSubscription,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'user-subscription/new',
      name: 'UserSubscriptionCreate',
      component: UserSubscriptionUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'user-subscription/:userSubscriptionId/edit',
      name: 'UserSubscriptionEdit',
      component: UserSubscriptionUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'user-subscription/:userSubscriptionId/view',
      name: 'UserSubscriptionView',
      component: UserSubscriptionDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'consultation',
      name: 'Consultation',
      component: Consultation,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'consultation/new',
      name: 'ConsultationCreate',
      component: ConsultationUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'consultation/:consultationId/edit',
      name: 'ConsultationEdit',
      component: ConsultationUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'consultation/:consultationId/view',
      name: 'ConsultationView',
      component: ConsultationDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'consultant',
      name: 'Consultant',
      component: Consultant,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'consultant/new',
      name: 'ConsultantCreate',
      component: ConsultantUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'consultant/:consultantId/edit',
      name: 'ConsultantEdit',
      component: ConsultantUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'consultant/:consultantId/view',
      name: 'ConsultantView',
      component: ConsultantDetails,
      meta: { authorities: [Authority.USER] },
    },
    // jhipster-needle-add-entity-to-router - JHipster will add entities to the router here
  ],
};
