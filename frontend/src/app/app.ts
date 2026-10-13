import { DatePipe } from '@angular/common';
import { Component, computed, inject, OnInit, signal, viewChild } from '@angular/core';

import { CuerdaForm } from './cuerda-form/cuerda-form';
import { CuerdasService } from './cuerdas.service';
import { CuerdaSet, ETIQUETA_TIPO, TIPOS, TipoGuitarra, diasDesde, fechaReciente, hoyIso } from './cuerdas.model';

type Orden = 'reciente' | 'antiguo' | 'nombre';
type Vista = 'tarjetas' | 'tabla';

@Component({
  selector: 'app-root',
  imports: [DatePipe, CuerdaForm],
  templateUrl: './app.html',
})
export class App implements OnInit {
  protected readonly service = inject(CuerdasService);
  private readonly formulario = viewChild.required(CuerdaForm);

  protected readonly tipos = TIPOS;
  protected readonly etiqueta = ETIQUETA_TIPO;
  protected readonly diasDesde = diasDesde;

  protected readonly filtroTipo = signal<TipoGuitarra | null>(null);
  protected readonly busqueda = signal('');
  protected readonly orden = signal<Orden>('reciente');
  protected readonly vista = signal<Vista>('tarjetas');
  protected readonly ampliada = signal<CuerdaSet | null>(null);

  protected readonly visibles = computed(() => {
    const tipo = this.filtroTipo();
    const q = this.busqueda().trim().toLowerCase();
    const lista = this.service
      .cuerdas()
      .filter((c) => !tipo || c.tipo === tipo)
      .filter((c) => !q || `${c.descripcion} ${c.notas ?? ''}`.toLowerCase().includes(q));
    switch (this.orden()) {
      case 'nombre':
        return lista.sort((a, b) => a.descripcion.localeCompare(b.descripcion, 'es'));
      case 'antiguo':
        return lista.sort((a, b) => fechaReciente(a).localeCompare(fechaReciente(b)));
      default:
        return lista.sort((a, b) => fechaReciente(b).localeCompare(fechaReciente(a)));
    }
  });

  protected readonly conteoPorTipo = computed(() => {
    const conteo: Record<TipoGuitarra, number> = { ELECTRICA: 0, ACUSTICA: 0, CLASICA: 0 };
    for (const c of this.service.cuerdas()) conteo[c.tipo]++;
    return conteo;
  });

  /** Summary for the current type filter. */
  protected readonly resumen = computed(() => {
    const tipo = this.filtroTipo();
    const lista = this.service.cuerdas().filter((c) => !tipo || c.tipo === tipo);
    const ultima = (campo: 'fechaCompra' | 'fechaCambio') =>
      lista
        .filter((c) => c[campo])
        .sort((a, b) => b[campo]!.localeCompare(a[campo]!))
        .at(0);
    return {
      sets: lista.length,
      packs: lista.reduce((n, c) => n + c.cantidad, 0),
      ultimaCompra: ultima('fechaCompra'),
      ultimoCambio: ultima('fechaCambio'),
    };
  });

  ngOnInit(): void {
    this.service.cargar();
  }

  protected nuevo(): void {
    this.formulario().abrir(null, this.filtroTipo() ?? undefined);
  }

  protected editar(set: CuerdaSet): void {
    this.formulario().abrir(set);
  }

  protected cambioHoy(set: CuerdaSet): void {
    this.service.marcarCambioHoy(set, hoyIso());
  }

  protected textoDias(iso: string): string {
    const d = diasDesde(iso);
    if (d <= 0) return 'hoy';
    if (d === 1) return 'ayer';
    if (d < 60) return `hace ${d} días`;
    if (d < 730) return `hace ${Math.round(d / 30.44)} meses`;
    return `hace ${Math.floor(d / 365.25)} años`;
  }
}
