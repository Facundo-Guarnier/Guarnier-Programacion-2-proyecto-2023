import { IOrden, NewOrden } from './orden.model';

export const sampleWithRequiredData: IOrden = {
  id: 34750,
};

export const sampleWithPartialData: IOrden = {
  id: 60920,
  cliente: 49117,
  accionId: 8681,
  accion: 'deposit Hecho Guantes',
  cantidad: 72787,
  modo: 'infomediaries Futuro',
  estado: 93222,
  descripcion: 'SSL',
};

export const sampleWithFullData: IOrden = {
  id: 41732,
  cliente: 54975,
  accionId: 30209,
  accion: 'Extremadura Islas connecting',
  operacion: 'generating',
  precio: 90644,
  cantidad: 98792,
  fechaOperacion: 'Morado Paseo',
  modo: 'Patatas Azerbayán',
  estado: 87181,
  descripcion: 'Re-implementado card',
};

export const sampleWithNewData: NewOrden = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
