import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { OrdenDetailComponent } from './orden-detail.component';

describe('Orden Management Detail Component', () => {
  let comp: OrdenDetailComponent;
  let fixture: ComponentFixture<OrdenDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [OrdenDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ orden: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(OrdenDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(OrdenDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load orden on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.orden).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
