import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { CuerdaSet } from '../cuerdas.model';
import { CuerdaForm } from './cuerda-form';

const set: CuerdaSet = {
  id: 7, tipo: 'ELECTRICA', descripcion: 'Orphee RX15 9-42', cantidad: 5,
  fechaCompra: '2025-08-25', fechaCambio: '2026-08-25', notas: null, imagenUrl: null,
};

describe('CuerdaForm', () => {
  beforeEach(() => {
    // jsdom does not implement modal dialogs.
    HTMLDialogElement.prototype.showModal ??= function (this: HTMLDialogElement) { this.open = true; };
    HTMLDialogElement.prototype.close ??= function (this: HTMLDialogElement) { this.open = false; };
    TestBed.configureTestingModule({
      imports: [CuerdaForm],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
  });

  it('permite quitar la fecha de cambio y guardar', async () => {
    const fixture = TestBed.createComponent(CuerdaForm);
    await fixture.whenStable();
    fixture.componentInstance.abrir(set);
    await fixture.whenStable();

    const el = fixture.nativeElement as HTMLElement;
    el.querySelectorAll<HTMLButtonElement>('[aria-label="Quitar fecha"]')[1].click();
    await fixture.whenStable();
    el.querySelector<HTMLButtonElement>('button[type=submit]')!.click();

    const req = TestBed.inject(HttpTestingController).expectOne('/api/cuerdas/7');
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toMatchObject({ fechaCompra: '2025-08-25', fechaCambio: null });
  });
});
