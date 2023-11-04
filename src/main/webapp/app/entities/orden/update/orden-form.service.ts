import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IOrden, NewOrden } from '../orden.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IOrden for edit and NewOrdenFormGroupInput for create.
 */
type OrdenFormGroupInput = IOrden | PartialWithRequiredKeyOf<NewOrden>;

type OrdenFormDefaults = Pick<NewOrden, 'id'>;

type OrdenFormGroupContent = {
  id: FormControl<IOrden['id'] | NewOrden['id']>;
  cliente: FormControl<IOrden['cliente']>;
  accionId: FormControl<IOrden['accionId']>;
  accion: FormControl<IOrden['accion']>;
  operacion: FormControl<IOrden['operacion']>;
  precio: FormControl<IOrden['precio']>;
  cantidad: FormControl<IOrden['cantidad']>;
  fechaOperacion: FormControl<IOrden['fechaOperacion']>;
  modo: FormControl<IOrden['modo']>;
  estado: FormControl<IOrden['estado']>;
};

export type OrdenFormGroup = FormGroup<OrdenFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class OrdenFormService {
  createOrdenFormGroup(orden: OrdenFormGroupInput = { id: null }): OrdenFormGroup {
    const ordenRawValue = {
      ...this.getFormDefaults(),
      ...orden,
    };
    return new FormGroup<OrdenFormGroupContent>({
      id: new FormControl(
        { value: ordenRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      cliente: new FormControl(ordenRawValue.cliente),
      accionId: new FormControl(ordenRawValue.accionId),
      accion: new FormControl(ordenRawValue.accion),
      operacion: new FormControl(ordenRawValue.operacion),
      precio: new FormControl(ordenRawValue.precio),
      cantidad: new FormControl(ordenRawValue.cantidad),
      fechaOperacion: new FormControl(ordenRawValue.fechaOperacion),
      modo: new FormControl(ordenRawValue.modo),
      estado: new FormControl(ordenRawValue.estado),
    });
  }

  getOrden(form: OrdenFormGroup): IOrden | NewOrden {
    return form.getRawValue() as IOrden | NewOrden;
  }

  resetForm(form: OrdenFormGroup, orden: OrdenFormGroupInput): void {
    const ordenRawValue = { ...this.getFormDefaults(), ...orden };
    form.reset(
      {
        ...ordenRawValue,
        id: { value: ordenRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): OrdenFormDefaults {
    return {
      id: null,
    };
  }
}
