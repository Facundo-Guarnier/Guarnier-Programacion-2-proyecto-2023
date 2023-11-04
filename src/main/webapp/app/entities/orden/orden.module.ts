import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { OrdenComponent } from './list/orden.component';
import { OrdenDetailComponent } from './detail/orden-detail.component';
import { OrdenUpdateComponent } from './update/orden-update.component';
import { OrdenDeleteDialogComponent } from './delete/orden-delete-dialog.component';
import { OrdenRoutingModule } from './route/orden-routing.module';

@NgModule({
  imports: [SharedModule, OrdenRoutingModule],
  declarations: [OrdenComponent, OrdenDetailComponent, OrdenUpdateComponent, OrdenDeleteDialogComponent],
})
export class OrdenModule {}
