/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import UserSubscription from './user-subscription.vue';
import UserSubscriptionService from './user-subscription.service';
import AlertService from '@/shared/alert/alert.service';

type UserSubscriptionComponentType = InstanceType<typeof UserSubscription>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('UserSubscription Management Component', () => {
    let userSubscriptionServiceStub: SinonStubbedInstance<UserSubscriptionService>;
    let mountOptions: MountingOptions<UserSubscriptionComponentType>['global'];

    beforeEach(() => {
      userSubscriptionServiceStub = sinon.createStubInstance<UserSubscriptionService>(UserSubscriptionService);
      userSubscriptionServiceStub.retrieve.resolves({ headers: {} });

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          jhiItemCount: true,
          bPagination: true,
          bModal: bModalStub as any,
          'font-awesome-icon': true,
          'b-badge': true,
          'jhi-sort-indicator': true,
          'b-button': true,
          'router-link': true,
        },
        directives: {
          'b-modal': {},
        },
        provide: {
          alertService,
          userSubscriptionService: () => userSubscriptionServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        userSubscriptionServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 'ABC' }] });

        // WHEN
        const wrapper = shallowMount(UserSubscription, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(userSubscriptionServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.userSubscriptions[0]).toEqual(expect.objectContaining({ id: 'ABC' }));
      });

      it('should calculate the sort attribute for an id', async () => {
        // WHEN
        const wrapper = shallowMount(UserSubscription, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(userSubscriptionServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ['id,asc'],
        });
      });
    });
    describe('Handles', () => {
      let comp: UserSubscriptionComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(UserSubscription, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        userSubscriptionServiceStub.retrieve.reset();
        userSubscriptionServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('should load a page', async () => {
        // GIVEN
        userSubscriptionServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 'ABC' }] });

        // WHEN
        comp.page = 2;
        await comp.$nextTick();

        // THEN
        expect(userSubscriptionServiceStub.retrieve.called).toBeTruthy();
        expect(comp.userSubscriptions[0]).toEqual(expect.objectContaining({ id: 'ABC' }));
      });

      it('should not load a page if the page is the same as the previous page', () => {
        // WHEN
        comp.page = 1;

        // THEN
        expect(userSubscriptionServiceStub.retrieve.called).toBeFalsy();
      });

      it('should re-initialize the page', async () => {
        // GIVEN
        comp.page = 2;
        await comp.$nextTick();
        userSubscriptionServiceStub.retrieve.reset();
        userSubscriptionServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 'ABC' }] });

        // WHEN
        comp.clear();
        await comp.$nextTick();

        // THEN
        expect(comp.page).toEqual(1);
        expect(userSubscriptionServiceStub.retrieve.callCount).toEqual(1);
        expect(comp.userSubscriptions[0]).toEqual(expect.objectContaining({ id: 'ABC' }));
      });

      it('should calculate the sort attribute for a non-id attribute', async () => {
        // WHEN
        comp.propOrder = 'name';
        await comp.$nextTick();

        // THEN
        expect(userSubscriptionServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ['name,asc', 'id'],
        });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        userSubscriptionServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 'ABC' });

        comp.removeUserSubscription();
        await comp.$nextTick(); // clear components

        // THEN
        expect(userSubscriptionServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(userSubscriptionServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
