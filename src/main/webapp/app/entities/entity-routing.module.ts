import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'orden',
        data: { pageTitle: 'prog2App.orden.home.title' },
        loadChildren: () => import('./orden/orden.module').then(m => m.OrdenModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
