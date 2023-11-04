import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { OrdenFormService, OrdenFormGroup } from './orden-form.service';
import { IOrden } from '../orden.model';
import { OrdenService } from '../service/orden.service';

@Component({
  selector: 'jhi-orden-update',
  templateUrl: './orden-update.component.html',
})
export class OrdenUpdateComponent implements OnInit {
  isSaving = false;
  orden: IOrden | null = null;

  editForm: OrdenFormGroup = this.ordenFormService.createOrdenFormGroup();

  constructor(
    protected ordenService: OrdenService,
    protected ordenFormService: OrdenFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ orden }) => {
      this.orden = orden;
      if (orden) {
        this.updateForm(orden);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const orden = this.ordenFormService.getOrden(this.editForm);
    if (orden.id !== null) {
      this.subscribeToSaveResponse(this.ordenService.update(orden));
    } else {
      this.subscribeToSaveResponse(this.ordenService.create(orden));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IOrden>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(orden: IOrden): void {
    this.orden = orden;
    this.ordenFormService.resetForm(this.editForm, orden);
  }
}
