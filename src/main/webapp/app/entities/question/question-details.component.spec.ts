/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import QuestionDetails from './question-details.vue';
import QuestionService from './question.service';
import AlertService from '@/shared/alert/alert.service';

type QuestionDetailsComponentType = InstanceType<typeof QuestionDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const questionSample = { id: 'ABC' };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Question Management Detail Component', () => {
    let questionServiceStub: SinonStubbedInstance<QuestionService>;
    let mountOptions: MountingOptions<QuestionDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      questionServiceStub = sinon.createStubInstance<QuestionService>(QuestionService);

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'router-link': true,
        },
        provide: {
          alertService,
          questionService: () => questionServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        questionServiceStub.find.resolves(questionSample);
        route = {
          params: {
            questionId: '' + 'ABC',
          },
        };
        const wrapper = shallowMount(QuestionDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.question).toMatchObject(questionSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        questionServiceStub.find.resolves(questionSample);
        const wrapper = shallowMount(QuestionDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
