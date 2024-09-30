/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import UserSubscriptionDetails from './user-subscription-details.vue';
import UserSubscriptionService from './user-subscription.service';
import AlertService from '@/shared/alert/alert.service';

type UserSubscriptionDetailsComponentType = InstanceType<typeof UserSubscriptionDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const userSubscriptionSample = { id: 'ABC' };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('UserSubscription Management Detail Component', () => {
    let userSubscriptionServiceStub: SinonStubbedInstance<UserSubscriptionService>;
    let mountOptions: MountingOptions<UserSubscriptionDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      userSubscriptionServiceStub = sinon.createStubInstance<UserSubscriptionService>(UserSubscriptionService);

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
          userSubscriptionService: () => userSubscriptionServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        userSubscriptionServiceStub.find.resolves(userSubscriptionSample);
        route = {
          params: {
            userSubscriptionId: '' + 'ABC',
          },
        };
        const wrapper = shallowMount(UserSubscriptionDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.userSubscription).toMatchObject(userSubscriptionSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        userSubscriptionServiceStub.find.resolves(userSubscriptionSample);
        const wrapper = shallowMount(UserSubscriptionDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
