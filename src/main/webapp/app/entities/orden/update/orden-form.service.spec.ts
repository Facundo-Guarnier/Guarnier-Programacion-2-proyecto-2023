import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../orden.test-samples';

import { OrdenFormService } from './orden-form.service';

describe('Orden Form Service', () => {
  let service: OrdenFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OrdenFormService);
  });

  describe('Service methods', () => {
    describe('createOrdenFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createOrdenFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            cliente: expect.any(Object),
            accionId: expect.any(Object),
            accion: expect.any(Object),
            operacion: expect.any(Object),
            precio: expect.any(Object),
            cantidad: expect.any(Object),
            fechaOperacion: expect.any(Object),
            modo: expect.any(Object),
            estado: expect.any(Object),
          })
        );
      });

      it('passing IOrden should create a new form with FormGroup', () => {
        const formGroup = service.createOrdenFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            cliente: expect.any(Object),
            accionId: expect.any(Object),
            accion: expect.any(Object),
            operacion: expect.any(Object),
            precio: expect.any(Object),
            cantidad: expect.any(Object),
            fechaOperacion: expect.any(Object),
            modo: expect.any(Object),
            estado: expect.any(Object),
          })
        );
      });
    });

    describe('getOrden', () => {
      it('should return NewOrden for default Orden initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createOrdenFormGroup(sampleWithNewData);

        const orden = service.getOrden(formGroup) as any;

        expect(orden).toMatchObject(sampleWithNewData);
      });

      it('should return NewOrden for empty Orden initial value', () => {
        const formGroup = service.createOrdenFormGroup();

        const orden = service.getOrden(formGroup) as any;

        expect(orden).toMatchObject({});
      });

      it('should return IOrden', () => {
        const formGroup = service.createOrdenFormGroup(sampleWithRequiredData);

        const orden = service.getOrden(formGroup) as any;

        expect(orden).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IOrden should not enable id FormControl', () => {
        const formGroup = service.createOrdenFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewOrden should disable id FormControl', () => {
        const formGroup = service.createOrdenFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
