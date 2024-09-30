/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import PersonalityTypeDetails from './personality-type-details.vue';
import PersonalityTypeService from './personality-type.service';
import AlertService from '@/shared/alert/alert.service';

type PersonalityTypeDetailsComponentType = InstanceType<typeof PersonalityTypeDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const personalityTypeSample = { id: 'ABC' };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('PersonalityType Management Detail Component', () => {
    let personalityTypeServiceStub: SinonStubbedInstance<PersonalityTypeService>;
    let mountOptions: MountingOptions<PersonalityTypeDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      personalityTypeServiceStub = sinon.createStubInstance<PersonalityTypeService>(PersonalityTypeService);

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
          personalityTypeService: () => personalityTypeServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        personalityTypeServiceStub.find.resolves(personalityTypeSample);
        route = {
          params: {
            personalityTypeId: '' + 'ABC',
          },
        };
        const wrapper = shallowMount(PersonalityTypeDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.personalityType).toMatchObject(personalityTypeSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        personalityTypeServiceStub.find.resolves(personalityTypeSample);
        const wrapper = shallowMount(PersonalityTypeDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
