/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import UserResponseUpdate from './user-response-update.vue';
import UserResponseService from './user-response.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import AnswerService from '@/entities/answer/answer.service';
import QuestionService from '@/entities/question/question.service';

import UserService from '@/entities/user/user.service';

type UserResponseUpdateComponentType = InstanceType<typeof UserResponseUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const userResponseSample = { id: 'ABC' };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<UserResponseUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('UserResponse Management Update Component', () => {
    let comp: UserResponseUpdateComponentType;
    let userResponseServiceStub: SinonStubbedInstance<UserResponseService>;

    beforeEach(() => {
      route = {};
      userResponseServiceStub = sinon.createStubInstance<UserResponseService>(UserResponseService);
      userResponseServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          userResponseService: () => userResponseServiceStub,
          answerService: () =>
            sinon.createStubInstance<AnswerService>(AnswerService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          questionService: () =>
            sinon.createStubInstance<QuestionService>(QuestionService, {
              retrieve: sinon.stub().resolves({}),
            } as any),

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

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(UserResponseUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(UserResponseUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.userResponse = userResponseSample;
        userResponseServiceStub.update.resolves(userResponseSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(userResponseServiceStub.update.calledWith(userResponseSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        userResponseServiceStub.create.resolves(entity);
        const wrapper = shallowMount(UserResponseUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.userResponse = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(userResponseServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        userResponseServiceStub.find.resolves(userResponseSample);
        userResponseServiceStub.retrieve.resolves([userResponseSample]);

        // WHEN
        route = {
          params: {
            userResponseId: `${userResponseSample.id}`,
          },
        };
        const wrapper = shallowMount(UserResponseUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.userResponse).toMatchObject(userResponseSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        userResponseServiceStub.find.resolves(userResponseSample);
        const wrapper = shallowMount(UserResponseUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
