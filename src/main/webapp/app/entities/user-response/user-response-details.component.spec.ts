/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import UserResponseDetails from './user-response-details.vue';
import UserResponseService from './user-response.service';
import AlertService from '@/shared/alert/alert.service';

type UserResponseDetailsComponentType = InstanceType<typeof UserResponseDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const userResponseSample = { id: 'ABC' };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('UserResponse Management Detail Component', () => {
    let userResponseServiceStub: SinonStubbedInstance<UserResponseService>;
    let mountOptions: MountingOptions<UserResponseDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      userResponseServiceStub = sinon.createStubInstance<UserResponseService>(UserResponseService);

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
          userResponseService: () => userResponseServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        userResponseServiceStub.find.resolves(userResponseSample);
        route = {
          params: {
            userResponseId: '' + 'ABC',
          },
        };
        const wrapper = shallowMount(UserResponseDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.userResponse).toMatchObject(userResponseSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        userResponseServiceStub.find.resolves(userResponseSample);
        const wrapper = shallowMount(UserResponseDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
