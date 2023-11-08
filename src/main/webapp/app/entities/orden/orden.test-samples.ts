import { IOrden, NewOrden } from './orden.model';

export const sampleWithRequiredData: IOrden = {
  id: 34750,
};

export const sampleWithPartialData: IOrden = {
  id: 49117,
  accionId: 8681,
  accion: 'deposit Hecho Guantes',
  operacion: 'content customized País',
  fechaOperacion: 'en Etiopía Extremadura',
  estado: 52515,
  descripcion: 'a',
  clienteNombre: 'Guapa state Colombia',
  clienteId: 17469,
};

export const sampleWithFullData: IOrden = {
  id: 45444,
  accionId: 62295,
  accion: 'Avon Granito',
  operacion: 'RSS',
  precio: 76870,
  cantidad: 76864,
  fechaOperacion: 'mejora Avon AI',
  modo: 'ADP haptic Cataluña',
  estado: 76479,
  descripcion: 'Blanco Coordinador',
  clienteNombre: 'fritas',
  clienteId: 63825,
};

export const sampleWithNewData: NewOrden = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
