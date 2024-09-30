/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import ConsultationUpdate from './consultation-update.vue';
import ConsultationService from './consultation.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import ConsultantService from '@/entities/consultant/consultant.service';

type ConsultationUpdateComponentType = InstanceType<typeof ConsultationUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const consultationSample = { id: 'ABC' };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<ConsultationUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Consultation Management Update Component', () => {
    let comp: ConsultationUpdateComponentType;
    let consultationServiceStub: SinonStubbedInstance<ConsultationService>;

    beforeEach(() => {
      route = {};
      consultationServiceStub = sinon.createStubInstance<ConsultationService>(ConsultationService);
      consultationServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          consultationService: () => consultationServiceStub,
          consultantService: () =>
            sinon.createStubInstance<ConsultantService>(ConsultantService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(ConsultationUpdate, { global: mountOptions });
        comp = wrapper.vm;
      });
      it('Should convert date from string', () => {
        // GIVEN
        const date = new Date('2019-10-15T11:42:02Z');

        // WHEN
        const convertedDate = comp.convertDateTimeFromServer(date);

        // THEN
        expect(convertedDate).toEqual(dayjs(date).format(DATE_TIME_LONG_FORMAT));
      });

      it('Should not convert date if date is not present', () => {
        expect(comp.convertDateTimeFromServer(null)).toBeNull();
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(ConsultationUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.consultation = consultationSample;
        consultationServiceStub.update.resolves(consultationSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(consultationServiceStub.update.calledWith(consultationSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        consultationServiceStub.create.resolves(entity);
        const wrapper = shallowMount(ConsultationUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.consultation = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(consultationServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        consultationServiceStub.find.resolves(consultationSample);
        consultationServiceStub.retrieve.resolves([consultationSample]);

        // WHEN
        route = {
          params: {
            consultationId: `${consultationSample.id}`,
          },
        };
        const wrapper = shallowMount(ConsultationUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.consultation).toMatchObject(consultationSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        consultationServiceStub.find.resolves(consultationSample);
        const wrapper = shallowMount(ConsultationUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
