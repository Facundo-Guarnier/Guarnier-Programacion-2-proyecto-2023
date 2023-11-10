import dayjs from 'dayjs/esm';

export interface IOrden {
  id: number;
  accionId?: number | null;
  accion?: string | null;
  operacion?: string | null;
  precio?: number | null;
  cantidad?: number | null;
  modo?: string | null;
  estado?: number | null;
  descripcion?: string | null;
  clienteNombre?: string | null;
  cliente?: number | null;
  fechaOperacion?: dayjs.Dayjs | null;
}

export type NewOrden = Omit<IOrden, 'id'> & { id: null };
