import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { App } from './app';
import { CuerdaSet } from './cuerdas.model';

const datos: CuerdaSet[] = [
  { id: 1, tipo: 'ELECTRICA', descripcion: 'Ernie Ball 2223', cantidad: 1, fechaCompra: '2020-08-08', fechaCambio: null, notas: null, imagenUrl: null },
  { id: 2, tipo: 'CLASICA', descripcion: 'Hannabach 815 SLT', cantidad: 2, fechaCompra: '2026-10-13', fechaCambio: null, notas: null, imagenUrl: null },
];

describe('App', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();
  });

  it('muestra los sets cargados, el más reciente primero', async () => {
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();
    TestBed.inject(HttpTestingController).expectOne('/api/cuerdas').flush(datos);
    await fixture.whenStable();

    const titulos = [...(fixture.nativeElement as HTMLElement).querySelectorAll('.card-title')].map((e) => e.textContent?.trim());
    expect(titulos).toEqual(['Hannabach 815 SLT', 'Ernie Ball 2223']);
  });
});
