import { Component, ElementRef, inject, signal, viewChild } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { CuerdasService } from '../cuerdas.service';
import { CuerdaSet, CuerdaSetRequest, TIPOS, TipoGuitarra, hoyIso } from '../cuerdas.model';

/** Dialog to create, edit or delete a set of strings. Open it with {@link abrir}. */
@Component({
  selector: 'app-cuerda-form',
  imports: [ReactiveFormsModule],
  templateUrl: './cuerda-form.html',
})
export class CuerdaForm {
  private readonly service = inject(CuerdasService);
  private readonly dialog = viewChild.required<ElementRef<HTMLDialogElement>>('dialog');

  protected readonly tipos = TIPOS;
  protected readonly editando = signal<CuerdaSet | null>(null);
  protected readonly preview = signal<string | null>(null);
  protected readonly guardando = signal(false);
  protected readonly confirmarBorrado = signal(false);
  protected readonly error = signal<string | null>(null);

  private archivo: File | null = null;
  private quitarImagen = false;

  protected readonly form = inject(NonNullableFormBuilder).group({
    tipo: ['ELECTRICA' as TipoGuitarra, Validators.required],
    descripcion: ['', [Validators.required, Validators.maxLength(500)]],
    cantidad: [1, [Validators.required, Validators.min(1)]],
    fechaCompra: [''],
    fechaCambio: [''],
    notas: ['', Validators.maxLength(2000)],
  });

  abrir(set: CuerdaSet | null, tipo?: TipoGuitarra): void {
    this.editando.set(set);
    this.archivo = null;
    this.quitarImagen = false;
    this.preview.set(set?.imagenUrl ?? null);
    this.confirmarBorrado.set(false);
    this.error.set(null);
    this.form.reset({
      tipo: set?.tipo ?? tipo ?? 'ELECTRICA',
      descripcion: set?.descripcion ?? '',
      cantidad: set?.cantidad ?? 1,
      fechaCompra: set ? (set.fechaCompra ?? '') : hoyIso(),
      fechaCambio: set?.fechaCambio ?? '',
      notas: set?.notas ?? '',
    });
    this.dialog().nativeElement.showModal();
  }

  protected cerrar(): void {
    this.dialog().nativeElement.close();
  }

  /**
   * Closes when the click lands on the backdrop (the dialog element itself).
   * Must return void: a handler returning false makes Angular call preventDefault(),
   * which would cancel every click inside the dialog (form submit, file picker…).
   */
  protected cerrarSiFondo(event: MouseEvent): void {
    if (event.target === this.dialog().nativeElement) {
      this.cerrar();
    }
  }

  protected hoy(campo: 'fechaCompra' | 'fechaCambio'): void {
    this.form.controls[campo].setValue(hoyIso());
  }

  protected quitarFecha(campo: 'fechaCompra' | 'fechaCambio'): void {
    this.form.controls[campo].setValue('');
  }

  protected elegirImagen(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file) return;
    this.archivo = file;
    this.quitarImagen = false;
    const reader = new FileReader();
    reader.onload = () => this.preview.set(reader.result as string);
    reader.readAsDataURL(file);
  }

  protected eliminarImagen(input: HTMLInputElement): void {
    this.archivo = null;
    this.quitarImagen = true;
    input.value = '';
    this.preview.set(null);
  }

  protected async guardar(): Promise<void> {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const v = this.form.getRawValue();
    const datos: CuerdaSetRequest = {
      ...v,
      fechaCompra: v.fechaCompra || null,
      fechaCambio: v.fechaCambio || null,
      notas: v.notas.trim() || null,
    };
    this.guardando.set(true);
    try {
      await this.service.guardar(this.editando()?.id ?? null, datos, {
        archivo: this.archivo,
        quitar: this.quitarImagen,
      });
      this.cerrar();
    } catch {
      this.error.set('No se pudo guardar. Revisa los datos e inténtalo de nuevo.');
    } finally {
      this.guardando.set(false);
    }
  }

  protected async borrar(): Promise<void> {
    const set = this.editando();
    if (!set) return;
    if (!this.confirmarBorrado()) {
      this.confirmarBorrado.set(true);
      return;
    }
    this.guardando.set(true);
    try {
      await this.service.borrar(set.id);
      this.cerrar();
    } catch {
      this.error.set('No se pudo borrar.');
    } finally {
      this.guardando.set(false);
    }
  }
}
