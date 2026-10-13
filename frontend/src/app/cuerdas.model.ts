export type TipoGuitarra = 'ELECTRICA' | 'ACUSTICA' | 'CLASICA';

export const TIPOS: { valor: TipoGuitarra; etiqueta: string }[] = [
  { valor: 'ELECTRICA', etiqueta: 'Eléctrica' },
  { valor: 'ACUSTICA', etiqueta: 'Acústica' },
  { valor: 'CLASICA', etiqueta: 'Clásica' },
];

export const ETIQUETA_TIPO: Record<TipoGuitarra, string> = {
  ELECTRICA: 'Eléctrica',
  ACUSTICA: 'Acústica',
  CLASICA: 'Clásica',
};

export interface CuerdaSet {
  id: number;
  tipo: TipoGuitarra;
  descripcion: string;
  cantidad: number;
  /** ISO yyyy-MM-dd */
  fechaCompra: string | null;
  /** ISO yyyy-MM-dd */
  fechaCambio: string | null;
  notas: string | null;
  imagenUrl: string | null;
}

export type CuerdaSetRequest = Omit<CuerdaSet, 'id' | 'imagenUrl'>;

/** Most recent activity date (purchase or change), used for sorting. */
export function fechaReciente(c: CuerdaSet): string {
  return [c.fechaCompra, c.fechaCambio].filter((f): f is string => !!f).sort().at(-1) ?? '';
}

export function hoyIso(): string {
  const d = new Date();
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
}

export function diasDesde(iso: string): number {
  const [y, m, d] = iso.split('-').map(Number);
  const desde = Date.UTC(y, m - 1, d);
  const hoy = new Date();
  return Math.round((Date.UTC(hoy.getFullYear(), hoy.getMonth(), hoy.getDate()) - desde) / 86_400_000);
}
