/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import ConsultantUpdate from './consultant-update.vue';
import ConsultantService from './consultant.service';
import AlertService from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';

type ConsultantUpdateComponentType = InstanceType<typeof ConsultantUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const consultantSample = { id: 'ABC' };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<ConsultantUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Consultant Management Update Component', () => {
    let comp: ConsultantUpdateComponentType;
    let consultantServiceStub: SinonStubbedInstance<ConsultantService>;

    beforeEach(() => {
      route = {};
      consultantServiceStub = sinon.createStubInstance<ConsultantService>(ConsultantService);
      consultantServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          consultantService: () => consultantServiceStub,

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
        const wrapper = shallowMount(ConsultantUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.consultant = consultantSample;
        consultantServiceStub.update.resolves(consultantSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(consultantServiceStub.update.calledWith(consultantSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        consultantServiceStub.create.resolves(entity);
        const wrapper = shallowMount(ConsultantUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.consultant = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(consultantServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        consultantServiceStub.find.resolves(consultantSample);
        consultantServiceStub.retrieve.resolves([consultantSample]);

        // WHEN
        route = {
          params: {
            consultantId: `${consultantSample.id}`,
          },
        };
        const wrapper = shallowMount(ConsultantUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.consultant).toMatchObject(consultantSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        consultantServiceStub.find.resolves(consultantSample);
        const wrapper = shallowMount(ConsultantUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
