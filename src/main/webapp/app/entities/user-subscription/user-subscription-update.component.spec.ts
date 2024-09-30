/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import UserSubscriptionUpdate from './user-subscription-update.vue';
import UserSubscriptionService from './user-subscription.service';
import AlertService from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';

type UserSubscriptionUpdateComponentType = InstanceType<typeof UserSubscriptionUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const userSubscriptionSample = { id: 'ABC' };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<UserSubscriptionUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('UserSubscription Management Update Component', () => {
    let comp: UserSubscriptionUpdateComponentType;
    let userSubscriptionServiceStub: SinonStubbedInstance<UserSubscriptionService>;

    beforeEach(() => {
      route = {};
      userSubscriptionServiceStub = sinon.createStubInstance<UserSubscriptionService>(UserSubscriptionService);
      userSubscriptionServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'b-input-group': true,
          'b-input-group-prepend': true,
          'b-form-datepicker': true,
          'b-form-input': true,
        },
        provide: {
          alertService,
          userSubscriptionService: () => userSubscriptionServiceStub,

          userService: () =>
            sinon.createStubInstance<UserService>(UserService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(UserSubscriptionUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.userSubscription = userSubscriptionSample;
        userSubscriptionServiceStub.update.resolves(userSubscriptionSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(userSubscriptionServiceStub.update.calledWith(userSubscriptionSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        userSubscriptionServiceStub.create.resolves(entity);
        const wrapper = shallowMount(UserSubscriptionUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.userSubscription = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(userSubscriptionServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        userSubscriptionServiceStub.find.resolves(userSubscriptionSample);
        userSubscriptionServiceStub.retrieve.resolves([userSubscriptionSample]);

        // WHEN
        route = {
          params: {
            userSubscriptionId: `${userSubscriptionSample.id}`,
          },
        };
        const wrapper = shallowMount(UserSubscriptionUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.userSubscription).toMatchObject(userSubscriptionSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        userSubscriptionServiceStub.find.resolves(userSubscriptionSample);
        const wrapper = shallowMount(UserSubscriptionUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
