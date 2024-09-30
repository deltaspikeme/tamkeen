/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import QuestionUpdate from './question-update.vue';
import QuestionService from './question.service';
import AlertService from '@/shared/alert/alert.service';

import PersonalityTestService from '@/entities/personality-test/personality-test.service';

type QuestionUpdateComponentType = InstanceType<typeof QuestionUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const questionSample = { id: 'ABC' };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<QuestionUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Question Management Update Component', () => {
    let comp: QuestionUpdateComponentType;
    let questionServiceStub: SinonStubbedInstance<QuestionService>;

    beforeEach(() => {
      route = {};
      questionServiceStub = sinon.createStubInstance<QuestionService>(QuestionService);
      questionServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          questionService: () => questionServiceStub,
          personalityTestService: () =>
            sinon.createStubInstance<PersonalityTestService>(PersonalityTestService, {
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
        const wrapper = shallowMount(QuestionUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.question = questionSample;
        questionServiceStub.update.resolves(questionSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(questionServiceStub.update.calledWith(questionSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        questionServiceStub.create.resolves(entity);
        const wrapper = shallowMount(QuestionUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.question = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(questionServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        questionServiceStub.find.resolves(questionSample);
        questionServiceStub.retrieve.resolves([questionSample]);

        // WHEN
        route = {
          params: {
            questionId: `${questionSample.id}`,
          },
        };
        const wrapper = shallowMount(QuestionUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.question).toMatchObject(questionSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        questionServiceStub.find.resolves(questionSample);
        const wrapper = shallowMount(QuestionUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
