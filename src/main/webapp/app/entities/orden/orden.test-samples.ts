import { IOrden, NewOrden } from './orden.model';

export const sampleWithRequiredData: IOrden = {
  id: 34750,
};

export const sampleWithPartialData: IOrden = {
  id: 80323,
  cliente: 60920,
  accionId: 49117,
  accion: 'SMTP',
  cantidad: 98359,
  modo: 'Azerbaijanian',
  estado: 'Guantes',
};

export const sampleWithFullData: IOrden = {
  id: 72787,
  cliente: 43977,
  accionId: 42588,
  accion: 'customized País SSL',
  operacion: 'Etiopía Extremadura',
  precio: 52515,
  cantidad: 17245,
  fechaOperacion: 'connecting',
  modo: 'generating',
  estado: 'Diseñador digital Loan',
};

export const sampleWithNewData: NewOrden = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
