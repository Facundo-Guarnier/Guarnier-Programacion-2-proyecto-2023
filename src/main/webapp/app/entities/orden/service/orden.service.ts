import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IOrden, NewOrden } from '../orden.model';

export type PartialUpdateOrden = Partial<IOrden> & Pick<IOrden, 'id'>;

type RestOf<T extends IOrden | NewOrden> = Omit<T, 'fechaOperacion'> & {
  fechaOperacion?: string | null;
};

export type RestOrden = RestOf<IOrden>;

export type NewRestOrden = RestOf<NewOrden>;

export type PartialUpdateRestOrden = RestOf<PartialUpdateOrden>;

export type EntityResponseType = HttpResponse<IOrden>;
export type EntityArrayResponseType = HttpResponse<IOrden[]>;

@Injectable({ providedIn: 'root' })
export class OrdenService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/ordens');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(orden: NewOrden): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(orden);
    return this.http.post<RestOrden>(this.resourceUrl, copy, { observe: 'response' }).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(orden: IOrden): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(orden);
    return this.http
      .put<RestOrden>(`${this.resourceUrl}/${this.getOrdenIdentifier(orden)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(orden: PartialUpdateOrden): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(orden);
    return this.http
      .patch<RestOrden>(`${this.resourceUrl}/${this.getOrdenIdentifier(orden)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestOrden>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestOrden[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getOrdenIdentifier(orden: Pick<IOrden, 'id'>): number {
    return orden.id;
  }

  compareOrden(o1: Pick<IOrden, 'id'> | null, o2: Pick<IOrden, 'id'> | null): boolean {
    return o1 && o2 ? this.getOrdenIdentifier(o1) === this.getOrdenIdentifier(o2) : o1 === o2;
  }

  addOrdenToCollectionIfMissing<Type extends Pick<IOrden, 'id'>>(
    ordenCollection: Type[],
    ...ordensToCheck: (Type | null | undefined)[]
  ): Type[] {
    const ordens: Type[] = ordensToCheck.filter(isPresent);
    if (ordens.length > 0) {
      const ordenCollectionIdentifiers = ordenCollection.map(ordenItem => this.getOrdenIdentifier(ordenItem)!);
      const ordensToAdd = ordens.filter(ordenItem => {
        const ordenIdentifier = this.getOrdenIdentifier(ordenItem);
        if (ordenCollectionIdentifiers.includes(ordenIdentifier)) {
          return false;
        }
        ordenCollectionIdentifiers.push(ordenIdentifier);
        return true;
      });
      return [...ordensToAdd, ...ordenCollection];
    }
    return ordenCollection;
  }

  protected convertDateFromClient<T extends IOrden | NewOrden | PartialUpdateOrden>(orden: T): RestOf<T> {
    return {
      ...orden,
      fechaOperacion: orden.fechaOperacion?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restOrden: RestOrden): IOrden {
    return {
      ...restOrden,
      fechaOperacion: restOrden.fechaOperacion ? dayjs(restOrden.fechaOperacion) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestOrden>): HttpResponse<IOrden> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestOrden[]>): HttpResponse<IOrden[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
