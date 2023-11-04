export interface IOrden {
  id: number;
  cliente?: number | null;
  accionId?: number | null;
  accion?: string | null;
  operacion?: string | null;
  precio?: number | null;
  cantidad?: number | null;
  fechaOperacion?: string | null;
  modo?: string | null;
  estado?: string | null;
}

export type NewOrden = Omit<IOrden, 'id'> & { id: null };
