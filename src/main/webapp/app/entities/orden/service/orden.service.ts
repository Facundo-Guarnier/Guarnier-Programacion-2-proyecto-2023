import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IOrden, NewOrden } from '../orden.model';

export type PartialUpdateOrden = Partial<IOrden> & Pick<IOrden, 'id'>;

export type EntityResponseType = HttpResponse<IOrden>;
export type EntityArrayResponseType = HttpResponse<IOrden[]>;

@Injectable({ providedIn: 'root' })
export class OrdenService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/ordens');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(orden: NewOrden): Observable<EntityResponseType> {
    return this.http.post<IOrden>(this.resourceUrl, orden, { observe: 'response' });
  }

  update(orden: IOrden): Observable<EntityResponseType> {
    return this.http.put<IOrden>(`${this.resourceUrl}/${this.getOrdenIdentifier(orden)}`, orden, { observe: 'response' });
  }

  partialUpdate(orden: PartialUpdateOrden): Observable<EntityResponseType> {
    return this.http.patch<IOrden>(`${this.resourceUrl}/${this.getOrdenIdentifier(orden)}`, orden, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IOrden>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IOrden[]>(this.resourceUrl, { params: options, observe: 'response' });
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
}
