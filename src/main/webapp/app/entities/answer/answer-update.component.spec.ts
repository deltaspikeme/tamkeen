/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import AnswerUpdate from './answer-update.vue';
import AnswerService from './answer.service';
import AlertService from '@/shared/alert/alert.service';

import QuestionService from '@/entities/question/question.service';

type AnswerUpdateComponentType = InstanceType<typeof AnswerUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const answerSample = { id: 'ABC' };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<AnswerUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Answer Management Update Component', () => {
    let comp: AnswerUpdateComponentType;
    let answerServiceStub: SinonStubbedInstance<AnswerService>;

    beforeEach(() => {
      route = {};
      answerServiceStub = sinon.createStubInstance<AnswerService>(AnswerService);
      answerServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          answerService: () => answerServiceStub,
          questionService: () =>
            sinon.createStubInstance<QuestionService>(QuestionService, {
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
        const wrapper = shallowMount(AnswerUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.answer = answerSample;
        answerServiceStub.update.resolves(answerSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(answerServiceStub.update.calledWith(answerSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        answerServiceStub.create.resolves(entity);
        const wrapper = shallowMount(AnswerUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.answer = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(answerServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        answerServiceStub.find.resolves(answerSample);
        answerServiceStub.retrieve.resolves([answerSample]);

        // WHEN
        route = {
          params: {
            answerId: `${answerSample.id}`,
          },
        };
        const wrapper = shallowMount(AnswerUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.answer).toMatchObject(answerSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        answerServiceStub.find.resolves(answerSample);
        const wrapper = shallowMount(AnswerUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
