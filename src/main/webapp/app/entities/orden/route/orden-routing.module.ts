import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { OrdenComponent } from '../list/orden.component';
import { OrdenDetailComponent } from '../detail/orden-detail.component';
import { OrdenUpdateComponent } from '../update/orden-update.component';
import { OrdenRoutingResolveService } from './orden-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const ordenRoute: Routes = [
  {
    path: '',
    component: OrdenComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: OrdenDetailComponent,
    resolve: {
      orden: OrdenRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: OrdenUpdateComponent,
    resolve: {
      orden: OrdenRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: OrdenUpdateComponent,
    resolve: {
      orden: OrdenRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(ordenRoute)],
  exports: [RouterModule],
})
export class OrdenRoutingModule {}
