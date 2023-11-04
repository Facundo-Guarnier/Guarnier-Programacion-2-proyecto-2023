import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { OrdenService } from '../service/orden.service';

import { OrdenComponent } from './orden.component';

describe('Orden Management Component', () => {
  let comp: OrdenComponent;
  let fixture: ComponentFixture<OrdenComponent>;
  let service: OrdenService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'orden', component: OrdenComponent }]), HttpClientTestingModule],
      declarations: [OrdenComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
              })
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(OrdenComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(OrdenComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(OrdenService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.ordens?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to ordenService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getOrdenIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getOrdenIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
