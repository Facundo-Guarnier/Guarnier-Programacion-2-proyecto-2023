import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
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

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IOrden | NewOrden> = Omit<T, 'fechaOperacion'> & {
  fechaOperacion?: string | null;
};

type OrdenFormRawValue = FormValueOf<IOrden>;

type NewOrdenFormRawValue = FormValueOf<NewOrden>;

type OrdenFormDefaults = Pick<NewOrden, 'id' | 'fechaOperacion'>;

type OrdenFormGroupContent = {
  id: FormControl<OrdenFormRawValue['id'] | NewOrden['id']>;
  accionId: FormControl<OrdenFormRawValue['accionId']>;
  accion: FormControl<OrdenFormRawValue['accion']>;
  operacion: FormControl<OrdenFormRawValue['operacion']>;
  precio: FormControl<OrdenFormRawValue['precio']>;
  cantidad: FormControl<OrdenFormRawValue['cantidad']>;
  modo: FormControl<OrdenFormRawValue['modo']>;
  estado: FormControl<OrdenFormRawValue['estado']>;
  descripcion: FormControl<OrdenFormRawValue['descripcion']>;
  clienteNombre: FormControl<OrdenFormRawValue['clienteNombre']>;
  cliente: FormControl<OrdenFormRawValue['cliente']>;
  fechaOperacion: FormControl<OrdenFormRawValue['fechaOperacion']>;
};

export type OrdenFormGroup = FormGroup<OrdenFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class OrdenFormService {
  createOrdenFormGroup(orden: OrdenFormGroupInput = { id: null }): OrdenFormGroup {
    const ordenRawValue = this.convertOrdenToOrdenRawValue({
      ...this.getFormDefaults(),
      ...orden,
    });
    return new FormGroup<OrdenFormGroupContent>({
      id: new FormControl(
        { value: ordenRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      accionId: new FormControl(ordenRawValue.accionId),
      accion: new FormControl(ordenRawValue.accion),
      operacion: new FormControl(ordenRawValue.operacion),
      precio: new FormControl(ordenRawValue.precio),
      cantidad: new FormControl(ordenRawValue.cantidad),
      modo: new FormControl(ordenRawValue.modo),
      estado: new FormControl(ordenRawValue.estado),
      descripcion: new FormControl(ordenRawValue.descripcion),
      clienteNombre: new FormControl(ordenRawValue.clienteNombre),
      cliente: new FormControl(ordenRawValue.cliente),
      fechaOperacion: new FormControl(ordenRawValue.fechaOperacion),
    });
  }

  getOrden(form: OrdenFormGroup): IOrden | NewOrden {
    return this.convertOrdenRawValueToOrden(form.getRawValue() as OrdenFormRawValue | NewOrdenFormRawValue);
  }

  resetForm(form: OrdenFormGroup, orden: OrdenFormGroupInput): void {
    const ordenRawValue = this.convertOrdenToOrdenRawValue({ ...this.getFormDefaults(), ...orden });
    form.reset(
      {
        ...ordenRawValue,
        id: { value: ordenRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): OrdenFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      fechaOperacion: currentTime,
    };
  }

  private convertOrdenRawValueToOrden(rawOrden: OrdenFormRawValue | NewOrdenFormRawValue): IOrden | NewOrden {
    return {
      ...rawOrden,
      fechaOperacion: dayjs(rawOrden.fechaOperacion, DATE_TIME_FORMAT),
    };
  }

  private convertOrdenToOrdenRawValue(
    orden: IOrden | (Partial<NewOrden> & OrdenFormDefaults)
  ): OrdenFormRawValue | PartialWithRequiredKeyOf<NewOrdenFormRawValue> {
    return {
      ...orden,
      fechaOperacion: orden.fechaOperacion ? orden.fechaOperacion.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
