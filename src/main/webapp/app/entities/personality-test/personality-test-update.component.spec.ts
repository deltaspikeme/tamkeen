/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import PersonalityTestUpdate from './personality-test-update.vue';
import PersonalityTestService from './personality-test.service';
import AlertService from '@/shared/alert/alert.service';

type PersonalityTestUpdateComponentType = InstanceType<typeof PersonalityTestUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const personalityTestSample = { id: 'ABC' };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<PersonalityTestUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('PersonalityTest Management Update Component', () => {
    let comp: PersonalityTestUpdateComponentType;
    let personalityTestServiceStub: SinonStubbedInstance<PersonalityTestService>;

    beforeEach(() => {
      route = {};
      personalityTestServiceStub = sinon.createStubInstance<PersonalityTestService>(PersonalityTestService);
      personalityTestServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          personalityTestService: () => personalityTestServiceStub,
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(PersonalityTestUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.personalityTest = personalityTestSample;
        personalityTestServiceStub.update.resolves(personalityTestSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(personalityTestServiceStub.update.calledWith(personalityTestSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        personalityTestServiceStub.create.resolves(entity);
        const wrapper = shallowMount(PersonalityTestUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.personalityTest = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(personalityTestServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        personalityTestServiceStub.find.resolves(personalityTestSample);
        personalityTestServiceStub.retrieve.resolves([personalityTestSample]);

        // WHEN
        route = {
          params: {
            personalityTestId: `${personalityTestSample.id}`,
          },
        };
        const wrapper = shallowMount(PersonalityTestUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.personalityTest).toMatchObject(personalityTestSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        personalityTestServiceStub.find.resolves(personalityTestSample);
        const wrapper = shallowMount(PersonalityTestUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
