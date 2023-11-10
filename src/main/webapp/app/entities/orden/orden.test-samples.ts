import dayjs from 'dayjs/esm';

import { IOrden, NewOrden } from './orden.model';

export const sampleWithRequiredData: IOrden = {
  id: 34750,
};

export const sampleWithPartialData: IOrden = {
  id: 49117,
  accionId: 8681,
  accion: 'deposit Hecho Guantes',
  operacion: 'content customized País',
  modo: 'en Etiopía Extremadura',
  descripcion: 'Pelota Estados',
  clienteNombre: 'Madrid Diseñador digital',
  cliente: 62295,
  fechaOperacion: dayjs('2023-11-02T05:42'),
};

export const sampleWithFullData: IOrden = {
  id: 49172,
  accionId: 15154,
  accion: 'Granito Pizza Re-implementado',
  operacion: 'mejora Avon AI',
  precio: 95045,
  cantidad: 88052,
  modo: 'Ladrillo Toallas',
  estado: 33476,
  descripcion: 'Account Violeta evolve',
  clienteNombre: 'calculate withdrawal Personal',
  cliente: 4890,
  fechaOperacion: dayjs('2023-11-02T13:35'),
};

export const sampleWithNewData: NewOrden = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
