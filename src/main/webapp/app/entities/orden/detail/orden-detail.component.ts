import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IOrden } from '../orden.model';

@Component({
  selector: 'jhi-orden-detail',
  templateUrl: './orden-detail.component.html',
})
export class OrdenDetailComponent implements OnInit {
  orden: IOrden | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ orden }) => {
      this.orden = orden;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
