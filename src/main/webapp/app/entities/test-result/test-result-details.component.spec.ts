/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import TestResultDetails from './test-result-details.vue';
import TestResultService from './test-result.service';
import AlertService from '@/shared/alert/alert.service';

type TestResultDetailsComponentType = InstanceType<typeof TestResultDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const testResultSample = { id: 'ABC' };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('TestResult Management Detail Component', () => {
    let testResultServiceStub: SinonStubbedInstance<TestResultService>;
    let mountOptions: MountingOptions<TestResultDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      testResultServiceStub = sinon.createStubInstance<TestResultService>(TestResultService);

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
          testResultService: () => testResultServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        testResultServiceStub.find.resolves(testResultSample);
        route = {
          params: {
            testResultId: '' + 'ABC',
          },
        };
        const wrapper = shallowMount(TestResultDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.testResult).toMatchObject(testResultSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        testResultServiceStub.find.resolves(testResultSample);
        const wrapper = shallowMount(TestResultDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
