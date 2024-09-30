/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import PersonalityTestDetails from './personality-test-details.vue';
import PersonalityTestService from './personality-test.service';
import AlertService from '@/shared/alert/alert.service';

type PersonalityTestDetailsComponentType = InstanceType<typeof PersonalityTestDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const personalityTestSample = { id: 'ABC' };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('PersonalityTest Management Detail Component', () => {
    let personalityTestServiceStub: SinonStubbedInstance<PersonalityTestService>;
    let mountOptions: MountingOptions<PersonalityTestDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      personalityTestServiceStub = sinon.createStubInstance<PersonalityTestService>(PersonalityTestService);

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
          personalityTestService: () => personalityTestServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        personalityTestServiceStub.find.resolves(personalityTestSample);
        route = {
          params: {
            personalityTestId: '' + 'ABC',
          },
        };
        const wrapper = shallowMount(PersonalityTestDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.personalityTest).toMatchObject(personalityTestSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        personalityTestServiceStub.find.resolves(personalityTestSample);
        const wrapper = shallowMount(PersonalityTestDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
