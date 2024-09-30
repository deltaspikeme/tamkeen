/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import TestResultUpdate from './test-result-update.vue';
import TestResultService from './test-result.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import PersonalityTestService from '@/entities/personality-test/personality-test.service';
import PersonalityTypeService from '@/entities/personality-type/personality-type.service';

type TestResultUpdateComponentType = InstanceType<typeof TestResultUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const testResultSample = { id: 'ABC' };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<TestResultUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('TestResult Management Update Component', () => {
    let comp: TestResultUpdateComponentType;
    let testResultServiceStub: SinonStubbedInstance<TestResultService>;

    beforeEach(() => {
      route = {};
      testResultServiceStub = sinon.createStubInstance<TestResultService>(TestResultService);
      testResultServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          testResultService: () => testResultServiceStub,

          userService: () =>
            sinon.createStubInstance<UserService>(UserService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          personalityTestService: () =>
            sinon.createStubInstance<PersonalityTestService>(PersonalityTestService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          personalityTypeService: () =>
            sinon.createStubInstance<PersonalityTypeService>(PersonalityTypeService, {
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
        const wrapper = shallowMount(TestResultUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(TestResultUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.testResult = testResultSample;
        testResultServiceStub.update.resolves(testResultSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(testResultServiceStub.update.calledWith(testResultSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        testResultServiceStub.create.resolves(entity);
        const wrapper = shallowMount(TestResultUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.testResult = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(testResultServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        testResultServiceStub.find.resolves(testResultSample);
        testResultServiceStub.retrieve.resolves([testResultSample]);

        // WHEN
        route = {
          params: {
            testResultId: `${testResultSample.id}`,
          },
        };
        const wrapper = shallowMount(TestResultUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.testResult).toMatchObject(testResultSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        testResultServiceStub.find.resolves(testResultSample);
        const wrapper = shallowMount(TestResultUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
