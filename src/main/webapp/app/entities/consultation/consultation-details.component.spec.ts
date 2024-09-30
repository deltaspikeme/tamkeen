/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import ConsultationDetails from './consultation-details.vue';
import ConsultationService from './consultation.service';
import AlertService from '@/shared/alert/alert.service';

type ConsultationDetailsComponentType = InstanceType<typeof ConsultationDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const consultationSample = { id: 'ABC' };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Consultation Management Detail Component', () => {
    let consultationServiceStub: SinonStubbedInstance<ConsultationService>;
    let mountOptions: MountingOptions<ConsultationDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      consultationServiceStub = sinon.createStubInstance<ConsultationService>(ConsultationService);

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
          consultationService: () => consultationServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        consultationServiceStub.find.resolves(consultationSample);
        route = {
          params: {
            consultationId: '' + 'ABC',
          },
        };
        const wrapper = shallowMount(ConsultationDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.consultation).toMatchObject(consultationSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        consultationServiceStub.find.resolves(consultationSample);
        const wrapper = shallowMount(ConsultationDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
