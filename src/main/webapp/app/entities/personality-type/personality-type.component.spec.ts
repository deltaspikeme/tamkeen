/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import PersonalityType from './personality-type.vue';
import PersonalityTypeService from './personality-type.service';
import AlertService from '@/shared/alert/alert.service';

type PersonalityTypeComponentType = InstanceType<typeof PersonalityType>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('PersonalityType Management Component', () => {
    let personalityTypeServiceStub: SinonStubbedInstance<PersonalityTypeService>;
    let mountOptions: MountingOptions<PersonalityTypeComponentType>['global'];

    beforeEach(() => {
      personalityTypeServiceStub = sinon.createStubInstance<PersonalityTypeService>(PersonalityTypeService);
      personalityTypeServiceStub.retrieve.resolves({ headers: {} });

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
          personalityTypeService: () => personalityTypeServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        personalityTypeServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 'ABC' }] });

        // WHEN
        const wrapper = shallowMount(PersonalityType, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(personalityTypeServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.personalityTypes[0]).toEqual(expect.objectContaining({ id: 'ABC' }));
      });

      it('should calculate the sort attribute for an id', async () => {
        // WHEN
        const wrapper = shallowMount(PersonalityType, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(personalityTypeServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ['id,asc'],
        });
      });
    });
    describe('Handles', () => {
      let comp: PersonalityTypeComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(PersonalityType, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        personalityTypeServiceStub.retrieve.reset();
        personalityTypeServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('should load a page', async () => {
        // GIVEN
        personalityTypeServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 'ABC' }] });

        // WHEN
        comp.page = 2;
        await comp.$nextTick();

        // THEN
        expect(personalityTypeServiceStub.retrieve.called).toBeTruthy();
        expect(comp.personalityTypes[0]).toEqual(expect.objectContaining({ id: 'ABC' }));
      });

      it('should not load a page if the page is the same as the previous page', () => {
        // WHEN
        comp.page = 1;

        // THEN
        expect(personalityTypeServiceStub.retrieve.called).toBeFalsy();
      });

      it('should re-initialize the page', async () => {
        // GIVEN
        comp.page = 2;
        await comp.$nextTick();
        personalityTypeServiceStub.retrieve.reset();
        personalityTypeServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 'ABC' }] });

        // WHEN
        comp.clear();
        await comp.$nextTick();

        // THEN
        expect(comp.page).toEqual(1);
        expect(personalityTypeServiceStub.retrieve.callCount).toEqual(1);
        expect(comp.personalityTypes[0]).toEqual(expect.objectContaining({ id: 'ABC' }));
      });

      it('should calculate the sort attribute for a non-id attribute', async () => {
        // WHEN
        comp.propOrder = 'name';
        await comp.$nextTick();

        // THEN
        expect(personalityTypeServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ['name,asc', 'id'],
        });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        personalityTypeServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 'ABC' });

        comp.removePersonalityType();
        await comp.$nextTick(); // clear components

        // THEN
        expect(personalityTypeServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(personalityTypeServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
