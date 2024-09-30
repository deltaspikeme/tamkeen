/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import AnswerDetails from './answer-details.vue';
import AnswerService from './answer.service';
import AlertService from '@/shared/alert/alert.service';

type AnswerDetailsComponentType = InstanceType<typeof AnswerDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const answerSample = { id: 'ABC' };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Answer Management Detail Component', () => {
    let answerServiceStub: SinonStubbedInstance<AnswerService>;
    let mountOptions: MountingOptions<AnswerDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      answerServiceStub = sinon.createStubInstance<AnswerService>(AnswerService);

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
          answerService: () => answerServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        answerServiceStub.find.resolves(answerSample);
        route = {
          params: {
            answerId: '' + 'ABC',
          },
        };
        const wrapper = shallowMount(AnswerDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.answer).toMatchObject(answerSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        answerServiceStub.find.resolves(answerSample);
        const wrapper = shallowMount(AnswerDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
