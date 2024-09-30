/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import PersonalityTypeUpdate from './personality-type-update.vue';
import PersonalityTypeService from './personality-type.service';
import AlertService from '@/shared/alert/alert.service';

type PersonalityTypeUpdateComponentType = InstanceType<typeof PersonalityTypeUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const personalityTypeSample = { id: 'ABC' };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<PersonalityTypeUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('PersonalityType Management Update Component', () => {
    let comp: PersonalityTypeUpdateComponentType;
    let personalityTypeServiceStub: SinonStubbedInstance<PersonalityTypeService>;

    beforeEach(() => {
      route = {};
      personalityTypeServiceStub = sinon.createStubInstance<PersonalityTypeService>(PersonalityTypeService);
      personalityTypeServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          personalityTypeService: () => personalityTypeServiceStub,
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(PersonalityTypeUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.personalityType = personalityTypeSample;
        personalityTypeServiceStub.update.resolves(personalityTypeSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(personalityTypeServiceStub.update.calledWith(personalityTypeSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        personalityTypeServiceStub.create.resolves(entity);
        const wrapper = shallowMount(PersonalityTypeUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.personalityType = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(personalityTypeServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        personalityTypeServiceStub.find.resolves(personalityTypeSample);
        personalityTypeServiceStub.retrieve.resolves([personalityTypeSample]);

        // WHEN
        route = {
          params: {
            personalityTypeId: `${personalityTypeSample.id}`,
          },
        };
        const wrapper = shallowMount(PersonalityTypeUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.personalityType).toMatchObject(personalityTypeSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        personalityTypeServiceStub.find.resolves(personalityTypeSample);
        const wrapper = shallowMount(PersonalityTypeUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
