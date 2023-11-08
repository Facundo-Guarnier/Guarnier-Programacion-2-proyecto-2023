export interface IOrden {
  id: number;
  accionId?: number | null;
  accion?: string | null;
  operacion?: string | null;
  precio?: number | null;
  cantidad?: number | null;
  fechaOperacion?: string | null;
  modo?: string | null;
  estado?: number | null;
  descripcion?: string | null;
  clienteNombre?: string | null;
  clienteId?: number | null;
}

export type NewOrden = Omit<IOrden, 'id'> & { id: null };
