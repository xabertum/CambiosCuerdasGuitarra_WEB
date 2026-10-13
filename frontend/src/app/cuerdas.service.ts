import { HttpClient } from '@angular/common/http';
import { Injectable, PendingTasks, inject, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';

import { CuerdaSet, CuerdaSetRequest } from './cuerdas.model';

@Injectable({ providedIn: 'root' })
export class CuerdasService {
  private readonly http = inject(HttpClient);
  private readonly pendingTasks = inject(PendingTasks);
  private readonly api = '/api/cuerdas';

  readonly cuerdas = signal<CuerdaSet[]>([]);
  readonly cargando = signal(true);
  readonly error = signal<string | null>(null);

  /** Registered as a pending task so the app (and tests) only become stable once loaded. */
  cargar(): void {
    this.pendingTasks.run(() => this.cargarLista());
  }

  private async cargarLista(): Promise<void> {
    this.cargando.set(true);
    try {
      this.cuerdas.set(await firstValueFrom(this.http.get<CuerdaSet[]>(this.api)));
      this.error.set(null);
    } catch {
      this.error.set('No se pudo conectar con el servidor.');
    } finally {
      this.cargando.set(false);
    }
  }

  async guardar(
    id: number | null,
    datos: CuerdaSetRequest,
    imagen: { archivo: File | null; quitar: boolean },
  ): Promise<CuerdaSet> {
    let set = await firstValueFrom(
      id == null
        ? this.http.post<CuerdaSet>(this.api, datos)
        : this.http.put<CuerdaSet>(`${this.api}/${id}`, datos),
    );
    if (imagen.archivo) {
      const form = new FormData();
      form.append('archivo', imagen.archivo);
      set = await firstValueFrom(this.http.put<CuerdaSet>(`${this.api}/${set.id}/imagen`, form));
    } else if (imagen.quitar && set.imagenUrl) {
      set = await firstValueFrom(this.http.delete<CuerdaSet>(`${this.api}/${set.id}/imagen`));
    }
    this.reemplazar(set);
    return set;
  }

  async marcarCambioHoy(set: CuerdaSet, hoy: string): Promise<void> {
    const { id, imagenUrl, ...datos } = set;
    const actualizado = await firstValueFrom(
      this.http.put<CuerdaSet>(`${this.api}/${id}`, { ...datos, fechaCambio: hoy }),
    );
    this.reemplazar(actualizado);
  }

  async borrar(id: number): Promise<void> {
    await firstValueFrom(this.http.delete<void>(`${this.api}/${id}`));
    this.cuerdas.update((lista) => lista.filter((c) => c.id !== id));
  }

  private reemplazar(set: CuerdaSet): void {
    this.cuerdas.update((lista) =>
      lista.some((c) => c.id === set.id) ? lista.map((c) => (c.id === set.id ? set : c)) : [set, ...lista],
    );
  }
}
