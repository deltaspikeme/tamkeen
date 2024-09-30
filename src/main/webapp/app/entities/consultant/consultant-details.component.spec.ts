/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import ConsultantDetails from './consultant-details.vue';
import ConsultantService from './consultant.service';
import AlertService from '@/shared/alert/alert.service';

type ConsultantDetailsComponentType = InstanceType<typeof ConsultantDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const consultantSample = { id: 'ABC' };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Consultant Management Detail Component', () => {
    let consultantServiceStub: SinonStubbedInstance<ConsultantService>;
    let mountOptions: MountingOptions<ConsultantDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      consultantServiceStub = sinon.createStubInstance<ConsultantService>(ConsultantService);

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
          consultantService: () => consultantServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        consultantServiceStub.find.resolves(consultantSample);
        route = {
          params: {
            consultantId: '' + 'ABC',
          },
        };
        const wrapper = shallowMount(ConsultantDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.consultant).toMatchObject(consultantSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        consultantServiceStub.find.resolves(consultantSample);
        const wrapper = shallowMount(ConsultantDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
